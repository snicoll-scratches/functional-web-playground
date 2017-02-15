package com.example.functional;


import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import com.example.functional.web.UserController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebFunctionPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFunctionPlaygroundApplication.class, args);
	}


	@Bean
	public ApplicationRunner databaseInitialization(UserRepository userRepository) {
		return a -> userRepository.count()
				.then(n -> n == 0 ? userRepository.save(new User("Stephane", "Nicoll")) : Mono.empty())
				.block();
	}

	@Bean
	public RouterFunction<ServerResponse> route(UserController userController) {
		return userController.route();
	}

}
