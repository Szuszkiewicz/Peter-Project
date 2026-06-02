package com.Peter.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class FrontendFallbackConfig {

    private static final List<String> API_PREFIXES = List.of(
            "/user",
            "/offerCall",
            "/article",
            "/category",
            "/collect",
            "/likes",
            "/comment",
            "/notifications",
            "/ws-notification"
    );

    @Bean
    public RouterFunction<ServerResponse> frontendFallbackRouter() {
        return RouterFunctions.route(this::isFrontendNavigation, this::serveFrontendRoute);
    }

    private Mono<ServerResponse> serveFrontendRoute(ServerRequest request) {
        ClassPathResource index = new ClassPathResource("static/index.html");
        if (!index.exists()) {
            return ServerResponse.notFound().build();
        }

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(BodyInserters.fromResource(index));
    }

    private boolean isFrontendNavigation(ServerRequest request) {
        if (request.method() != HttpMethod.GET) {
            return false;
        }

        String path = request.path();
        if (path.equals("/") || path.equals("/index.html")) {
            return false;
        }

        if (path.substring(path.lastIndexOf('/') + 1).contains(".")) {
            return false;
        }

        if (API_PREFIXES.stream().anyMatch(prefix -> path.equals(prefix) || path.startsWith(prefix + "/"))) {
            return false;
        }

        return request.headers().accept().isEmpty()
                || request.headers().accept().stream().anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
    }
}
