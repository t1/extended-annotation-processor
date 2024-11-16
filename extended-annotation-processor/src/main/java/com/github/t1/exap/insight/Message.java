package com.github.t1.exap.insight;

import javax.tools.Diagnostic;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.OTHER;
import static javax.tools.Diagnostic.Kind.WARNING;

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

    public boolean isError() {return is(ERROR);}

    public boolean isWarning() {return is(WARNING);}

    public boolean isMandatory_warning() {return is(MANDATORY_WARNING);}

    public boolean isNote() {return is(NOTE);}

    public boolean isOther() {return is(OTHER);}

    private boolean is(Diagnostic.Kind kind) {return getKind() == kind;}

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
