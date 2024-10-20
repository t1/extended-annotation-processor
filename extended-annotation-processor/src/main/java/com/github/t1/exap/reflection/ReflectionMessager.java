package com.github.t1.exap.reflection;

import com.github.t1.exap.insight.Elemental;
import com.github.t1.exap.insight.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ReflectionMessager implements Messager {
    private static final Logger log = LoggerFactory.getLogger(ReflectionMessager.class);

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
        message(null, kind, msg);
        switch (kind) {
            case ERROR:
                log.error(msg.toString());
                break;
            case MANDATORY_WARNING:
            case WARNING:
                log.warn(msg.toString());
                break;
            case NOTE:
                log.info(msg.toString());
                break;
            case OTHER:
                log.debug(msg.toString());
                break;
        }
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e) {
        assert e == null : "messages for elements should go via Elemental#message()";
        printMessage(kind, msg + " ### " + e);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        assert e == null : "messages for elements should go via Elemental#message()";
        printMessage(kind, msg + " ### " + e + " # " + a);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        assert e == null : "messages for elements should go via Elemental#message()";
        printMessage(kind, msg + " ### " + e + " # " + a + "=" + v);
    }

    /**
     * get all messages of that kind (or null for all kinds) for that element (or null for all elements)
     */
    List<String> getMessages(Elemental element, Kind kind) {
        return messages.stream()
                .filter(message -> (element == null || message.getElemental().equals(element))
                                   && (kind == null || message.getKind().equals(kind)))
                .map(message -> message.getText().toString()).collect(Collectors.toList());
    }

    List<Message> getMessages() {
        return messages;
    }

    public void message(Elemental elemental, Kind kind, CharSequence message) {
        if (elemental == null)
            log.debug("{}: {}", kind, message);
        else
            log.debug("{}: {}: {}", kind, elemental, message);
        messages.add(new Message(elemental, kind, message));
    }
}
