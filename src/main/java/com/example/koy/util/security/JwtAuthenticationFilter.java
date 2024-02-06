package com.example.koy.util.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.koy.util.errors.ErrorCode;
import com.example.koy.util.errors.exception.RestApiException;
import com.example.koy.util.errors.handler.SecurityExceptionHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;

	private final SecurityExceptionHandler securityExceptionHandler;

	public static final String[] PUBLIC_ROUTERS = {
		"/account/login",
	};

	public JwtAuthenticationFilter(JwtProvider jwtProvider, SecurityExceptionHandler securityExceptionHandler) {
		this.jwtProvider = jwtProvider;
		this.securityExceptionHandler = securityExceptionHandler;
	}

	public String getJwtTokenFromHeader(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		String jwtToken = getJwtTokenFromHeader(request);

		String path = request.getRequestURI();

		for (String publicRouter : PUBLIC_ROUTERS) {
			if (path.startsWith(publicRouter)) {
				filterChain.doFilter(request, response);

				return;
			}
		}

		Boolean isTokenValid = jwtProvider.validateToken(jwtToken);
		if (isTokenValid) {
			// 토큰이 유효하다면 security SecurityContextHolder 저장 및 다음 필터로 이동
			Authentication authentication = jwtProvider.getAuthentication(jwtToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} else {
			securityExceptionHandler.resolveAuthenticationException(request, response,
				new RestApiException(ErrorCode.UNAUTHORIZED_ERROR, "Invalid token", "다시 로그인해주세요."));
		}
	}
}
