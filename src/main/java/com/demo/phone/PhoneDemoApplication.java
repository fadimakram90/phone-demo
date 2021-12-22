package com.demo.phone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.demo.phone")
@EntityScan("com.demo.phone.model")
@SpringBootApplication
public class PhoneDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhoneDemoApplication.class, args);
	}

}
