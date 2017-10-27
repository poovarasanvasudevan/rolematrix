package com.htc.rolematrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class RoleMatrixApplication
		//extends SpringBootServletInitializer
{
/*
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(RoleMatrixApplication.class);
	}
*/
	public static void main(String[] args) {
		SpringApplication.run(RoleMatrixApplication.class, args);
	}
}
