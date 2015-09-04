package com.github.t1.exap.reflection;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public interface ReflectionMessageTarget {
    public default List<String> getMessages(Kind kind) {
        return ((ReflectionMessager) getProcessingEnv().getMessager()).getMessages(getElement(), kind);
    }

    public ProcessingEnvironment getProcessingEnv();

    public Element getElement();
}
