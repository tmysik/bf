/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.bf.BFParser;
import java.io.IOException;
import java.util.Arrays;

public class OperationNode extends Node {

    private final BFParser.OpCode opCode;
    @Node.Children
    private final OperationNode[] children;
    private final BranchProfile expansion = BranchProfile.create();


    OperationNode(BFParser.OpCode opCode, OperationNode[] children) {
        this.opCode = opCode;
        this.children = children;
    }

    public void execute(Memory memory) {
        try {
            switch(opCode) {
                case LEFT:
                    memory.index--;
                    break;
                case RIGHT:
                    int i = memory.index + 1;
                    if (i >= memory.cells.length) {
                        expansion.enter();
                        memory.cells = Arrays.copyOf(memory.cells, memory.cells.length * 2);
                    }
                    memory.index = i;
                    break;
                case INC:
                    memory.cells[memory.index]++;
                    break;
                case DEC:
                    memory.cells[memory.index]--;
                    break;
                case IN:
                    memory.cells[memory.index] = input();
                    break;
                case OUT:
                    output(memory.cells[memory.index]);
                    break;
                case REPEAT:
                    while (memory.cells[memory.index] > 0) {
                        executeChildren(memory);
                    }
                    break;
                default:
                    assert false : "Unknown operation: " + opCode;
            }
        } catch (IOException ex) {
            CompilerDirectives.transferToInterpreter();
        }
    }

    @CompilerDirectives.TruffleBoundary
    private int input() throws IOException {
        return BFLanguage.INSTANCE.findContext().getInput().read();
    }

    @CompilerDirectives.TruffleBoundary
    private void output(int value) throws IOException {
        // XXX why it doesn't work?!
        //BFLanguage.INSTANCE.findContext().getOutput().write(value);
        System.out.print((char) value);
    }

    @ExplodeLoop
    private void executeChildren(Memory memory) {
        for (OperationNode child : children) {
            child.execute(memory);
        }
    }

    @Override
    public String toString() {
        return "OperationNode{" + "opCode=" + opCode + ", children=" + Arrays.toString(children) + '}';
    }

}