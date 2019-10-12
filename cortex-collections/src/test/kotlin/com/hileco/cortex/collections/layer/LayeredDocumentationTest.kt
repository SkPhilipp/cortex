package com.hileco.cortex.collections.layer

import com.hileco.cortex.documentation.Documentation
import org.junit.Test

class LayeredDocumentationTest {
    @Test
    fun document() {
        Documentation.of("layered-collections")
                .headingParagraph("Layered Implementation")
                .paragraph(("Layered collections allow for immediate and low-cost copies." +
                        " Supported implementations are:"))
                .source(" LayeredVmMap")
                .source("LayeredVmSet")
                .source("LayeredVmStack")
    }
}