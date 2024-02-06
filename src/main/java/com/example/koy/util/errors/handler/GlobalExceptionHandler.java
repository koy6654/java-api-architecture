package com.example.koy.util.errors.handler;

import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.koy.util.CommonService;
import com.example.koy.util.errors.ErrorCode;
import com.example.koy.util.errors.ErrorResponse;
import com.example.koy.util.errors.exception.RestApiException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * API 호출 시 DTO 값이 유효하지 않은 경우
	 *
	 * @param exception MethodArgumentNotValidException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception) {
		log.error("handleMethodArgumentNotValidException : {}", exception.getStackTrace()[0]);

		BindingResult bindingResult = exception.getBindingResult();
		String reason = initReasonJsonString(bindingResult.getFieldErrors());
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_ARGUMENT_ERROR, reason, "올바르지 않은 입력입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
	 *
	 * @param exception MissingRequestHeaderException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(
		MissingRequestHeaderException exception) {
		log.error("handleMissingRequestHeaderException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_HEADER_ERROR, exception.getMessage(),
			"올바르지 않은 헤더 요청입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * API 호출 시 클라이언트에서 넘어온 Body 값이 없는 경우
	 *
	 * @param exception MissingRequestHeaderException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception) {
		log.error("handleHttpMessageNotReadableException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, exception.getMessage(),
			"올바르지 않은 입력입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * 권한이 없을 때 발생하는 에러 (From spring security)
	 *
	 * @param exception AuthenticationException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleSecurityUnauthorizedException(AuthenticationException exception) {
		log.error("handleSecurityUnauthorizedException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, exception.getMessage(),
			"다시 로그인해주세요.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * 커스텀 에러 핸들러
	 * @param exception RestApiException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(RestApiException.class)
	protected ResponseEntity<ErrorResponse> handleRestApiException(RestApiException exception) {
		log.error("handleRestApiException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(exception.getErrorCode(), exception.getReason(),
			exception.getUserMessage());

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * 데이터베이스 접근 에러 핸들러
	 * @param exception DataAccessException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(DataAccessException.class)
	protected ResponseEntity<ErrorResponse> handleDataAccessException(PSQLException exception) {
		log.error("handleDataAccessException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, "Internal Server Exception",
			"일시적인 서버 오류입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * Security 접근 에러 핸들러
	 * @param exception AccessDeniedException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
		log.error("handleAccessDeniedException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR,
			ErrorCode.FORBIDDEN_ERROR.getMessage(), "접근 권한이 없습니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * Security UserDetails Not found 에러 핸들러
	 * @param exception UsernameNotFoundException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(UsernameNotFoundException.class)
	protected ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {
		log.error("handleUsernameNotFoundException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, exception.getMessage(),
			"인증 정보가 유효하지 않습니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * GetMapping RequestParams not null 에러 핸들러
	 * @param  exception MissingServletRequestParameterException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<ErrorResponse> handleRequestParamException(
		MissingServletRequestParameterException exception) {
		log.error("handleRequestParamException : {}", exception.getStackTrace()[0]);

		JSONObject reason = new JSONObject();
		reason.put(exception.getParameterName(), "need_" + exception.getParameterName());

		final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_PARAMETER_MISSING_ERROR,
			reason.toString(), "올바르지 않은 입력입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * GetMapping RequestParams @NotBlank 에러 핸들러
	 * @param  exception ConstraintViolationException
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
		ConstraintViolationException exception) {
		log.error("handleConstraintViolationException : {}", exception.getStackTrace()[0]);

		JSONObject reason = new JSONObject();

		exception.getConstraintViolations().forEach((e) -> {
			String key = e.getPropertyPath().toString().substring(e.getPropertyPath().toString().lastIndexOf(".") + 1);
			reason.put(key, e.getMessage());
		});
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_ARGUMENT_ERROR, reason.toString(),
			"올바르지 않은 입력입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * 위에서 핸들링 하지 못한 모든 Exception 핸들러
	 * @param exception Exception
	 * @return ResponseEntity<ErrorResponse>
	 */
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleAllException(Exception exception) {
		log.error("handleAllException : {}", exception.getStackTrace()[0]);

		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, "Internal Server Exception",
			"일시적인 서버 오류입니다.");

		return new ResponseEntity<>(response, response.getError());
	}

	/**
	 * FieldErrorList to JSON String
	 * @param fieldErrorList List<FieldError>
	 * @return String
	 */
	private String initReasonJsonString(List<FieldError> fieldErrorList) {
		JSONObject jsonObject = new JSONObject();

		for (FieldError fieldError : fieldErrorList) {
			jsonObject.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return CommonService.camelToSnake(jsonObject.toString());
	}
}
