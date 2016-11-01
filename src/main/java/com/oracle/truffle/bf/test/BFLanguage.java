/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.bf.BFParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@TruffleLanguage.Registration(name = "BF", version = "0.1", mimeType = BFLanguage.MIME_TYPE)
@ProvidedTags({StandardTags.StatementTag.class, StandardTags.RootTag.class, DebuggerTags.AlwaysHalt.class})
public final class BFLanguage extends TruffleLanguage<BFContext> {

    public static final String MIME_TYPE = "application/x-bf";

    public static final BFLanguage INSTANCE = new BFLanguage();


    private BFLanguage() {
    }

    public BFContext findContext() {
        CompilerAsserts.neverPartOfCompilation();
        return super.findContext(super.createFindContextNode());
    }

    @Override
    protected BFContext createContext(Env env) {
        BufferedReader in = new BufferedReader(new InputStreamReader(env.in()));
        PrintWriter out = new PrintWriter(env.out(), true);
        return new BFContext(in, out, env);
    }

    @Override
    protected CallTarget parse(Source source, Node node, String... strings) throws Exception {
        BFParser.Operation[] operations = new BFParser().parse(source.getInputStream());
        BFRootNode rootNode = new BFRootNode(prepareNodes(source, operations), source.createSection(0, source.getLength()));
        return Truffle.getRuntime().createCallTarget(rootNode);
    }

    @Override
    protected Object findExportedSymbol(BFContext context, String globalName, boolean bln) {
        return null;
    }

    @Override
    protected Object getLanguageGlobal(BFContext context) {
        return context;
    }

    @Override
    protected boolean isObjectOfLanguage(Object o) {
        return false;
    }

    @Override
    protected Object evalInContext(Source source, Node node, MaterializedFrame mf) throws Exception {
        throw new IllegalStateException("evalInContext not supported in BF");
    }

    private OperationNode[] prepareNodes(Source source, BFParser.Operation[] operations) {
        OperationNode[] nodes = new OperationNode[operations.length];
        for (int i = 0; i < nodes.length; i++) {
            final BFParser.OpCode code = operations[i].getCode();
            OperationNode[] children = null;
            if (code == BFParser.OpCode.REPEAT) {
                children = prepareNodes(source, ((BFParser.Repeat) operations[i]).getChildren());
            }
            nodes[i] = new OperationNode(code, children, source.createSection(i, 1));
        }
        return nodes;
    }

}
