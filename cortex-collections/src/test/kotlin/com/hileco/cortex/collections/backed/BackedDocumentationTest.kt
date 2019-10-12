package com.hileco.cortex.collections.backed

import com.hileco.cortex.documentation.Documentation
import org.junit.Test

class BackedDocumentationTest {
    @Test
    fun document() {
        Documentation.of("backed-collections")
                .headingParagraph("Backed Implementation")
                .paragraph(("Backed collections allow for direct access to memory and are backed by standard Java collections." +
                        " Supported implementations are:"))
                .source(" BackedVmByteArray")
                .source(" BackedVmMap")
                .source("BackedVmSet")
                .source("BackedVmStack")
    }
}