package com.soumyajit.news.social.media.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "News Social Media : ", version = "1.0", description = "The Social Media Platform API provides a comprehensive set of endpoints to manage and interact with user-generated news content on a news social media platform. Designed with scalability and ease of use in mind, this API allows for the creation, retrieval, liking, and remove like of posts and also comment in a post , as well as user authentication and authorization to ensure secure access to the platform."))
public class NewsSocialMediaAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsSocialMediaAppApplication.class, args);
	}

}
