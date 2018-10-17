package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hileco.cortex.server.api.DemoConstraintApi;
import com.hileco.cortex.server.api.DemoFuzzerApi;
import com.hileco.cortex.server.api.DemoJumpMappingApi;
import com.hileco.cortex.server.api.DemoOptimizerApi;
import com.hileco.cortex.server.api.DemoPathingApi;
import com.hileco.cortex.server.api.InstructionsListApi;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.ipc.netty.http.server.HttpServer;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

@EnableWebFlux
@Configuration
public class Server {

    private static final int PORT = 8080;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void main(String[] args) {
        var routerFunction = new AnnotationConfigApplicationContext(Server.class).getBean(RouterFunction.class);
        var adapter = new ReactorHttpHandlerAdapter(toHttpHandler(routerFunction));
        var httpServer = HttpServer.create(PORT);
        httpServer.startAndAwait(adapter);
    }

    @Bean
    public Jackson2JsonEncoder jackson2JsonEncoder() {
        return new Jackson2JsonEncoder(OBJECT_MAPPER);
    }

    @Bean
    public Jackson2JsonDecoder jackson2JsonDecoder() {
        return new Jackson2JsonDecoder(OBJECT_MAPPER);
    }

    @Bean
    public WebFluxConfigurer webFluxConfigurer(Jackson2JsonEncoder encoder, Jackson2JsonDecoder decoder) {
        return new WebFluxConfigurer() {
            @Override
            public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
                configurer.defaultCodecs().jackson2JsonEncoder(encoder);
                configurer.defaultCodecs().jackson2JsonDecoder(decoder);
            }
        };
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/"), (request) -> ServerResponse.temporaryRedirect(URI.create("/index.html")).build())
                .and(route(GET("/api/demo/constraints.json"), new DemoConstraintApi()))
                .and(route(GET("/api/demo/fuzzer.json"), new DemoFuzzerApi()))
                .and(route(GET("/api/demo/pathing.json"), new DemoPathingApi()))
                .and(route(GET("/api/demo/optimizer.json"), new DemoOptimizerApi()))
                .and(route(GET("/api/demo/jump-mapping.json"), new DemoJumpMappingApi()))
                .and(route(GET("/api/demo/instructions.json"), new InstructionsListApi()))
                .and(resources("/**", new ClassPathResource("static/docs/")));
    }
}
