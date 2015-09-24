package com.github.t1.exap.reflection;

import static java.util.Collections.*;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.tools.Diagnostic.Kind;

import org.slf4j.*;

public class ReflectionMessager implements Messager {
    private static final Logger log = LoggerFactory.getLogger(ReflectionMessager.class);

    private final Map<Element, Map<Kind, List<String>>> messages = new HashMap<>();

    @Override
    public void printMessage(Kind kind, CharSequence msg) {
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
        storeMessage(e, kind, msg);
        printMessage(kind, msg + " ### " + e);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        storeMessage(e, kind, msg);
        printMessage(kind, msg + " ### " + e + " # " + a);
    }

    @Override
    public void printMessage(Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        storeMessage(e, kind, msg);
        printMessage(kind, msg + " ### " + e + " # " + a + "=" + v);
    }

    private void storeMessage(Element element, Kind kind, CharSequence msg) {
        Map<Kind, List<String>> map = messages.get(element);
        if (map == null) {
            map = new EnumMap<>(Kind.class);
            messages.put(element, map);
        }

        List<String> list = map.get(kind);
        if (list == null) {
            list = new ArrayList<>();
            map.put(kind, list);
        }

        list.add(msg.toString());
    }

    public List<String> getMessages(Element element, Kind kind) {
        Map<Kind, List<String>> map = messages.get(element);
        if (map == null)
            return emptyList();
        List<String> list = map.get(kind);
        return (list == null) ? emptyList() : unmodifiableList(list);
    }

    public Map<Kind, List<String>> getMessages(Element element) {
        Map<Kind, List<String>> map = messages.get(element);
        if (map == null)
            return emptyMap();
        return map;
    }

    public Map<Element, Map<Kind, List<String>>> getMessages() {
        return messages;
    }

    public Map<Element, List<String>> getMessages(Kind kind) {
        Map<Element, List<String>> result = new HashMap<>();
        for (Entry<Element, Map<Kind, List<String>>> elementEntry : messages.entrySet())
            for (Entry<Kind, List<String>> kindEntry : elementEntry.getValue().entrySet())
                if (kindEntry.getKey().compareTo(kind) >= 0)
                    result.put(elementEntry.getKey(), kindEntry.getValue());
        return result;
    }
}
