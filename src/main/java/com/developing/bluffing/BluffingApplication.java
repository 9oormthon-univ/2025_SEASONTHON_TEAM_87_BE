package com.developing.bluffing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BluffingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BluffingApplication.class, args);
	}

}
