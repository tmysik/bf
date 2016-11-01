/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Instrumentable;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

@Instrumentable(factory = BFRootNodeWrapper.class)
public class BFRootNode extends RootNode {

    @Node.Children
    private final OperationNode[] children;
    private final SourceSection sourceSection;


    public BFRootNode(BFRootNode node) {
        this(node.children, node.sourceSection);
    }

    BFRootNode(OperationNode[] children, SourceSection sourceSection) {
        super(BFLanguage.class, null, null);
        this.children = children;
        this.sourceSection = sourceSection;
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

    @Override
    public SourceSection getSourceSection() {
        return sourceSection;
    }

    @Override
    protected boolean isTaggedWith(Class<?> tag) {
        if (tag == StandardTags.RootTag.class) {
            return true;
        }
        return super.isTaggedWith(tag);
    }

}
