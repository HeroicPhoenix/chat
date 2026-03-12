package com.lvwyh.chat.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器
 *
 * 作用：
 * 1. 统一处理业务异常
 * 2. 统一处理参数校验异常
 * 3. 统一处理系统未知异常
 * 4. 避免默认返回 HTML 错误页，统一返回 JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 统一响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return new ApiResponse<Object>(e.getCode(), false, e.getMessage(), null);
    }

    /**
     * 处理 @RequestBody + @Valid 参数校验异常
     *
     * 例如：
     * - RegisterAO
     * - LoginAO
     *
     * @param e 参数校验异常
     * @return 统一响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = "参数校验失败";

        if (e.getBindingResult() != null && e.getBindingResult().hasErrors()) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            if (fieldErrors != null && !fieldErrors.isEmpty()) {
                message = fieldErrors.get(0).getDefaultMessage();
            }
        }

        log.warn("参数校验异常：{}", message);
        return new ApiResponse<Object>(400, false, message, null);
    }

    /**
     * 处理表单参数绑定异常
     *
     * 例如：
     * - @ModelAttribute
     * - query 参数校验失败
     *
     * @param e 绑定异常
     * @return 统一响应
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handleBindException(BindException e) {
        String message = "参数绑定失败";

        if (e.getBindingResult() != null && e.getBindingResult().hasErrors()) {
            List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
            if (fieldErrors != null && !fieldErrors.isEmpty()) {
                message = fieldErrors.get(0).getDefaultMessage();
            }
        }

        log.warn("参数绑定异常：{}", message);
        return new ApiResponse<Object>(400, false, message, null);
    }

    /**
     * 处理其他未捕获异常
     *
     * @param request 请求对象
     * @param e 系统异常
     * @return 统一响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(HttpServletRequest request, Exception e) {
        log.error("系统异常：uri={}", request.getRequestURI(), e);
        return new ApiResponse<Object>(500, false, "系统异常，请稍后重试", null);
    }
}