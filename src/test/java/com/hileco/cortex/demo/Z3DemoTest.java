package com.hileco.cortex.demo;

import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;

import java.util.Arrays;

class Z3DemoTest {

    public static final String CALL_DATA_0 = "call_data_0";

    /**
     * The "Solve for ((call_data[0] + 10) % 0xffffff < 10)", implemented.
     *
     * Install Z3 with:
     * - Download & unzip release 4.7.1
     * - Add the bin folder to $PATH
     * - `mvn install:install-file -Dfile=z3-4.7.1-x64-win/bin/com.microsoft.z3.jar -DgroupId=com.microsoft.z3 -DartifactId=z3 -Dversion=4.7.1 -Dpackaging=jar`
     */
    public static void main(String[] args) {
        var context = new Context();
        var callData0Symbol = context.mkSymbol(CALL_DATA_0);
        var callData0 = context.mkIntConst(callData0Symbol);
        var callDataPlusTen = context.mkAdd(callData0, context.mkInt(10));
        var callDataPlusTenOverflowing = context.mkMod((IntExpr) callDataPlusTen, context.mkInt(16777215));
        var fullExpression = context.mkLt(callDataPlusTenOverflowing, context.mkInt(10));
        var solver = context.mkSolver();
        solver.add(fullExpression);
        solver.check();
        var model = solver.getModel();
        var constants = Arrays.stream(model.getConstDecls())
                .filter(funcDecl -> CALL_DATA_0.equals(funcDecl.getName().toString()))
                .findAny()
                .orElseThrow();
        var result = ((IntNum) model.getConstInterp(constants)).getInt();
        System.out.println(((result + 10) % 0xffffff < 10));
    }
}
