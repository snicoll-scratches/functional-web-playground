package com.example.functional;


import java.util.List;

import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import reactor.core.publisher.Mono;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.reactiveweb.ReactiveWebAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@SpringBootApplication(
		exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, ReactiveWebAutoConfiguration.class})
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
	public HttpHandler httpHandler(List<RouterFunction> routerFunctions) {
		return toHttpHandler(
				routerFunctions.stream().reduce(RouterFunction::and).get()
		);
	}

}
