/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.truffle.bf.test;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.debug.Breakpoint;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.debug.DebuggerSession;
import com.oracle.truffle.api.debug.SuspendedCallback;
import com.oracle.truffle.api.debug.SuspendedEvent;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.vm.PolyglotEngine;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public final class BFMain {

    // XXX debug does not work, NPE
    private static final boolean DEBUG = false;


    private BFMain() {
    }

    /**
     * The main entry point.
     */
    public static void main(String... args) throws IOException {
        Source source;
        if (args.length == 0) {
            // @formatter:off
            source = Source.newBuilder(new InputStreamReader(System.in)).
                name("<stdin>").
                mimeType(BFLanguage.MIME_TYPE).
                build();
            // @formatter:on
        } else {
            source = Source.newBuilder(BFMain.class.getResource("/test/" + args[0]))
                    .mimeType(BFLanguage.MIME_TYPE)
                    .build();
        }
        long time = System.currentTimeMillis();
        executeSource(source, System.in, System.out, DEBUG);
        System.out.println("");
        System.out.println("Elapsed " + (System.currentTimeMillis() - time) + "ms");
    }

    private static void executeSource(Source source, InputStream in, PrintStream out, boolean debug) {
        out.println("== running on " + Truffle.getRuntime().getName());

        PolyglotEngine engine = PolyglotEngine.newBuilder().setIn(in).setOut(out).build();
        assert engine.getLanguages().containsKey(BFLanguage.MIME_TYPE);

        try {
            if (debug) {
                Debugger debugger = Debugger.find(engine);
                final SuspendedCallback callback = new SuspendedCallback() {
                    @Override
                    public void onSuspend(SuspendedEvent event) {
                        System.out.println("------------------- suspended");
                        event.prepareStepInto(1);
                    }
                };
                try (DebuggerSession session = debugger.startSession(callback)) {
                    session.install(Breakpoint.newBuilder(source).lineIs(1).build());
                    engine.eval(source);
                }
            } else {
                engine.eval(source);
            }
        } catch (Throwable ex) {
            /*
             * PolyglotEngine.eval wraps the actual exception in an IOException, so we have to
             * unwrap here.
             */
            Throwable cause = ex.getCause();
            if (cause instanceof UnsupportedSpecializationException) {
                out.println((UnsupportedSpecializationException) cause);
//                out.println(formatTypeError((UnsupportedSpecializationException) cause));
            } else {
                /* Unexpected error, just print out the full stack trace for debugging purposes. */
                ex.printStackTrace(out);
            }
        }

        engine.dispose();
    }

    /**
     * Provides a user-readable message for run-time type errors. SL is strongly typed, i.e., there
     * are no automatic type conversions of values. Therefore, Truffle does the type checking for
     * us: if no matching node specialization for the actual values is found, then we have a type
     * error. Specialized nodes use the {@link UnsupportedSpecializationException} to report that no
     * specialization was found. We therefore just have to convert the information encapsulated in
     * this exception in a user-readable form.
     */
//    public static String formatTypeError(UnsupportedSpecializationException ex) {
//        StringBuilder result = new StringBuilder();
//        result.append("Type error");
//        if (ex.getNode() != null && ex.getNode().getSourceSection() != null) {
//            SourceSection ss = ex.getNode().getSourceSection();
//            if (ss != null && ss.getSource() != null) {
//                result.append(" at ").append(ss.getSource().getName()).append(" line ").append(ss.getStartLine()).append(" col ").append(ss.getStartColumn());
//            }
//        }
//        result.append(": operation");
//        if (ex.getNode() != null) {
//            NodeInfo nodeInfo = BFContext.lookupNodeInfo(ex.getNode().getClass());
//            if (nodeInfo != null) {
//                result.append(" \"").append(nodeInfo.shortName()).append("\"");
//            }
//        }
//        result.append(" not defined for");
//
//        String sep = " ";
//        for (int i = 0; i < ex.getSuppliedValues().length; i++) {
//            Object value = ex.getSuppliedValues()[i];
//            Node node = ex.getSuppliedNodes()[i];
//            if (node != null) {
//                result.append(sep);
//                sep = ", ";
//
//                if (value instanceof Long || value instanceof BigInteger) {
//                    result.append("Number ").append(value);
//                } else if (value instanceof Boolean) {
//                    result.append("Boolean ").append(value);
//                } else if (value instanceof String) {
//                    result.append("String \"").append(value).append("\"");
//                } else if (value instanceof SLFunction) {
//                    result.append("Function ").append(value);
//                } else if (value == SLNull.SINGLETON) {
//                    result.append("NULL");
//                } else if (value == null) {
//                    // value is not evaluated because of short circuit evaluation
//                    result.append("ANY");
//                } else {
//                    result.append(value);
//                }
//            }
//        }
//        return result.toString();
//    }

}
