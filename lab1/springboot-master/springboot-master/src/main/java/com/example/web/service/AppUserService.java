package com.example.web.service;

import com.example.web.dto.AppUserDto;
import com.example.web.dto.query.AppUserPagedInput;
import com.example.web.entity.AppUser;
import com.example.web.tools.dto.IdInput;
import com.example.web.tools.dto.IdsInput;
import com.example.web.tools.dto.PagedResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppUserService {
    @Transactional
    PagedResult<AppUserDto> List(AppUserPagedInput input);
    AppUserDto CreateOrEdit(AppUserDto input);
    void Delete(IdInput input);
    void BatchDelete(IdsInput input);
    AppUserDto Get(AppUserPagedInput input);
    String SignIn(AppUserDto input);
    AppUserDto Register(AppUserDto input);
    void ForgetPassword(AppUserDto input);

    // PostgreSQL / EclipseLink
    List<AppUser> getUsersByBirth(LocalDateTime birth);
    List<AppUser> getUsersByNameSubstring(String nameSubstring);
    long countUsersByHairColor(String hairColor);
    long countUsersByEyeColor(String eyeColor);
    double calculateEyeColorPercentage(String eyeColor);

    // 统计分析
    double calculateAverageHeight();
    Map<String, Long> countHairColor();
    Map<String, Long> countEyeColor();

    void Export(@RequestParam String query, HttpServletResponse response) throws IOException;
    void importExcel(MultipartFile file)throws IOException;
}
