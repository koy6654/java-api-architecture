package com.example.koy.domain.account.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginResponseDto {
	private String token;
	private Boolean requirePasswordChange;

	public static LoginResponseDto fromEntity(@Nullable String token, Boolean requirePasswordChange) {
		return LoginResponseDto.builder().token(token).requirePasswordChange(requirePasswordChange).build();
	}
}
