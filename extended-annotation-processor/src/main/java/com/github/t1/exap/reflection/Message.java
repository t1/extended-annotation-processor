package com.github.t1.exap.reflection;

import static java.util.Objects.*;

import javax.tools.Diagnostic;

public class Message {
    public static final Elemental NO_ELEMENT = new Elemental(null, null) {
        @Override
        public String toString() {
            return "#NO_ELEMENT#";
        }
    };
    public static final Elemental ANY_ELEMENT = new Elemental(null, null) {
        @Override
        public String toString() {
            return "#ANY_ELEMENT#";
        }
    };

    private final Elemental elemental;
    private final Diagnostic.Kind kind;
    private final CharSequence text;

    public Message(Elemental elemental, Diagnostic.Kind kind, CharSequence text) {
        this.elemental = requireNonNull(elemental, "elemental");
        this.kind = requireNonNull(kind, "kind");
        this.text = requireNonNull(text, "text");
    }

    public Diagnostic.Kind getKind() {
        return kind;
    }

    public Elemental getElemental() {
        return elemental;
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message [" + elemental + ":" + kind + ":" + text + "]";
    }
}
