package com.example.crud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	// Cliente HTTP usado para chamar a ViaCEP.
	// Os timeouts evitam que a nossa API fique "pendurada" esperando para sempre
	// quando a ViaCEP estiver lenta ou fora do ar.
	@Bean
	public RestTemplate restTemplate(@Value("${viacep.connect-timeout-ms}") int connectTimeoutMs,
									 @Value("${viacep.read-timeout-ms}") int readTimeoutMs) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeoutMs);
		factory.setReadTimeout(readTimeoutMs);
		return new RestTemplate(factory);
	}

}
