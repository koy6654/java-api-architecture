package com.example.koy.domain.account;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.koy.domain.account.dto.request.LoginRequestDto;
import com.example.koy.domain.account.dto.request.SignUpRequestDto;
import com.example.koy.domain.account.dto.response.LoginResponseDto;
import com.example.koy.domain.account.dto.response.SignUpResponseDto;
import com.example.koy.util.errors.exception.RestApiException;

import jakarta.servlet.http.HttpServletRequest;

public interface AccountService {
	/**
	 * 회원 가입 서비스 로직
	 * @param signUpRequestDto SignUpRequestDto
	 * @return SignUpResponseDto
	 */
	SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) throws RestApiException;

	/**
	 * 로그인 서비스 로직
	 * @param loginRequestDto LoginRequestDto
	 * @return LoginResponseDto
	 */
	LoginResponseDto login(HttpServletRequest request, LoginRequestDto loginRequestDto) throws RestApiException,
		UnsupportedEncodingException, UnknownHostException;
}
