package com.example.koy.domain.account.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponseDto {
	private Boolean success;

	public static SignUpResponseDto fromEntity(Boolean success) {
		return SignUpResponseDto.builder().success(success).build();
	}
}
