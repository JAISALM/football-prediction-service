package com.fps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballPredictionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballPredictionServiceApplication.class, args);
	}

}
