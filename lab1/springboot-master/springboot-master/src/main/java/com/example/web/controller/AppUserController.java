package com.example.web.controller;

import com.example.web.dto.AppUserDto;
import com.example.web.entity.ImportHistory;
import com.example.web.repository.ImportHistoryRepository;
import com.example.web.service.MinIOService;
import com.example.web.tools.UpdateNotificationService;
import com.example.web.tools.UpdateResponse;
import com.example.web.tools.dto.ResultDto;
import com.example.web.dto.query.AppUserPagedInput;
import com.example.web.service.AppUserService;
import com.example.web.tools.BaseContext;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;
import com.example.web.tools.dto.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * 用户控制器
 */
@RestController()
@RequestMapping("/User")
public class AppUserController {
    @Autowired()
    private AppUserService AppUserService;

    private final MinIOService minIOService;

    @Autowired
    public AppUserController(MinIOService minIOService) {
        this.minIOService = minIOService;
    }

    @Autowired
    private ImportHistoryRepository importHistoryRepository;

    /**
     * 用户分页查询接口
     * 包含分页信息和查询结果
     * 分页结果，包含用户数据列表
     */
    @RequestMapping(value = "/List", method = RequestMethod.POST)
    @SneakyThrows
    public PagedResult<AppUserDto> List(@RequestBody AppUserPagedInput input) {
//        // 打印请求参数
//        System.out.println("请求参数: " + input);

        // 调用服务
        PagedResult<AppUserDto> result = AppUserService.List(input);

//        // 验证是否成功读取数据
//        boolean success = result != null && result.getItems() != null && !result.getItems().isEmpty();
//        System.out.println("读取数据成功: " + success + ", 条数: " + (result.getItems() != null ? result.getItems().size() : 0));

        return result;
    }

    /**
     * 用户创建或则修改接口
     */
    @RequestMapping(value = "/CreateOrEdit", method = RequestMethod.POST)
    public AppUserDto CreateOrEdit(@RequestBody AppUserDto input) {
        return AppUserService.CreateOrEdit(input);
    }

    /**
     * 用户删除
     */
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public void Delete(@RequestBody IdInput input) {
        AppUserService.Delete(input);
    }

    /**
     * 用户批量删除
     */
    @RequestMapping(value = "/BatchDelete", method = RequestMethod.POST)
    public void BatchDelete(@RequestBody IdsInput input) {
        AppUserService.BatchDelete(input);
    }

    /**
     * 查询单个对用户
     */
    @RequestMapping(value = "/Get", method = RequestMethod.POST)
    public AppUserDto Get(@RequestBody AppUserPagedInput input) {
        return AppUserService.Get(input);
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/SignIn", method = RequestMethod.POST)
    public ResponseData<String> SignIn(@RequestBody AppUserDto input, HttpServletRequest request) {
        String token = AppUserService.SignIn(input);
        return ResponseData.GetResponseDataInstance(token, "Login successful", true);
    }

    /**
     * 获取用户信息
     */
    @SneakyThrows
    @RequestMapping(value = "/GetByToken", method = RequestMethod.POST)
    public AppUserDto GetByToken(@RequestHeader("Authorization") String token) {

        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        AppUserPagedInput queryInput = new AppUserPagedInput();
        queryInput.setId(userId);
        AppUserDto AppUserDto = AppUserService.Get(queryInput);

        return AppUserDto;
    }

    /**
     * 用户注册接口
     */
    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    public AppUserDto Register(@RequestBody AppUserDto input) throws Exception {
        return AppUserService.Register(input);
    }

    /**
     * 找回密码
     */
    @RequestMapping(value = "/ForgetPassword", method = RequestMethod.POST)
    public void ForgetPassword(@RequestBody AppUserDto input) throws Exception {
        AppUserService.ForgetPassword(input);
    }

    @RequestMapping(value = "/CalculateAverageHeight", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Double> calculateAverageHeight() {
        double avgHeight = AppUserService.calculateAverageHeight();
        return ResponseEntity.ok(avgHeight);
    }

    // 获取 hairColor 的统计数量
    @GetMapping("/count-haircolor")
    public Map<String, Long> getHairColorStatistics() {
        Map<String, Long> stats = AppUserService.countHairColor();
        System.out.println("Hair Color Stats: " + stats);  // 打印出数据
        return stats;
    }

    // 获取 eyeColor 的统计数量
    @GetMapping("/count-eyecolor")
    public Map<String, Long> getEyeColorStatistics() {
        return AppUserService.countEyeColor();
    }


    @PostMapping("/CalculateEyeColorPercentage")
    public ResultDto<Double> calculateEyeColorPercentage(@RequestBody Map<String, String> input) {
        String eyeColor = input.get("EyeColor");
        if (eyeColor == null || eyeColor.isEmpty()) {
            return ResultDto.ReturnError("Eye color cannot be empty!");
        }

        double percentage = AppUserService.calculateEyeColorPercentage(eyeColor);
        return ResultDto.ReturnData(percentage);
    }

    /**
     * 用户导出
     */
    @RequestMapping(value = "/Export", method = RequestMethod.GET)
    public void Export(@RequestParam String query, HttpServletResponse response) throws IOException {
        AppUserService.Export(query, response);
    }


    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file){
        String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            //先上传 MinIO（第一阶段）
            minIOService.putObject(file, objectName);

            //再导入数据库（第二阶段）
            AppUserService.importExcel(file);

            return ResponseEntity.ok(
                    Map.of("Success", true, "Msg", "文件导入成功并已保存到 MinIO")
            );


        } catch (Exception e) {
            //补偿：如果 MinIO 已上传但 DB 失败
            if (minIOService.exists(objectName)) {
                minIOService.removeObject(objectName);
            }

            return ResponseEntity.internalServerError()
                    .body(Map.of("Success", false, "Msg", e.getMessage()));
        }
    }
    @GetMapping("/importHistory")
    public List<ImportHistory> getImportHistory() {
        return importHistoryRepository.findAll();
    }
}
