package com.example.functional.web;

import com.example.functional.domain.User;
import com.example.functional.domain.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Controller
public class UserController implements RouterFunction<ServerResponse> {

	private final UserRepository repository;

	public UserController(UserRepository repository) {
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Mono<HandlerFunction<ServerResponse>> route(ServerRequest request) {
		return ((RouterFunction<ServerResponse>) RouterFunctions
				.route(GET("/users"), this::listPeople)
				.andRoute(GET("/users/{id}"), this::getUser)
				.andRoute(POST("/users"), this::createUser)).route(request);
	}

	private Mono<ServerResponse> listPeople(ServerRequest request) {
		Flux<User> people = this.repository.findAll();
		return ServerResponse.ok().body(people, User.class);
	}

	private Mono<ServerResponse> getUser(ServerRequest request) {
		String userId = request.pathVariable("id");
		Mono<ServerResponse> notFound = ServerResponse.notFound().build();
		return this.repository.findOne(userId)
				.then(user -> ServerResponse.ok().body(Mono.just(user), User.class))
				.otherwiseIfEmpty(notFound);
	}

	private Mono<ServerResponse> createUser(ServerRequest request) {
		Mono<User> user = request.bodyToMono(User.class);
		return ServerResponse.ok().build(this.repository.save(user).then());
	}

}
