package com.hileco.cortex.server;

import com.hileco.cortex.server.demo.ConstraintDemo;
import com.hileco.cortex.server.demo.FuzzerTreeDemo;
import com.hileco.cortex.server.demo.PathingDemo;
import com.hileco.cortex.server.demo.ProgramDemo;
import com.hileco.cortex.server.demo.TreeMapperDemo;

import static spark.Spark.get;
import static spark.Spark.path;

public class CortexServer {
    public static void main(String[] args) {
        path("/demo", () -> {
            get("/constraints", new TextRoute(new ConstraintDemo()));
            get("/fuzzer", new TextRoute(new FuzzerTreeDemo()));
            get("/pathing", new TextRoute(new PathingDemo()));
            get("/program", new TextRoute(new ProgramDemo()));
            get("/jump-mapping", new TextRoute(new TreeMapperDemo()));
        });
        get("/", (req, res) -> "<a href='/demo/constraints'>/demo/constraints</a>\n" +
                "<a href='/demo/fuzzer'>/demo/fuzzer</a>\n" +
                "<a href='/demo/pathing'>/demo/pathing</a>\n" +
                "<a href='/demo/program'>/demo/program</a>\n" +
                "<a href='/demo/jump-mapping'>/demo/jump-mapping</a>\n");
    }
}
