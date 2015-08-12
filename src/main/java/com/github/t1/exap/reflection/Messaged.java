package com.github.t1.exap.reflection;

import static javax.tools.Diagnostic.Kind.*;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

class Messaged {
    protected final Messager messager;
    private final Element element;

    public Messaged(Messager messager, Element element) {
        this.messager = messager;
        this.element = element;
    }

    public void error(CharSequence message) {
        messager.printMessage(ERROR, message, element);
    }

    public void warning(CharSequence message) {
        messager.printMessage(WARNING, message, element);
    }

    public void note(CharSequence message) {
        messager.printMessage(NOTE, message, element);
    }
}
