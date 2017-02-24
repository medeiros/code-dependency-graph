package com.arneam;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        Set<String> nodes = dependencies.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toSet());
        nodes.addAll(dependencies.keySet());
        return nodes;
    }

    public Set<Pair<String, String>> edges() {
        Set<Pair<String, String>> edgeItems = new HashSet<>();
        dependencies.forEach((k, v) -> v.forEach(item -> edgeItems.add(new ImmutablePair(k, item))));
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

    public void generateGraphMLFromCSVFiles(String nodesCSVFile, String edgesCSVFile, String xmlFile) {
        // according to https://en.wikipedia.org/wiki/GraphML and
        // https://gephi.org/users/supported-graph-formats/graphml-format/

        List<String> nodes;
        List<String> edges;

        try {
            nodes = Files.readAllLines(Paths.get(nodesCSVFile));
            edges = Files.readAllLines(Paths.get(edgesCSVFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Charset charset = Charset.forName("UTF-8");

        XMLTag doc = XMLDoc.from(Paths.get("./template.graphml").toFile())
            .addTag("key")
                .addAttribute("id", "d1")
                .addAttribute("for", "edge")
                .addAttribute("attr.name", "weight")
                .addAttribute("attr.type", "double")
                .gotoParent()
            .addTag("graph")
                .addAttribute("id", "G")
                .addAttribute("edgedefault", "undirected");

        nodes.forEach(node -> {
            int nodeSize = node.split("\\.").length;
            String simplifiedNode = node;

            // todo: add unit test (scenario not covered)
            if (StringUtils.countMatches(simplifiedNode, ".") >= 2) {
                simplifiedNode = node.split("\\.")[nodeSize - 2] + "." + node.split("\\.")[nodeSize - 1];
            }

            doc.addTag("node").addAttribute("id", simplifiedNode).gotoParent();
        });

        for (int i = 0; i < edges.size(); i++) {
            String edgeSource = edges.get(i).split(";")[0];
            String edgeTarget = edges.get(i).split(";")[1];

            int edgeSourceSize = edgeSource.split("\\.").length;
            int edgeTargetSize = edgeTarget.split("\\.").length;

            // todo: add unit test (scenario not covered)
            if (StringUtils.countMatches(edgeSource, ".") >= 2) {
                edgeSource = edgeSource.split("\\.")[edgeSourceSize - 2] + "." + edgeSource.split("\\.")[edgeSourceSize - 1];
            }
            // todo: add unit test (scenario not covered)
            if (StringUtils.countMatches(edgeTarget, ".") >= 2) {
                edgeTarget = edgeTarget.split("\\.")[edgeTargetSize - 2] + "." + edgeTarget.split("\\.")[edgeTargetSize - 1];
            }

            doc.addTag("edge")
                .addAttribute("id", "e" + i)
                .addAttribute("source", edgeSource)
                .addAttribute("target", edgeTarget)
                .addTag("data")
                .addAttribute("key", "d1")
                .addText("1.0")
                .gotoParent();
        }

//        edges.forEach(edge -> {
//            doc.addTag("edge")
//                .addAttribute("id", "e" + new Random().nextInt(100))
//                .addAttribute("source", edge.split(";")[0])
//                .addAttribute("target", edge.split(";")[1])
//                .addTag("data")
//                    .addAttribute("key", "d1")
//                    .addText("1.0")
//                .gotoParent();
//        });

        String s = doc.toString();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(xmlFile), charset)) {
            writer.write(s, 0, s.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
