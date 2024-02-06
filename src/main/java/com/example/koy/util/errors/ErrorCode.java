package com.example.koy.util.errors;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "B001", "Bad Request Exception"),
	INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "B002", "Invalid Argument Exception"),
	REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "B003", "Required request body is missing"),
	REQUEST_PARAMETER_MISSING_ERROR(HttpStatus.BAD_REQUEST, "B004", "Required request parameter is missing"),
	DUPLICATED_ERROR(HttpStatus.BAD_REQUEST, "B005", "Duplicated Exception"),
	NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "N001", "Not Found Exception"),
	NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "N002", "Not Found Header Data Exception"),
	UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "U001", "Unauthorized Exception"),
	FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "F001", "Forbidden Exception"),
	CONFLICT(HttpStatus.CONFLICT, "C001", "Conflict Exception"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "I999", "Internal Server Exception");

	private final HttpStatus httpStatus;
	private final String divisionCode;
	private final String message;
}
