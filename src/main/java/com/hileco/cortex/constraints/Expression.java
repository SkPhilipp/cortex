package com.hileco.cortex.constraints;

import lombok.Data;

@Data
public class Expression {
    public enum Type {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        LESS_THAN,
        GREATER_THAN,
        EQUAL_TO,
        NOT_EQUAL_TO,
        OR,
        AND,
        MODULO,
        HASH,
        REFERENCE,
        VALUE
    }

    private Expression(Type type, Expression left, Expression right, Reference reference, Long constant) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.reference = reference;
        this.constant = constant;
    }

    public static Expression operation(Type type, Expression left, Expression right) {
        return new Expression(type, left, right, null, null);
    }

    public static Expression reference(Reference reference) {
        return new Expression(Type.REFERENCE, null, null, reference, null);
    }

    public static Expression value(Long constant) {
        return new Expression(Type.VALUE, null, null, null, constant);
    }

    private Type type;
    private Expression left;
    private Expression right;
    private Reference reference;
    private Long constant;
}
