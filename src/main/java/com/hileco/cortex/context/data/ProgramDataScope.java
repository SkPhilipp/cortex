package com.hileco.cortex.context.data;

// TODO: Embed scope into values on the tree
// TODO: Embed scope into values on the stack
public enum ProgramDataScope {
    /**
     * Example: An unchanging number
     */
    STATIC,
    /**
     * Example: An SSH server's public key
     */
    DEPLOYMENT,
    /**
     * Example: A process ID assigned by an OS
     */
    EXECUTION,
    /**
     * Example: Weather
     */
    CHANGING,
}
