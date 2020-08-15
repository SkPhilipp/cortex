package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.BlockModel
import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

class GethContractLoader {
    /**
     * TODO: Replace mock implementation
     */
    fun load(networkModel: NetworkModel, blockModel: BlockModel): List<GethContract> {
        return listOf(GethContract(
                "6080604052600436106100295760003560e01c806367df93f21461002e578063ba0bba4014610086575b600080fd5b6100846004803603606081101561004457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919080359060200190929190505050610116565b005b34801561009257600080fd5b5061009b610166565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100db5780820151818401526020810190506100c0565b50505050905090810190601f1680156101085780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6130396002828161012357fe5b041415610161578273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f19350505050505b505050565b60606040518060400160405280600681526020017f7468616e6b73000000000000000000000000000000000000000000000000000081525090509056fea165627a7a7230582037c5b11af10f1fc101b283afa78b94007e820efb8fdbfafc64bac34bb70d7a130029",
                blockModel.number.toString().padStart(32, '0'),
                blockModel.number.toString().padStart(32, '0'),
                BigDecimal.ONE
        ))
    }
}