package com.arneam;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassDependencies {

    private File projectDir;
    private Map<String, Set<String>> dependencies = new HashMap<>();
    private String regex;

    public ClassDependencies(File projectDir) {
        this.projectDir = projectDir;
    }

    public ClassDependencies all() {
        return load(".*");
    }

    public ClassDependencies allWith(String regex) {
        return load(regex);
    }

    private ClassDependencies load(String regex) {
        this.regex = regex;
        loadData();
        return this;
    }

    public Map<String, Set<String>> data() {
        return dependencies;
    }

    public Set<String> nodes() {
        return dependencies.keySet();
    }

    public Set<Pair<String, String>> edges() {
        Set<Pair<String, String>> edgeItems = new HashSet<>();
        dependencies.forEach((k, v) -> {
            v.forEach(item -> {
                edgeItems.add(new ImmutablePair(k, item));
            });
        });
        return edgeItems;
    }

    String getFullQualifiedNameOfClassFile(String pkg, File file) {
        return pkg + "." + getClassNameFromFile(file);
    }

    String getClassNameFromFile(File file) {
        return file.getName().split("\\.")[0];
    }

    private void loadData() {
        dependencies.clear();

        DirExplorer explorer = new DirExplorer(
            (level, path, file) -> path.endsWith(".java"),
            (level, path, file) -> {
                try {
                    new VoidVisitorAdapter<Object>() {
                        String className;

                        @Override
                        public void visit(PackageDeclaration n, Object arg) {
                            super.visit(n, arg);
                            className = getFullQualifiedNameOfClassFile(n.getName().toString(), file);
                        }

                        @Override
                        public void visit(ImportDeclaration n, Object arg) {
                            super.visit(n, arg);
                            try {
                                if (n.getName().toString().matches(regex)) {
                                    Set<String> deps = dependencies.get(className);
                                    if (deps == null) {
                                        deps = new HashSet<>();
                                        dependencies.put(className, deps);
                                    }
                                    deps.add(n.getNameAsString());
                                }
                            } catch(Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }.visit(JavaParser.parse(file), null);
                } catch (IOException e) {
                    new RuntimeException(e);
                }
            });

        explorer.explore(projectDir);
    }

    public String toCSVFormat(Set<String> nodes, String separator) {
        return nodes().stream().map( it -> it + separator).reduce("", String::concat);
    }

    public String toCSVFormat(Set<Pair<String, String>> edges, String edgeSeparator, String separator) {
        return edges().stream().map( it -> it.getKey() + edgeSeparator + it.getValue() + separator)
                .reduce("", String::concat);
    }

    public void writeNodesIntoCSVFile(Set<String> nodes, String separator, String path) {
        Charset charset = Charset.forName("UTF-8");
        String s = toCSVFormat(nodes, separator);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), charset)) {
            writer.write(s, 0, s.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeEdgesIntoCSVFile(Set<Pair<String, String>> edges, String edgeSeparator,
                                      String separator, String path) {
        Charset charset = Charset.forName("UTF-8");
        String s = toCSVFormat(edges, edgeSeparator, separator);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), charset)) {
            writer.write(s, 0, s.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
