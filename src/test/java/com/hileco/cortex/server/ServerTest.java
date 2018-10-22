package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.context.data.ProgramStoreZone.CALL_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Server.class)
public class ServerTest {

    private static final int FUZZER_SEED = 0;

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
    public void documentList() throws Exception {
        this.mockMvc.perform(get("/api/instructions/list"))
                .andDo(document("instructions-list", responseFields(
                        fieldWithPath("instructions[].name").description("The instruction's name."),
                        fieldWithPath("instructions[].takes").description("The stack positions which are inputs to the instruction."),
                        fieldWithPath("instructions[].provides").description("The stack positions which are modified by the instruction.")
                )));
    }

    @Test
    public void documentConstraints() throws Exception {
        var request = new InstructionsController.ProgramRequest(List.of(
                new PUSH(BigInteger.valueOf(0).toByteArray()),
                new LOAD(CALL_DATA),
                new PUSH(BigInteger.valueOf(10).toByteArray()),
                new EQUALS()
        ));
        this.mockMvc.perform(post("/api/instructions/constraints")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-constraints", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), responseFields(
                        fieldWithPath("expression").description("The top constraint expression.")
                )));
    }

    @Test
    public void documentFuzzer() throws Exception {
        this.mockMvc.perform(get("/api/instructions/fuzzer?seed=0"))
                .andDo(document("instructions-fuzzer", responseFields(
                        fieldWithPath("instructions").description("The instructions of the generated program.")
                )));
    }

    @Test
    public void documentOptimizer() throws Exception {
        var request = new InstructionsController.ProgramRequest(List.of(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        this.mockMvc.perform(post("/api/instructions/optimizer")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-optimizer", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), responseFields(
                        fieldWithPath("instructions").description("The instructions of the optimized equivalent.")
                )));
    }

    @Test
    public void documentFlowMapping() throws Exception {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var request = new InstructionsController.ProgramRequest(program.getInstructions());
        this.mockMvc.perform(post("/api/instructions/flow-mapping")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-flow-mapping", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), relaxedResponseFields(
                        fieldWithPath("flowMapping").description("The list of all possible paths through the program.")
                )));
    }

    @Test
    public void documentSolve() throws Exception {
        var request = new InstructionsController.ProgramRequest(List.of(
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new PUSH(BigInteger.valueOf(0xffffffL).toByteArray()),
                new PUSH(BigInteger.valueOf(10L).toByteArray()),
                new PUSH(BigInteger.valueOf(0L).toByteArray()),
                new LOAD(CALL_DATA),
                new ADD(),
                new MODULO(),
                new LESS_THAN()
        ));
        this.mockMvc.perform(post("/api/instructions/solve")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-solve", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), responseFields(
                        fieldWithPath("expression").description("The mathematical representation."),
                        fieldWithPath("solution").description("The suggested solution.")
                )));
    }

    @Test
    public void documentPathing() throws Exception {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var request = new InstructionsController.ProgramRequest(program.getInstructions());
        this.mockMvc.perform(post("/api/instructions/pathing")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-pathing", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), responseFields(
                        fieldWithPath("paths").description("The list of all possible paths.")
                )));
    }
}
