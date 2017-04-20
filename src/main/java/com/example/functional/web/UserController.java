package com.example.functional.web;

import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Controller
public class UserController {

	private final UserRepository repository;

	public UserController(UserRepository repository) {
		this.repository = repository;
	}

	public RouterFunction<ServerResponse> route() {
		return RouterFunctions
				.route(GET("/users"), this::listPeople)
				.andRoute(GET("/users/{id}"), this::getUser)
				.andRoute(POST("/users"), this::createUser);
	}

	private Mono<ServerResponse> listPeople(ServerRequest request) {
		Flux<User> people = this.repository.findAll();
		return ServerResponse.ok().body(people, User.class);
	}

	private Mono<ServerResponse> getUser(ServerRequest request) {
		String userId = request.pathVariable("id");
		Mono<ServerResponse> notFound = ServerResponse.notFound().build();
		return this.repository.findOne(userId)
				.flatMap(user -> ServerResponse.ok().body(Mono.just(user), User.class))
				.switchIfEmpty(notFound);
	}

	private Mono<ServerResponse> createUser(ServerRequest request) {
		Mono<User> user = request.bodyToMono(User.class);
		return ServerResponse.ok().build(this.repository.save(user).then());
	}

}
