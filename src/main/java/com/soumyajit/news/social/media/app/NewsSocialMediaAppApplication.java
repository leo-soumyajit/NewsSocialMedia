package com.soumyajit.news.social.media.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class NewsSocialMediaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsSocialMediaAppApplication.class, args);
	}

}
