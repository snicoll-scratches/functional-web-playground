package com.example.functional;


import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import com.example.functional.web.UserHandler;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

@SpringBootApplication(
		exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, ReactiveWebAutoConfiguration.class})
public class WebFunctionPlaygroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFunctionPlaygroundApplication.class, args);
	}


	@Bean
	ApplicationRunner databaseInitialization(UserRepository userRepository) {
		return a -> userRepository.count()
				.then(n -> n == 0 ? userRepository.save(new User("Stephane", "Nicoll")) : Mono.empty())
				.block();
	}

	@Bean
	RouterFunction<?> router(UserHandler handler) {
		return route(GET("/users"), handler::listPeople)
				.and(route(GET("/users/{id}"), handler::getUser))
				.and(route(POST("/users"), handler::createUser));
	}

	@Bean
	HttpHandler httpHandler(RouterFunction<?> router) {
		return toHttpHandler(router);
	}

	/*
	@Bean
	HttpServer nettyServer(RouterFunction<?> router) {
		HttpHandler handler = toHttpHandler(router);
		HttpServer httpServer = HttpServer.create(7080);
		httpServer.start(new ReactorHttpHandlerAdapter(handler));
		return httpServer;
	}
	*/

}
