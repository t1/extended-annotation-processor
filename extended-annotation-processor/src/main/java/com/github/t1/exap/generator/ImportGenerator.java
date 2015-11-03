package com.github.t1.exap.generator;

import java.io.PrintWriter;
import java.util.*;

import com.github.t1.exap.reflection.Type;

public class ImportGenerator {
    private final Set<Type> imports = new TreeSet<>(Comparator.comparing(c -> c.getFullName()));

    public void add(Type type) {
        if (requiresImport(type))
            imports.add(type);
    }

    private boolean requiresImport(Type type) {
        if (type.getPackage() == null || "java.lang".equals(type.getPackage().getName()))
            return false;
        return true;
    }

    public void print(PrintWriter out) {
        printImportGroup(out, "java");
        printImportGroup(out, "javax");
        printImportGroup(out, "org");
        printImportGroup(out, "com");
    }

    private void printImportGroup(PrintWriter out, String groupName) {
        List<String> actualImports = new ArrayList<>();
        for (Type type : this.imports)
            if (type.getFullName().startsWith(groupName + ".")) {
                actualImports.add(type.getFullName().replace('$', '.'));
            }
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

    private String stripLastItem(String typeName) {
        int i = typeName.lastIndexOf('.');
        if (i >= 0)
            typeName = typeName.substring(0, i + 1);
        return typeName;
    }

    private boolean removeOther(List<String> typeNames, String stripped) {
        boolean removed = false;
        for (Iterator<String> iter = typeNames.iterator(); iter.hasNext();) {
            String typeName = iter.next();
            if (stripLastItem(typeName).equals(stripped)) {
                iter.remove();
                removed = true;
            }
        }
        return removed;
    }
}
