package com.example.koy.util.pgutil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("spring.jpa.properties")
public class HibernateConfiguration {
	private Hibernate hibernate;

	@Data
	public static class Hibernate {
		private Boolean showSql;
		private Boolean formatSql;
		private Hbm2ddl hbm2ddl;
		private Naming naming;
	}

	@Data
	public static class Hbm2ddl {
		private String auto;
	}

	@Data
	public static class Naming {
		private String physicalStrategy;
	}

	public Map<String, Object> propertiesToMap() {
		Map<String, Object> properties = new HashMap<>();

		properties.put("hibernate.hbm2ddl.auto", hibernate.getHbm2ddl().getAuto());
		properties.put("hibernate.show_sql", hibernate.getShowSql());
		properties.put("hibernate.format_sql", hibernate.getFormatSql());
		properties.put("hibernate.physical_naming_strategy", hibernate.getNaming().getPhysicalStrategy());

		return properties;
	}
}
