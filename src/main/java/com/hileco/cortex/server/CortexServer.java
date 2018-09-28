package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hileco.cortex.server.api.demo.DemoConstraintApi;
import com.hileco.cortex.server.api.demo.DemoFuzzerApi;
import com.hileco.cortex.server.api.demo.DemoJumpMappingApi;
import com.hileco.cortex.server.api.demo.DemoOptimizerApi;
import com.hileco.cortex.server.api.demo.DemoPathingApi;
import com.hileco.cortex.server.api.docs.DocsInstructionsListApi;
import spark.ResponseTransformer;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.staticFileLocation;

public class CortexServer {

    private static final ObjectMapper OBJECT_MAPPER;
    private static final ResponseTransformer JSON;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JSON = OBJECT_MAPPER::writeValueAsString;
    }

    public static void main(String[] args) {
        staticFileLocation("/public");
        path("/api", () -> {
            path("/demo", () -> {
                get("/constraints.json", new DemoConstraintApi(), JSON);
                get("/fuzzer.json", new DemoFuzzerApi(), JSON);
                get("/pathing.json", new DemoPathingApi(), JSON);
                get("/program.json", new DemoOptimizerApi(), JSON);
                get("/jump-mapping.json", new DemoJumpMappingApi(), JSON);
            });
            path("/docs", () -> {
                get("/instructions.json", new DocsInstructionsListApi(), JSON);
            });
        });
        after((request, response) -> response.type("application/json"));
    }
}
