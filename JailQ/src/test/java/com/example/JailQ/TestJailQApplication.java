package com.example.JailQ;

import org.springframework.boot.SpringApplication;

public class TestJailQApplication {

	public static void main(String[] args) {
		SpringApplication.from(JailQApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
