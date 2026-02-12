package com.recursify.recursify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RecursifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecursifyApplication.class, args);
	}

}
