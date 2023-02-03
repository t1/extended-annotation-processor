package com.github.t1.exap.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import java.util.ArrayList;
import java.util.List;

import static com.github.t1.exap.reflection.Message.ANY_ELEMENT;
import static com.github.t1.exap.reflection.Message.NO_ELEMENT;

class ReflectionMessager implements Messager {
    private static final Logger log = LoggerFactory.getLogger(ReflectionMessager.class);

    private final List<Message> messages = new ArrayList<>();

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
        message(NO_ELEMENT, kind, msg);
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

    List<String> getMessages(Elemental element, Kind kind) {
        List<String> list = new ArrayList<>();
        for (Message message : messages)
            if ((ANY_ELEMENT.equals(element) || message.getElemental().equals(element))
                && message.getKind().equals(kind))
                list.add(message.getText().toString());
        return list;
    }

    List<Message> getMessages() {
        return messages;
    }

    public void message(Elemental elemental, Kind kind, CharSequence message) {
        log.debug("{}: {}: {}", kind, elemental, message);
        messages.add(new Message(elemental, kind, message));
    }
}
