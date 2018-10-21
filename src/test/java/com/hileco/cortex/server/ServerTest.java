package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Server.class)
public class ServerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void documentDemoConstraints() throws Exception {
        this.mockMvc.perform(get("/api/demo/constraints.json"))
                .andDo(document("demo-constraints", responseFields(
                        fieldWithPath("expression").description("The mathematical representation."),
                        fieldWithPath("solution").description("The suggested solution.")
                )));
    }

    @Test
    public void documentDemoFuzzer() throws Exception {
        this.mockMvc.perform(get("/api/demo/fuzzer.json"))
                .andDo(document("demo-fuzzer", responseFields(
                        fieldWithPath("graph").description("The graph representation of the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentDemoJumpMapping() throws Exception {
        this.mockMvc.perform(get("/api/demo/pathing.json"))
                .andDo(document("demo-pathing", responseFields(
                        fieldWithPath("program").description("The program itself."),
                        fieldWithPath("paths").description("The list of all possible paths.")
                )));
    }

    @Test
    public void documentDemoOptimizer() throws Exception {
        this.mockMvc.perform(get("/api/demo/optimizer.json"))
                .andDo(document("demo-optimizer", responseFields(
                        fieldWithPath("optimizedGraph").description("The graph representation of the program, post-optimization."),
                        fieldWithPath("graph").description("The graph representation of the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentDemoPathing() throws Exception {
        this.mockMvc.perform(get("/api/demo/jump-mapping.json"))
                .andDo(document("demo-jump-mapping", relaxedResponseFields(
                        fieldWithPath("jumpMapping").description("The list of all possible paths through the program."),
                        fieldWithPath("program").description("The program itself.")
                )));
    }

    @Test
    public void documentInstructionsList() throws Exception {
        this.mockMvc.perform(get("/api/instructions/list.json"))
                .andDo(document("instructions-list", responseFields(
                        fieldWithPath("[].name").description("The instruction's name."),
                        fieldWithPath("[].takes").description("The stack positions which are inputs to the instruction."),
                        fieldWithPath("[].provides").description("The stack positions which are modified by the instruction.")
                )));
    }

    @Test
    public void documentInstructionsConstraints() throws Exception {
        var instructions = new InstructionsController.ConstraintsRequest(List.of(
                new PUSH(BigInteger.valueOf(0).toByteArray()),
                new LOAD(ProgramStoreZone.CALL_DATA),
                new PUSH(BigInteger.valueOf(10).toByteArray()),
                new EQUALS()
        ));
        var content = this.objectMapper.writeValueAsString(instructions);
        var request = this.objectMapper.readValue(content, InstructionsController.ConstraintsRequest.class);
        this.mockMvc.perform(post("/api/instructions/constraints.json")
                                     .content(content))
                .andDo(document("instructions-constraints"))
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                    System.out.println();
                });
    }
}
