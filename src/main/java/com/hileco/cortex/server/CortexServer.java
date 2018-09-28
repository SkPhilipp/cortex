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
import spark.Spark;

public class CortexServer {

    private static final int SERVER_PORT = 8080;
    private static final ObjectMapper OBJECT_MAPPER;
    private static final ResponseTransformer JSON;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JSON = OBJECT_MAPPER::writeValueAsString;
    }

    public static void main(String[] args) {
        Spark.port(SERVER_PORT);
        Spark.staticFileLocation("/public");
        Spark.path("/api", () -> {
            Spark.path("/demo", () -> {
                Spark.get("/constraints.json", new DemoConstraintApi(), JSON);
                Spark.get("/fuzzer.json", new DemoFuzzerApi(), JSON);
                Spark.get("/pathing.json", new DemoPathingApi(), JSON);
                Spark.get("/program.json", new DemoOptimizerApi(), JSON);
                Spark.get("/jump-mapping.json", new DemoJumpMappingApi(), JSON);
            });
            Spark.path("/docs", () -> {
                Spark.get("/instructions.json", new DocsInstructionsListApi(), JSON);
            });
        });
        Spark.after((request, response) -> response.type("application/json"));
    }
}
