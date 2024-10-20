package com.github.t1.exap.insight;

import javax.tools.Diagnostic;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Message {
    private final Elemental elemental;
    private final Diagnostic.Kind kind;
    private final CharSequence text;

    public Message(Elemental elemental, Diagnostic.Kind kind, CharSequence text) {
        this.elemental = elemental;
        this.kind = requireNonNull(kind, "kind");
        this.text = requireNonNull(text, "text");
    }

    public Diagnostic.Kind getKind() {return kind;}

    public Elemental getElemental() {return elemental;}

    public CharSequence getText() {return text;}

    @Override public boolean equals(Object o) {
        return this == o
               || o instanceof Message that
                  && Objects.equals(this.elemental, that.elemental)
                  && this.kind == that.kind
                  && this.text.equals(that.text);
    }

    @Override public int hashCode() {return Objects.hash(elemental, kind, text);}

    @Override
    public String toString() {
        return "Message [" + elemental + ":" + kind + ":" + text + "]";
    }
}
