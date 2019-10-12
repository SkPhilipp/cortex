package com.hileco.cortex.symbolic

// multiply & divide & modulo & conditional operations & bitwise operations
//    if (left is Value && right is Value) {
//            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
//            return Value(result.toLong())
//        }
//        return Modulo(left, right)
//    }

// add
//                 if (left is Value && right is Value) {
//                    val result = left.constant.toBigInteger().add(right.constant.toBigInteger()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
//                    stack.push(Value(result.toLong()))
//                } else if (left is Add && right is Value) {
//                    if (left.left is Value) {
//                        val result = left.left.constant.toBigInteger().add(right.constant.toBigInteger()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
//                        stack.push(Add(left.right, Value(result.toLong())))
//                    } else if (left.right is Value) {
//                        val result = left.right.constant.toBigInteger().add(right.constant.toBigInteger()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
//                        stack.push(Add(left.left, Value(result.toLong())))
//                    }
//                } else if (right is Add && left is Value) {
//                    if (right.left is Value) {
//                        val result = right.left.constant.toBigInteger().add(left.constant.toBigInteger()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
//                        stack.push(Add(right.right, Value(result.toLong())))
//                    } else if (right.right is Value) {
//                        val result = right.right.constant.toBigInteger().add(left.constant.toBigInteger()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
//                        stack.push(Add(right.left, Value(result.toLong())))
//                    }
//                } else {
//                    stack.push(Add(left, right))
//                }

// subtract
//         if (left is Value && right is Value) {
//            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
//            return Value(result.toLong())
//        } else if (right is Value) {
//            if (right.constant == 0L) {
//                return left
//            } else if (left is Subtract) {
//                if (left.left is Value) {
//                    val result = calculate(left.left.constant.toBigInteger(), right.constant.toBigInteger())
//                    return Subtract(Value(result.toLong()), left.right)
//                } else if (left.right is Value) {
//                    val result = calculateAdd(left.right.constant.toBigInteger(), right.constant.toBigInteger())
//                    return Subtract(left.left, Value(result.toLong()))
//                }
//            }
//        } else if (left is Value) {
//            if (left.constant == 0L) {
//                return right
//            } else if (right is Subtract) {
//                if (right.left is Value) {
//                    val result = calculate(right.left.constant.toBigInteger(), left.constant.toBigInteger())
//                    return Subtract(right.right, Value(result.toLong()))
//                } else if (right.right is Value) {
//                    val result = calculateAdd(right.right.constant.toBigInteger(), left.constant.toBigInteger())
//                    return Subtract(Value(result.toLong()), right.left)
//                }
//            }
//        }
//        return Subtract(left, right)