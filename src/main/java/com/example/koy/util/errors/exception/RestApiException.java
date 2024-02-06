package com.example.koy.util.errors.exception;

import org.json.JSONObject;

import com.example.koy.util.errors.ErrorCode;

import lombok.Getter;

// 1. ErrorCode + reason(string)
// throw new RestApiException(ErrorCode.BAD_REQUEST_ERROR, "example reason");

// 2. ErrorCode + reason(JSONObject)
// JSONObject jsonObject = new JSONObject();
// jsonObject.put("key", "value")
// throw new RestApiException(ErrorCode.BAD_REQUEST_ERROR, jsonObject);

@Getter
public class RestApiException extends Exception {
	private final ErrorCode errorCode;
	private final String reason;
	private final String userMessage;

	/**
	 * Constructor-1
	 * @param errorCode ErrorCode
	 * @param reason String
	 */
	public RestApiException(final ErrorCode errorCode, final String reason, String userMessage) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.reason = reason;
		this.userMessage = userMessage;
	}

	/**
	 * Constructor-2
	 * @param errorCode ErrorCode
	 * @param jsonObject JSONObject
	 */
	public RestApiException(final ErrorCode errorCode, final JSONObject jsonObject, String userMessage) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.reason = jsonObject.toString();
		this.userMessage = userMessage;
	}
}
