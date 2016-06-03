package com.github.t1.exap.generator;

import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

public class ImportGenerator {
    private static final List<String> ROOT_PACKAGES = asList("java", "javax", "org", "com");
    private static final String OTHER = "*";

    private final Package selfPackage;
    private final Set<Type> imports = new TreeSet<>(Comparator.comparing(Type::getFullName));

    public ImportGenerator(Package selfPackage) {
        this.selfPackage = selfPackage;
    }

    public void add(Type type) {
        if (requiresImport(type))
            imports.add(type);
    }

    private boolean requiresImport(Type type) {
        return !(type.getPackage() == null || "java.lang".equals(type.getPackage().getName())
                         || type.getPackage().equals(selfPackage));
    }

    public void print(PrintWriter out) {
        ROOT_PACKAGES.forEach(p -> printImportGroup(out, p));
        printImportGroup(out, OTHER);
    }

    private void printImportGroup(PrintWriter out, String groupName) {
        List<String> actualImports = this.imports
                .stream()
                .filter(type -> matches(type, groupName))
                .map(type -> type.getFullName().replace('$', '.').replaceAll("<.*>", ""))
                .collect(Collectors.toList());
        if (!actualImports.isEmpty()) {
            while (!actualImports.isEmpty()) {
                String actualImport = actualImports.remove(0);
                if (removeOther(actualImports, stripLastItem(actualImport)))
                    actualImport = stripLastItem(actualImport) + "*";
                out.println("import " + actualImport + ";");
            }
            out.println();
        }
    }

    private boolean matches(Type type, String groupName) {
        if (OTHER.equals(groupName))
            return !ROOT_PACKAGES.contains(type.getPackage().toPath().getName(0).toString());
        return type.getFullName().startsWith(groupName + ".");
    }

    private String stripLastItem(String typeName) {
        int i = typeName.lastIndexOf('.');
        if (i >= 0)
            typeName = typeName.substring(0, i + 1);
        return typeName;
    }

    private boolean removeOther(List<String> typeNames, String stripped) {
        boolean removed = false;
        for (Iterator<String> iter = typeNames.iterator(); iter.hasNext(); ) {
            String typeName = iter.next();
            if (stripLastItem(typeName).equals(stripped)) {
                iter.remove();
                removed = true;
            }
        }
        return removed;
    }
}
