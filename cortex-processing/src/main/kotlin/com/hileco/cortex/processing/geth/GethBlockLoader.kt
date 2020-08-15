package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

class GethBlockLoader {
    /**
     * TODO: Replace mock implementation
     */
    fun load(networkModel: NetworkModel, block: BigDecimal): GethBlock {
        return GethBlock(block)
    }
}