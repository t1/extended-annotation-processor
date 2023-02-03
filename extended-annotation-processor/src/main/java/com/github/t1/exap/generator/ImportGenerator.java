package com.github.t1.exap.generator;

import com.github.t1.exap.reflection.Package;
import com.github.t1.exap.reflection.Type;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
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
                String stripped = stripLastItem(typeName);
                if (removeOther(imports, stripped))
                    typeName = stripped + "*";
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

    private String stripLastItem(String typeName) {
        int i = typeName.lastIndexOf('.');
        if (i >= 0)
            typeName = typeName.substring(0, i + 1);
        return typeName;
    }

    private boolean removeOther(Iterable<Type> typeNames, String stripped) {
        boolean removed = false;
        for (Iterator<Type> iter = typeNames.iterator(); iter.hasNext(); ) {
            String typeName = typeName(iter.next());
            if (stripLastItem(typeName).equals(stripped)) {
                iter.remove();
                removed = true;
            }
        }
        return removed;
    }
}
