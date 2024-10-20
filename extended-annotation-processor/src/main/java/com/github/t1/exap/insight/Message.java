package com.github.t1.exap.insight;

import javax.tools.Diagnostic;

import static java.util.Objects.requireNonNull;

public class Message {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + elemental.hashCode();
        result = prime * result + kind.hashCode();
        result = prime * result + text.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message that = (Message) obj;
        return this.elemental.equals(that.elemental)
               && this.kind == that.kind
               && this.text.equals(that.text);
    }

    @Override
    public String toString() {
        return "Message [" + elemental + ":" + kind + ":" + text + "]";
    }
}
