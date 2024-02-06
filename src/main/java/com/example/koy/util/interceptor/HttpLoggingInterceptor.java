package com.example.koy.util.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {
	public static final String LOG_ID = "HTTP_LOGGER";

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler,
		@Nullable Exception ex) throws Exception {
		String requestUri = request.getRequestURI();
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.equals("anonymousUser") && principal instanceof UserDetails) {
			String employeeNumber = ((UserDetails)principal).getUsername();
			log.info(LOG_ID + ": [{}] {} {} {} - {}", request.getMethod(), response.getStatus(),
				formatter.format(now), requestUri, employeeNumber);
		} else {
			log.info(LOG_ID + ": [{}] {} {} {}", request.getMethod(), response.getStatus(), formatter.format(now),
				requestUri);
		}
	}
}
