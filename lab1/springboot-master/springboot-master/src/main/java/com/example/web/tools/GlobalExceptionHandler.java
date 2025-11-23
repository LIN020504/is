package com.example.web.tools;

import com.example.web.SysConst;
import com.example.web.tools.dto.ResponseData;
import com.example.web.tools.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理切面
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseData bizExceptionHandler(CustomException e) {
        log.error("System exception captured: {}", e.getMessage(), e); // 记录异常堆栈
        ResponseData responseData = new ResponseData();
        responseData.setCode(SysConst.STATUS_500);
        responseData.setMsg(e.getErrorMsg());
        responseData.setSuccess(false);
        return responseData;
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData exceptionHandler(Exception e) {
        log.error("System exception captured: {}", e.getMessage(), e); // 记录异常堆栈
        ResponseData responseData = new ResponseData();
        responseData.setCode(SysConst.STATUS_500);
        responseData.setMsg("System abnormality, please contact the administrator");
        log.error("System exception captured: {}", e.getMessage(), e); // 记录异常堆栈
        responseData.setSuccess(false);
        return responseData;
    }
}
