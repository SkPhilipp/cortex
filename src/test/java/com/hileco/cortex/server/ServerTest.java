package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.InstructionsBuilder;
import com.hileco.cortex.instructions.conditions.EQUALS;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.debug.HALT;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.DIVIDE;
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
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.WINNER;
import static com.hileco.cortex.vm.data.ProgramStoreZone.CALL_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Server.class)
public class ServerTest {

    private static final int FUZZER_SEED = 2;

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
                .apply(documentationConfiguration(this.restDocumentation)
                               .operationPreprocessors()
                               .withRequestDefaults(
                                       modifyUris()
                                               .scheme("https")
                                               .host("cortex")
                                               .removePort(),
                                       removeHeaders("Content-Length", "Content-Type", "Host")))
                .build();
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
                ), relaxedResponseFields(
                        fieldWithPath("expression").description("The mathematical representation."),
                        fieldWithPath("solution").description("The suggested solution."),
                        fieldWithPath("solution.possibleValues").description("Possible values part of the solution."),
                        fieldWithPath("solution.solvable").description("Whether a solution is technically possible.")
                )));
    }

    @Test
    public void documentVisualize() throws Exception {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var request = new InstructionsController.ProgramRequest(program.getInstructions());
        this.mockMvc.perform(post("/api/instructions/visualize")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-visualize", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), this.storeResponseFile()));
    }

    private Snippet storeResponseFile() {
        return operation -> {
            var context = (RestDocumentationContext) operation.getAttributes().get(RestDocumentationContext.class.getName());
            var path = Paths.get(context.getOutputDirectory().getAbsolutePath(), operation.getName(), "response-file.adoc");
            var outputStream = new ByteArrayOutputStream();
            outputStream.write("++++\n".getBytes());
            outputStream.write("<p style=\"text-align: center\">\n".getBytes());
            outputStream.write("<img src=\"data:image/png;base64,".getBytes());
            outputStream.write(Base64.getEncoder().encode(operation.getResponse().getContent()));
            outputStream.write("\"/>\n".getBytes());
            outputStream.write("</p>\n".getBytes());
            outputStream.write("++++\n".getBytes());
            Files.createDirectories(path.getParent());
            Files.write(path, outputStream.toByteArray());
        };
    }

    @Test
    public void documentAttack() throws Exception {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var request = new InstructionsController.ProgramRequest(program.getInstructions());
        this.mockMvc.perform(post("/api/instructions/attack?targetMethod=anyCall")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("instructions-attack", requestFields(
                        fieldWithPath("instructions").description("The instructions of the program.")
                ), relaxedResponseFields(
                        fieldWithPath("solutions").description("The suggested solution."),
                        fieldWithPath("solutions[].possibleValues").description("Possible values part of the solution."),
                        fieldWithPath("solutions[].solvable").description("Whether a solution is technically possible.")
                )));
    }

    @Test
    public void documentIntroduction() throws Exception {
        var instructionsBuilder = new InstructionsBuilder();
        instructionsBuilder.IF(conditionBuilder -> conditionBuilder.include(List.of(new PUSH(BigInteger.valueOf(2).toByteArray()),
                                                                                    new PUSH(BigInteger.valueOf(1).toByteArray()),
                                                                                    new LOAD(CALL_DATA),
                                                                                    new DIVIDE(),
                                                                                    new PUSH(BigInteger.valueOf(12345).toByteArray()),
                                                                                    new EQUALS())),
                               contentBuilder -> contentBuilder.include(List.of(new HALT(WINNER))));
        var request = new InstructionsController.ProgramRequest(instructionsBuilder.build());
        this.mockMvc.perform(post("/api/instructions/attack?targetMethod=winner")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .content(this.objectMapper.writeValueAsString(request)))
                .andDo(document("introduction-attack"));
    }

}
