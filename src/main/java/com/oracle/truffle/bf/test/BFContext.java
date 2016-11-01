/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.ExecutionContext;
import com.oracle.truffle.api.TruffleLanguage;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class BFContext extends ExecutionContext {

    private final BufferedReader input;
    private final PrintWriter output;
    private final TruffleLanguage.Env env;


    public BFContext(BufferedReader input, PrintWriter output, TruffleLanguage.Env env) {
        this.input = input;
        this.output = output;
        this.env = env;
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintWriter getOutput() {
        return output;
    }

}
