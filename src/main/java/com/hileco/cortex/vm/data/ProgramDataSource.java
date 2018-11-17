package com.hileco.cortex.vm.data;

import com.hileco.cortex.vm.Program;

public enum ProgramDataSource {
    /**
     * Example: Data of agent initiating the process
     */
    PROCESS,
    /**
     * Example: Address of the program
     */
    PROGRAM,
    /**
     * Example: 0x1234
     */
    FIXED,
    /**
     * Example: Timestamp of execution start
     */
    EXECUTION,
    /**
     * Example: Any value originating from {@link Program#storage}
     */
    STORAGE
}
