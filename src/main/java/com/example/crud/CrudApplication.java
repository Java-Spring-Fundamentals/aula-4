package com.example.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	/**
	 * Timeouts evitam que a nossa API fique "pendurada" esperando a ViaCEP
	 * caso ela esteja lenta ou fora do ar.
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofSeconds(3))
				.setReadTimeout(Duration.ofSeconds(5))
				.build();
	}

}
