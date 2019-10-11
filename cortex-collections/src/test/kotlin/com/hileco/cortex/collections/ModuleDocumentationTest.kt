package com.hileco.cortex.collections

import com.hileco.cortex.documentation.Documentation
import org.junit.Test

class ModuleDocumentationTest {
    @Test
    fun document() {
        Documentation.of("module")
                .headingParagraph("Interfaces")
                .source(" VmByteArray")
                .source("VmMap")
                .source("VmSet")
                .source("VmStack")
    }
}