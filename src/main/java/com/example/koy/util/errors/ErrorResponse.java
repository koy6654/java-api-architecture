package com.example.koy.util.errors;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
	private HttpStatus error;
	private int status;
	private String code;
	private String message;
	private String reason;
	@JsonProperty("user_message")
	private String userMessage;

	/**
	 * Constructor-1
	 * @param errorCode ErrorCode
	 */
	protected ErrorResponse(final ErrorCode errorCode) {
		this.error = errorCode.getHttpStatus();
		this.status = errorCode.getHttpStatus().value();
		this.code = errorCode.getDivisionCode();
		this.message = errorCode.getMessage();
	}

	/**
	 * Constructor-2
	 * @param errorCode ErrorCode
	 * @param reason String
	 */
	protected ErrorResponse(final ErrorCode errorCode, final String reason, final String userMessage) {
		this.error = errorCode.getHttpStatus();
		this.status = errorCode.getHttpStatus().value();
		this.code = errorCode.getDivisionCode();
		this.message = errorCode.getMessage();
		this.reason = reason;
		this.userMessage = userMessage;
	}

	public static ErrorResponse of(final ErrorCode errorCode) {
		return new ErrorResponse(errorCode);
	}

	public static ErrorResponse of(final ErrorCode errorCode, final String reason, final String userMessage) {
		return new ErrorResponse(errorCode, reason, userMessage);
	}
}
