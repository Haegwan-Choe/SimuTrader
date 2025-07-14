package com.kwan.simutrader.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kwan.simutrader.common.exception.CommonException;
import com.kwan.simutrader.common.exception.ErrorCode;
import com.kwan.simutrader.common.exception.ExceptionDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// 응답 DTO 통일
@Data
public class ResponseDTO<T> {

    @JsonIgnore // json 직렬화 제외
    private HttpStatus status; // 상태 코드는 응답 헤더로 전달되므로

    @NotNull
    private boolean success; // 요청 성공 여부

    @Nullable // 데이터가 없을 수도 있음
    private T data; // 실제 응답 데이터 (성공 시)

    @Nullable // 에러가 없을 수도 있음
    private ExceptionDTO error; // 에러 정보 (실패 시)

    // 모든 필드를 받는 생성자 (static 팩토리 메서드 내부에서 사용)
    public ResponseDTO(HttpStatus status, boolean success, @Nullable T data, @Nullable ExceptionDTO error) {
        this.status = status;
        this.success = success;
        this.data = data;
        this.error = error;
    }

    /* 필기. static 팩토리 메서드 */

    // 1. 성공 응답
    // HTTP Status : 200 OK
    public static <T> ResponseDTO<T> ok(T data) {
        return new ResponseDTO<>(
                HttpStatus.OK,
                true,
                data,
                null
        );
    }

    // 2. 비즈니스 로직 오류 (CustomException 사용)
    // HTTP Status : 커스텀 에러코드에서 뽑아서 사용
    public static ResponseDTO<Object> fail(@NotNull CommonException e) {
        return new ResponseDTO<>(
                e.getErrorCode().getHttpStatus(),
                false,
                null,
                ExceptionDTO.of(e.getErrorCode()) // ErrorCode 기반 ExceptionDTO 생성
        );
    }

    // 3. 클라이언트 요청 오류 : 필수 파라미터 누락
    // HTTP status : 400 Bad Request
    public static ResponseDTO<Object> fail(final MissingServletRequestParameterException e) {
        return new ResponseDTO<>(
                HttpStatus.BAD_REQUEST, // 400 에러
                false,
                null,
                ExceptionDTO.of(ErrorCode.MISSING_REQUEST_PARAMETER)
        );
    }

    // 4. 클라이언트 요청 오류: 메서드 인자 타입 불일치 (예: 숫자 입력 자리에 문자열)
    // HTTP Status: 400 Bad Request
    public static ResponseDTO<Object> fail(final MethodArgumentTypeMismatchException e) {
        return new ResponseDTO<>(
                HttpStatus.BAD_REQUEST, // 400 에러
                false,
                null,
                ExceptionDTO.of(ErrorCode.INVALID_INPUT_VALUE)
        );
    }

    // 5. 범용적인 서버 내부 오류 (예상치 못한 RuntimeException 등)
    // HTTP Status: 500 Internal Server Error
    // 이 메서드는 위에 명시된 특정 예외 처리 메서드가 잡지 못한 모든 Throwable을 처리
    public static ResponseDTO<Object> fail(Throwable e) {
        // 실제 운영 환경에서는 상세한 예외 메시지를 클라이언트에게 직접 노출하지 않는 것이 좋음
        // 서버 로그에는 자세한 스택 트레이스를 기록
        return new ResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR, // 서버 내부에서 발생한 예측 불가능한 오류
                false,
                null,
                ExceptionDTO.of(ErrorCode.INTERNAL_SERVER_ERROR)
        );
    }


}
