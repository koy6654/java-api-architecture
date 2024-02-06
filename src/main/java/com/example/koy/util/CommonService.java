package com.example.koy.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import com.example.koy.util.errors.ErrorCode;
import com.example.koy.util.errors.exception.RestApiException;

import jakarta.servlet.http.HttpServletRequest;

public final class CommonService {
	/**
	 * IP 가져오기
	 * @param request HttpServletRequest
	 * @return String - 접속 IP 반환
	 */
	public static String getRequestIp(HttpServletRequest request) throws UnknownHostException, RestApiException {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null) {
			ip = request.getRemoteAddr();
		}

		if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();
		}

		if (ip == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ip", "invalid");

			throw new RestApiException(ErrorCode.INTERNAL_SERVER_ERROR, jsonObject, "Invalid request header ip");
		}

		return ip;
	}

	/**
	 * camelToSnake
	 * @param camelCase String
	 * @return String
	 */
	public static String camelToSnake(String camelCase) {
		String regex = "([a-z])([A-Z]+)";
		String replacement = "$1_$2";
		return camelCase.replaceAll(regex, replacement).toLowerCase();
	}

	/**
	 * convert date to utc time at 00:00:00
	 * @param date String
	 * @return String
	 */
	public static String atStartOfUtcTime(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		ZonedDateTime startTime = localDate.atStartOfDay(ZoneId.of("UTC"));
		return startTime.toInstant().toString();
	}

	/**
	 * convert date to utc time at 23:59:59
	 * @param date String
	 * @return String
	 */
	public static String atEndOfUtcTime(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		ZonedDateTime endTime = localDate.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC"));
		return endTime.toInstant().toString();
	}

	/**
	 * get offset with page and limit
	 * @param page String
	 * @param limit String
	 * @return Integer
	 */
	public static Integer getOffset(String page, String limit) {
		return Integer.parseInt(page) * Integer.parseInt(limit);
	}
}
