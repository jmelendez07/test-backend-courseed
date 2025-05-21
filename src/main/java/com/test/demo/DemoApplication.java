package com.test.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class DemoApplication {

	public static void main(String[] args) {
		System.getenv("MONGODB_URI");
		System.getenv("MONGODB_DB");
		System.getenv("PAYU_API_KEY");
		System.getenv("PAYU_MERCHANT_ID");
		System.getenv("FRONTEND_URL");
		System.getenv("PAYU_URL");

		SpringApplication.run(DemoApplication.class, args);
	}

}
