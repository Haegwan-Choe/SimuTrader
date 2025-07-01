package com.kwan.simutrader.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonException extends RuntimeException {
    private final ErrorCode errorCode;
    // 에러 발생 시 ErrorCode별 메시지
    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
