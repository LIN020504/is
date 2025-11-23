package com.example.web.tools.dto;

import lombok.Data;

/**
 * 统一接口返回格式
 * 用于后端 Controller 向前端返回统一的数据结构
 */
@Data
public class ResultDto<T> {
    private boolean Success;   // 是否成功
    private String Message;    // 提示信息
    private T Data;            // 实际返回的数据

    // ✅ 返回成功结果
    public static <T> ResultDto<T> ReturnData(T data) {
        ResultDto<T> result = new ResultDto<>();
        result.setSuccess(true);
        result.setMessage("Operation successful");
        result.setData(data);
        return result;
    }

    // ✅ 返回错误信息
    public static <T> ResultDto<T> ReturnError(String message) {
        ResultDto<T> result = new ResultDto<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}

