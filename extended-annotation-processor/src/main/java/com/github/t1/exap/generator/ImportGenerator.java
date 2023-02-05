package com.github.t1.exap.generator;

import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.Type;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class ImportGenerator {
    private static final List<String> ROOT_PACKAGES = asList("java", "javax", "org", "com");
    private static final String OTHER = "*";

    private final Package selfPackage;
    private final Set<Type> imports = new TreeSet<>(Comparator.comparing(Type::getFullName));

    public ImportGenerator(Package selfPackage) {
        this.selfPackage = selfPackage;
    }

    public void add(Type type) {
        if (!isAutoImport(type))
            imports.add(type);
    }

    private boolean isAutoImport(Type type) {
        return type.getPackage() == null
               || "java.lang".equals(type.getPackage().getName())
               || type.getPackage().equals(selfPackage);
    }

    public void print(PrintWriter out) {
        ROOT_PACKAGES.forEach(p -> printImportGroup(out, p));
        printImportGroup(out, OTHER);
    }

    private void printImportGroup(PrintWriter out, String groupName) {
        boolean anyImports = false;
        for (int i = 0; i < imports.size(); i++) {
            Type type = new ArrayList<>(imports).get(i);
            if (matches(type, groupName)) {
                imports.remove(type);
                --i;
                anyImports = true;
                String typeName = typeName(type);
                out.println("import " + typeName + ";");
            }
        }
        if (anyImports)
            out.println();
    }

    private String typeName(Type type) {return type.getFullName().replace('$', '.').replaceAll("<.*>", "");}

    private boolean matches(Type type, String groupName) {
        if (OTHER.equals(groupName))
            return !ROOT_PACKAGES.contains(type.getPackage().toPath().getName(0).toString());
        return type.getFullName().startsWith(groupName + ".");
    }
}
