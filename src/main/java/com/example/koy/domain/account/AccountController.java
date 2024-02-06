package com.example.koy.domain.account;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.koy.domain.account.dto.request.LoginRequestDto;
import com.example.koy.domain.account.dto.request.SignUpRequestDto;
import com.example.koy.domain.account.dto.response.LoginResponseDto;
import com.example.koy.domain.account.dto.response.SignUpResponseDto;
import com.example.koy.util.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {
	private final AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PreAuthorize("hasAnyAuthority('administrator')")
	@PostMapping(value = "/signup")
	public ResponseEntity<Map<String, SignUpResponseDto>> signupController(
		@Valid @RequestBody SignUpRequestDto signUpRequest) throws Exception {
		SignUpResponseDto signUpResponse = accountService.signup(signUpRequest);

		Map<String, SignUpResponseDto> result = new LinkedHashMap<>();
		result.put("data", signUpResponse);

		return ResponseEntity.ok(result);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<Map<String, LoginResponseDto>> loginController(HttpServletRequest request,
		@Valid @RequestBody LoginRequestDto loginRequest) throws Exception {
		LoginResponseDto loginResponse = accountService.login(request, loginRequest);

		Map<String, LoginResponseDto> result = new LinkedHashMap<>();
		result.put("data", loginResponse);

		return ResponseEntity.ok(result);
	}
}
