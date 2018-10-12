package com.hileco.cortex.documentation;

import com.hileco.cortex.server.Server;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Server.class)
public class ServerTests {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @Before
    public void setup() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient().baseUrl("https://cortex.minesec.net")
                .filter(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void documentDemoConstraints() {
        this.webTestClient.get()
                .uri("/api/demo/constraints.json")
                .exchange()
                .expectStatus().isOk().expectBody()
                .consumeWith(document("demo-constraints", responseFields(
                        fieldWithPath("expression").description("The mathematical representation."),
                        fieldWithPath("solution").description("The suggested solution.")
                )));
    }

    @Test
    public void documentDemoFuzzer() {
        this.webTestClient.get()
                .uri("/api/demo/fuzzer.json")
                .exchange()
                .expectBody()
                .consumeWith(document("demo-fuzzer", responseFields(
                        fieldWithPath("graph").description("The graph representation of the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentDemoJumpMapping() {
        this.webTestClient.get()
                .uri("/api/demo/pathing.json")
                .exchange()
                .expectBody()
                .consumeWith(document("demo-pathing", responseFields(
                        fieldWithPath("program").description("The program itself."),
                        fieldWithPath("paths").description("The list of all possible paths.")
                )));
    }

    @Test
    public void documentDemoOptimizer() {
        this.webTestClient.get()
                .uri("/api/demo/program.json")
                .exchange()
                .expectBody()
                .consumeWith(document("demo-program", responseFields(
                        fieldWithPath("optimizedGraph").description("The graph representation of the program, post-optimization."),
                        fieldWithPath("graph").description("The graph representation of the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentDemoPathing() {
        this.webTestClient.get()
                .uri("/api/demo/jump-mapping.json")
                .exchange()
                .expectBody()
                .consumeWith(document("demo-jump-mapping", responseFields(
                        fieldWithPath("jumpMapping").description("The list of all possible paths through the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentDemoInstructionsList() {
        this.webTestClient.get()
                .uri("/api/demo/instructions.json")
                .exchange()
                .expectBody()
                .consumeWith(document("demo-instructions", responseFields(
                        fieldWithPath("[].name").description("The instruction's name."),
                        fieldWithPath("[].takes").description("The stack positions which are inputs to the instruction."),
                        fieldWithPath("[].provides").description("The stack positions which are modified by the instruction.")
                )));
    }
}
