package com.example.koy.util.errors.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import com.example.koy.util.errors.ErrorCode;
import com.example.koy.util.errors.exception.RestApiException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint {
	private final HandlerExceptionResolver resolver;

	private final DispatcherServlet servlet;

	public SecurityExceptionHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
		DispatcherServlet servlet) {
		this.resolver = resolver;
		this.servlet = servlet;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authenticationException) throws AuthenticationException {
		if (!this.checkEndpointExist(request)) {
			this.resolveAuthenticationException(request, response,
				new RestApiException(ErrorCode.NOT_FOUND_ERROR, "Resource not found", "페이지를 찾을 수 없습니다."));
			return;
		}

		this.resolveAuthenticationException(request, response, authenticationException);
	}

	// GlobalExceptionHandler 로 에러 처리를 넘김
	public void resolveAuthenticationException(HttpServletRequest request, HttpServletResponse response,
		Exception exception) {
		resolver.resolveException(request, response, null, exception);
	}

	private Boolean checkEndpointExist(HttpServletRequest request) {
		assert servlet.getHandlerMappings() != null : "errorId: 3f15aa06-a9ad-4c14-8c47-3ac99a01dda3";
		for (HandlerMapping handlerMapping : servlet.getHandlerMappings()) {
			try {
				HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
				if (foundHandler != null) {
					return true;
				}
			} catch (Exception err) {
				return false;
			}
		}

		return false;
	}
}
