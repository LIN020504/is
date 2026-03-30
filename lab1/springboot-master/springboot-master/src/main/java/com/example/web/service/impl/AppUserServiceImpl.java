package com.example.web.service.impl;

import com.alibaba.excel.EasyExcel;
import com.example.web.SysConst;
import com.example.web.dto.AppUserDto;
import com.example.web.dto.query.AppUserPagedInput;
import com.example.web.entity.AppUser;
import com.example.web.entity.ImportHistory;
import com.example.web.repository.AppUserRepository;
import com.example.web.repository.ImportHistoryRepository;
import com.example.web.service.AppUserService;
import com.example.web.service.NotifyService;
import com.example.web.tools.Extension;
import com.example.web.tools.JWTUtils;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;
import com.example.web.tools.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AppUserServiceImpl implements AppUserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private ImportHistoryRepository importHistoryRepository;
    //分页查询
    @Override
    public PagedResult<AppUserDto> List(AppUserPagedInput input) {
        StringBuilder jpql = new StringBuilder("SELECT u FROM AppUser u WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (input.getId() != null && input.getId() != 0) {
            jpql.append(" AND u.id = :id");
            params.put("id", input.getId());
        }
        if (Extension.isNotNullOrEmpty(input.getName())) {
            jpql.append(" AND u.name LIKE :name");
            params.put("name", "%" + input.getName() + "%");
        }
        if (Extension.isNotNullOrEmpty(input.getEmail())) {
            jpql.append(" AND u.email = :email");
            params.put("email", input.getEmail());
        }
        if (input.getRoleType() != null) {
            jpql.append(" AND u.roleType = :roleType");
            params.put("roleType", input.getRoleType());
        }
        if (Extension.isNotNullOrEmpty(input.getEyeColor())) {
            jpql.append(" AND u.eyeColor = :eyeColor");
            params.put("eyeColor", input.getEyeColor());
        }
        if (Extension.isNotNullOrEmpty(input.getHairColor())) {
            jpql.append(" AND u.hairColor = :hairColor");
            params.put("hairColor", input.getHairColor());
        }
        if (Extension.isNotNullOrEmpty(input.getHeight())) {
            jpql.append(" AND u.height = :height");
            params.put("height", Double.valueOf(input.getHeight()));
        }
        if (Extension.isNotNullOrEmpty(input.getNationality())) {
            jpql.append(" AND u.nationality = :nationality");
            params.put("nationality", input.getNationality());
        }
        if (Extension.isNotNullOrEmpty(input.getLocation())) {
            jpql.append(" AND u.location = :location");
            params.put("location", input.getLocation());
        }

        // 执行查询
        TypedQuery<AppUser> query = entityManager.createQuery(jpql.toString(), AppUser.class);
        params.forEach(query::setParameter);

        // 分页
        int firstResult = (int) ((input.getPage() - 1) * input.getLimit());
        query.setFirstResult(firstResult);
        query.setMaxResults(input.getLimit().intValue());
        List<AppUser> users = query.getResultList();

        // 总数查询
        String countJpql = "SELECT COUNT(u) " + jpql.substring(jpql.indexOf("FROM"));
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        params.forEach(countQuery::setParameter);
        Long totalCount = countQuery.getSingleResult();

        // DTO 转换
        List<AppUserDto> dtoList = Extension.copyBeanList(users, AppUserDto.class);


        return PagedResult.GetInstance(dtoList, totalCount);
    }


    //创建或编辑
    @SneakyThrows
    @Override
    public AppUserDto CreateOrEdit(AppUserDto input) {
        if (Extension.isNullOrEmpty(input.getUserName())) {
            throw new CustomException("Username cannot be empty");
        }

        //注册场景：应用层唯一性约束
        if (input.getId() == null) {

            // 对 userName 加悲观写锁，防止并发重复注册
            List<AppUser> exists = entityManager.createQuery(
                            "SELECT u FROM AppUser u WHERE u.userName = :userName",
                            AppUser.class
                    )
                    .setParameter("userName", input.getUserName())
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            if (!exists.isEmpty()) {
                throw new CustomException("Username already exists");
            }
        }

        AppUser appUser = input.MapToEntity();

        // 密码处理
        if (!Extension.isNullOrEmpty(input.getPassword())) {
            String hashedPassword = BCrypt.hashpw(input.getPassword(), BCrypt.gensalt());
            appUser.setPassword(hashedPassword);
        }

        try {
            appUserRepository.saveAndFlush(appUser);
            notifyService.notifyUpdate("Update");
        } catch (OptimisticLockException e) {
            throw new CustomException(
                    "Update failed: the user was modified by someone else");
        }

        return appUser.MapToDto();
    }

    //删除
    @Override
    public void Delete(IdInput input) {
        if (input == null || input.getId() == null) {
            throw new RuntimeException("ID cannot be null");
        }

        AppUser user = entityManager.find(AppUser.class, input.getId().intValue());

        if (user == null) {
            throw new RuntimeException("User already deleted");
        }

        entityManager.remove(user);
        entityManager.flush();
        //notifyService.notifyUpdate("Delete");

//        int affected = entityManager.createQuery(
//                        "delete from AppUser u where u.id = :id")
//                .setParameter("id", input.getId())
//                .executeUpdate();
//
//        if (affected == 0) {
//            throw new CustomException("User already deleted");
//        }
    }

    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            AppUser user = entityManager.find(AppUser.class, id.intValue());
            if (user != null) entityManager.remove(user);
        }
        notifyService.notifyUpdate("Update");
    }

    //获取单个用户
    @Override
    public AppUserDto Get(AppUserPagedInput input) {
        if (input.getId() == null) return new AppUserDto();
        AppUser user = entityManager.find(AppUser.class, input.getId().intValue());
        if (user == null) return new AppUserDto();
        try {
            return user.MapToDto();
        } catch (Exception e) {
            return new AppUserDto();
        }
    }

    // 登录
    @Override
    public String SignIn(AppUserDto input) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.userName = :userName", AppUser.class);
        query.setParameter("userName", input.getUserName());
        List<AppUser> users = query.getResultList();

        if (users.isEmpty()) throw new CustomException("Invalid username or password.");

        Map<String, String> claims = new HashMap<>();
        claims.put(SysConst.UserIdClaim, users.get(0).getId().toString());
        claims.put(SysConst.RoleTypeClaim, String.valueOf(users.get(0).getRoleType()));
        return JWTUtils.getToken(claims);
    }

    //注册
    @Override
    public AppUserDto Register(AppUserDto input) {
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(u) FROM AppUser u WHERE u.userName = :userName", Long.class);
        countQuery.setParameter("userName", input.getUserName());
        if (countQuery.getSingleResult() > 0) throw new CustomException("Username already exists");
        return CreateOrEdit(input);
    }

    //忘记密码
    @Override
    public void ForgetPassword(AppUserDto input) {
        TypedQuery<AppUser> query = entityManager.createQuery(
                "SELECT u FROM AppUser u WHERE u.userName = :userName", AppUser.class);
        query.setParameter("userName", input.getUserName());
        List<AppUser> users = query.getResultList();
        if (users.isEmpty()) throw new CustomException("Username not found!");

        AppUser user = users.get(0);
        user.setPassword(BCrypt.hashpw(input.getPassword(), BCrypt.gensalt()));
        entityManager.merge(user);
    }

    //特定统计
    @Override
    public List<AppUser> getUsersByBirth(LocalDateTime birth) {
        return entityManager.createQuery(
                        "SELECT u FROM AppUser u WHERE u.birth = :birth", AppUser.class)
                .setParameter("birth", birth)
                .getResultList();
    }

    @Override
    public List<AppUser> getUsersByNameSubstring(String nameSubstring) {
        return entityManager.createQuery(
                        "SELECT u FROM AppUser u WHERE u.name LIKE :sub", AppUser.class)
                .setParameter("sub", "%" + nameSubstring + "%")
                .getResultList();
    }

    @Override
    public long countUsersByHairColor(String hairColor) {
        Long result = entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u WHERE u.hairColor = :hc", Long.class)
                .setParameter("hc", hairColor)
                .getSingleResult();
        return result;
    }

    @Override
    public long countUsersByEyeColor(String eyeColor) {
        Long result = entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u WHERE u.eyeColor = :ec", Long.class)
                .setParameter("ec", eyeColor)
                .getSingleResult();
        return result;
    }

    @Override
    public double calculateEyeColorPercentage(String eyeColor) {
        long countColor = countUsersByEyeColor(eyeColor);
        long total = entityManager.createQuery("SELECT COUNT(u) FROM AppUser u", Long.class)
                .getSingleResult();
        return total == 0 ? 0.0 : Math.round((countColor * 10000.0 / total)) / 100.0;
    }

    @Override
    public double calculateAverageHeight() {
        Query q = entityManager.createNativeQuery(
                "SELECT AVG(CAST(height AS double precision)) FROM AppUser WHERE height IS NOT NULL"
        );
        Double avg = (Double) q.getSingleResult();
        return avg != null ? avg : 0.0;
    }

    @Override
    public Map<String, Long> countHairColor() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT u.hairColor, COUNT(u) FROM AppUser u GROUP BY u.hairColor", Object[].class)
                .getResultList();
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public Map<String, Long> countEyeColor() {
        List<Object[]> rows = entityManager.createQuery(
                        "SELECT u.eyeColor, COUNT(u) FROM AppUser u GROUP BY u.eyeColor", Object[].class)
                .getResultList();
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    /**
     * 用户导出
     */
    @Override
    public void Export(@RequestParam String query, HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();


        AppUserPagedInput input = mapper.readValue(query, AppUserPagedInput.class);

        List<AppUserDto> items =List(input).getItems();


        //声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        //生成一个表格，设置表格名称为"用户表"
        HSSFSheet sheet = workbook.createSheet("用户表");

        //设置表格列宽度为10个字节
        sheet.setDefaultColumnWidth(10);
        //创建标题的显示样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //创建第一行表头
        HSSFRow headrow = sheet.createRow(0);

        //表头数据
        String[] header = {"UserName","Password","Name","Email","PhoneNumber","RoleType","BirthDay",};
        //遍历添加表头(下面模拟遍历用户，也是同样的操作过程)
        for (int i = 0; i < header.length; i++) {
            //创建一个单元格
            HSSFCell cell = headrow.createCell(i);

            //创建一个内容对象
            HSSFRichTextString text = new HSSFRichTextString(header[i]);

            //将内容对象的文字内容写入到单元格中
            cell.setCellValue(text);
            cell.setCellStyle(headerStyle);
        }


        for(int i=0;i<items.size();i++){

            AppUserDto appUser = items.get(i);

            //创建一行
            HSSFRow row = sheet.createRow(i+1);

            if(appUser.getUserName()!=null) {
                row.createCell(0).setCellValue(new HSSFRichTextString(appUser.getUserName()));
            }
            if(appUser.getPassword()!=null) {
                row.createCell(1).setCellValue(new HSSFRichTextString(appUser.getPassword()));
            }
            if(appUser.getName()!=null) {
                row.createCell(2).setCellValue(new HSSFRichTextString(appUser.getName()));
            }
            if(appUser.getEmail()!=null) {
                row.createCell(3).setCellValue(new HSSFRichTextString(appUser.getEmail()));
            }
            if(appUser.getPhoneNumber()!=null) {
                row.createCell(4).setCellValue(new HSSFRichTextString(appUser.getPhoneNumber()));
            }
            if(appUser.getRoleType()!=null) {
                row.createCell(5).setCellValue(new HSSFRichTextString(appUser.RoleTypeFormat()));
            }
            if(appUser.getBirth()!=null) {
                row.createCell(6).setCellValue(new HSSFRichTextString(Extension.LocalDateTimeConvertString(appUser.getBirth(), null)));
            }
        }

        //准备将Excel的输出流通过response输出到页面下载
        //八进制输出流
        response.setContentType("application/octet-stream");

        //这后面可以设置导出Excel的名称
        response.setHeader("Content-disposition", "attachment;filename="+System.currentTimeMillis()+".xls");

        //刷新缓冲
        response.flushBuffer();

        //workbook将Excel写入到response的输出流中，供页面下载
        workbook.write(response.getOutputStream());
    }

    /**
     * Excel 导入（事务控制）
     */
    @Override
    public void importExcel(MultipartFile file) throws IOException {
        ImportHistory history = new ImportHistory();
        history.getId();
        history.setFileName(file.getOriginalFilename());
        history.setUpload_time(LocalDateTime.now());

        try {
            // 读取 Excel
            List<AppUser> users = EasyExcel.read(file.getInputStream())
                    .head(AppUser.class)
                    .sheet()
                    .doReadSync();

            // 校验（示例：不能为空）
            if (users == null || users.isEmpty()) {
                throw new RuntimeException("Excel 文件中没有数据");
            }

//            if (true) {
//                throw new RuntimeException("Simulated database failure");
//            }

            // 批量保存用户
            appUserRepository.saveAll(users);

            if (true) {
                throw new RuntimeException("Simulated server logic error");
            }

            // 记录成功历史
            history.setStatus("SUCCESS");
            importHistoryRepository.save(history);

        } catch (Exception e) {

            // ❗ 事务会回滚（用户不会插入）
            history.setStatus("FAILED");
            importHistoryRepository.save(history);

            throw e; // 必须抛出，保证事务回滚
        }
    }
}
