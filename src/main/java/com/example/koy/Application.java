package com.example.koy;

import java.util.Iterator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Application implements ApplicationRunner {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Iterator<String> iterator = args.getOptionNames().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = args.getOptionValues(key);
		}
	}
}
