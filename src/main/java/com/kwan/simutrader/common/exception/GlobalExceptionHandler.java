package com.kwan.simutrader.common.exception;

import com.kwan.simutrader.common.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j // 로깅
@RestControllerAdvice // 모든 @RestController에 적용되는 전역 예외 처리기
public class GlobalExceptionHandler {

    // CommonException 처리
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ResponseDTO<Object>> handleCommonException(CommonException e) {
        log.warn("CommonException occured: {}", e.getMessage(), e); // 경고 레벨로 로깅
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ResponseDTO.fail(e));
    }

    // MissingServletRequestParameterException 처리 (400 Bad Request)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException occured: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.fail(e));
    }

    // MethodArgumentTypeMismatchException 처리 (400 Bad Request)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseDTO<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException occured: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.fail(e));
    }

    // 모든 예상치 못한 오류 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> handleAllUncaughtException(Exception e) {
        log.error("Uncaught Exception occured: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.fail(e));
    }

}
