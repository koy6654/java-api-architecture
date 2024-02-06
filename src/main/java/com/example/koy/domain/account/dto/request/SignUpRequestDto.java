package com.example.koy.domain.account.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SignUpRequestDto {
	@NotEmpty(message = "need_employee_number")
	private String employeeNumber;

	@NotEmpty(message = "need_name")
	private String name;

	@NotEmpty(message = "need_email")
	private String email;

	@NotEmpty(message = "need_department")
	private String department;

	@NotEmpty(message = "need_position")
	private String position;

	@NotEmpty(message = "need_phone_number")
	@Size(max = 11, message = "invalid_password_length(11)")
	private String phoneNumber;

	@NotNull(message = "need_group_id")
	private Integer groupId;

	@NotEmpty(message = "need_password")
	@Size(min = 8, max = 100, message = "invalid_password_length(8~100)")
	private String password;

	public void setPasswordHash(String hashPassword) {
		this.password = hashPassword;
	}
}

