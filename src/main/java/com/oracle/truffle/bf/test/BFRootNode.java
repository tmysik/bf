/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;

public class BFRootNode extends RootNode {

    @Node.Children
    private final OperationNode[] children;


    public BFRootNode(BFRootNode node) {
        this(node.children);
    }

    BFRootNode(OperationNode[] children) {
        super(BFLanguage.class, null, null);
        this.children = children;
    }

    @ExplodeLoop
    @Override
    public Object execute(VirtualFrame vf) {
        assert BFLanguage.INSTANCE.findContext() != null;
        Memory memory = new Memory();
        for (OperationNode child : children) {
            child.execute(memory);
        }
        return null;
    }

}
