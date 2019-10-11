package com.hileco.cortex.symbolic

import com.hileco.cortex.vm.ProgramStoreZone

sealed class Expression {

    companion object {
        private val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == True || (it is Value && it.constant >= 0) }
        private val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == False || it == Value(0) }
        fun constructAnd(inputs: List<Expression>): Expression {
            val distinctInputs = inputs.distinct().filterNot(IS_EQUIVALENT_TRUE)
            val falseInputs = distinctInputs.count(IS_EQUIVALENT_FALSE)
            when {
                falseInputs > 0 -> return False
                distinctInputs.isEmpty() -> return True
                distinctInputs.size == 1 -> return distinctInputs.single()
            }
            return And(distinctInputs)
        }

        fun constructOr(inputs: List<Expression>): Expression {
            val distinctInputs = inputs.distinct().filterNot(IS_EQUIVALENT_FALSE)
            val trueInputs = distinctInputs.count(IS_EQUIVALENT_TRUE)
            when {
                trueInputs > 0 -> return True
                distinctInputs.isEmpty() -> return True
                distinctInputs.size == 1 -> return distinctInputs.single()
            }
            return Or(distinctInputs)
        }
    }

}

data class Reference(val type: ProgramStoreZone,
                     val address: Expression) : Expression() {
    override fun toString(): String {
        return "$type[$address]"
    }
}

data class Value(val constant: Long) : Expression() {
    override fun toString(): String {
        return constant.toString()
    }
}

data class Stack(val address: Int = 0) : Expression() {
    override fun toString(): String {
        return "STACK[$address]"
    }
}

data class Not(val input: Expression) : Expression() {
    override fun toString(): String {
        return "!($input)"
    }
}

data class And(val inputs: List<Expression>) : Expression() {
    override fun toString(): String {
        return inputs.joinToString(separator = " && ", prefix = "(", postfix = ")") { "$it" }
    }
}

data class Or(val inputs: List<Expression>) : Expression() {
    override fun toString(): String {
        return inputs.joinToString(separator = " || ", prefix = "(", postfix = ")") { "$it" }
    }
}

object True : Expression() {
    override fun toString(): String {
        return "TRUE"
    }
}

object False : Expression() {
    override fun toString(): String {
        return "FALSE"
    }
}

data class Add(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left + $right)"
    }
}

data class Subtract(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left - $right)"
    }
}

data class Multiply(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left * $right)"
    }
}

data class Divide(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left / $right)"
    }
}

data class LessThan(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left < $right)"
    }
}

data class GreaterThan(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left > $right)"
    }
}

data class Equals(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left == $right)"
    }
}

data class BitwiseOr(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left || $right)"
    }
}

data class BitwiseAnd(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left && $right)"
    }
}

data class Modulo(val left: Expression, val right: Expression) : Expression() {
    override fun toString(): String {
        return "($left % $right)"
    }
}

data class IsZero(val input: Expression) : Expression() {
    override fun toString(): String {
        return "0 == $input"
    }
}

data class Hash(val input: Expression, val method: String) : Expression() {
    override fun toString(): String {
        return "HASH_$method($input)"
    }
}
