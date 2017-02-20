package com.arneam;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ClassDependenciesTest {

    //static final String FILE_PATH = "/home/daniel/code/opensource/code-dependency-graph/src/main/java/";
    static final String FILE_PATH = "/Users/dma/code/opensource/code-dependency-graph/src/main/java";

    File projectDir;
    ClassDependencies classDependencies;

    @Before
    public void setup() {
        projectDir = new File(FILE_PATH);
        classDependencies = new ClassDependencies(projectDir);
    }

    @After
    public void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get("./nodes.csv"));
//        Files.deleteIfExists(Paths.get("./edges.csv"));
    }

    @Test
    public void shouldGetClassNameFromFile() {
        File file = new File("/src/main/java/com/arneam/ClassDependencies.java");
        assertThat(classDependencies.getClassNameFromFile(file), equalTo("ClassDependencies"));
    }

    @Test
    public void shouldGetFQNFromClassFile() {
        String pkg = "com.arneam";
        File file = new File("/src/main/java/com/arneam/ClassDependencies.java");
        assertThat(classDependencies.getFullQualifiedNameOfClassFile(pkg, file),
                equalTo("com.arneam.ClassDependencies"));
    }

    @Test
    public void shouldLoadAllDependenciesForClassDependenciesClass() {
        Map<String, Set<String>> dependencies = classDependencies.all().data();
        assertThat(dependencies, hasEntry(is("com.arneam.ClassDependencies"), containsInAnyOrder(
            "com.github.javaparser.JavaParser",
            "com.github.javaparser.ast.ImportDeclaration",
            "com.github.javaparser.ast.PackageDeclaration",
            "com.github.javaparser.ast.visitor.VoidVisitorAdapter",
            "org.apache.commons.lang3.tuple.ImmutablePair",
            "org.apache.commons.lang3.tuple.Pair",
            "java.io.BufferedWriter",
            "java.io.File",
            "java.io.IOException",
            "java.nio.charset.Charset",
            "java.nio.file.Files",
            "java.nio.file.Paths",
            "java.util.HashMap",
            "java.util.HashSet",
            "java.util.Map",
            "java.util.Set"
        )));
    }

    @Test
    public void shouldLoadAllDependenciesForClassDependenciesWhichEndsWIthMap() {
        Map<String, Set<String>> dependencies = classDependencies.allWith(".*(Map)").data();
        assertThat(dependencies, hasEntry(is("com.arneam.ClassDependencies"), containsInAnyOrder(
                "java.util.HashMap",
                "java.util.Map"
        )));
    }

    @Test
    public void shouldLoadAllDependenciesForDirExplorerClass() {
        Map<String, Set<String>> dependencies = classDependencies.all().data();
        assertThat(dependencies, hasEntry(is("com.arneam.DirExplorer"), contains("java.io.File")));
    }

    @Test
    public void shouldGenerateNodesFromAllDependencies() { // also called vertex (plural: vertices)
        Set<String> nodes = classDependencies.all().nodes();
        assertThat(nodes, containsInAnyOrder("com.arneam.ClassDependencies", "com.arneam.DirExplorer"));
    }

    @Test
    public void shouldGenerateNodesFromFilteredDependencies() { // also called vertex (plural: vertices)
        Set<String> nodes = classDependencies.allWith(".*(Declaration)").nodes();
        assertThat(nodes, contains("com.arneam.ClassDependencies"));
    }

    @Test
    public void shouldGenerateEdgesFromAllDependencies() { // edge = pair of vertices or nodes
        Set<Pair<String, String>> edges = classDependencies.all().edges();
        assertThat(edges, containsInAnyOrder(
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                hasProperty("value", is("com.github.javaparser.JavaParser"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("com.github.javaparser.ast.ImportDeclaration"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("com.github.javaparser.ast.PackageDeclaration"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("com.github.javaparser.ast.visitor.VoidVisitorAdapter"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.IOException"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.HashMap"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.Map"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.Set"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.HashSet"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("org.apache.commons.lang3.tuple.ImmutablePair"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("org.apache.commons.lang3.tuple.Pair"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.BufferedWriter"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.charset.Charset"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.file.Files"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.file.Paths"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                hasProperty("value", is("java.io.File"))),
            allOf(hasProperty("key", is("com.arneam.DirExplorer")),
                    hasProperty("value", is("java.io.File")))
            )
        );
    }

    @Test
    public void shouldGenerateEdgesFromFilteredDependencies() {
        Set<Pair<String, String>> edges = classDependencies.allWith(".*(File)").edges();
        assertThat(edges, containsInAnyOrder(
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.File"))),
            allOf(hasProperty("key", is("com.arneam.DirExplorer")),
                    hasProperty("value", is("java.io.File")))));
    }

    @Test
    public void shouldGenerateCSVDataFromNodes() {
        assertThat(classDependencies.toCSVFormat(classDependencies.all().nodes(), System.lineSeparator()),
            is("com.arneam.ClassDependencies\ncom.arneam.DirExplorer\n"));
    }

    @Test
    public void shouldGenerateCSVDataFromEdges() {
        assertThat(classDependencies.toCSVFormat(classDependencies.allWith(".*(File)").edges(),
            ";",
            System.lineSeparator()),
            anyOf(
                is("com.arneam.ClassDependencies;java.io.File\ncom.arneam.DirExplorer;java.io.File\n"),
                is("com.arneam.DirExplorer;java.io.File\ncom.arneam.ClassDependencies;java.io.File\n")));
    }

    @Test
    public void shouldWriteNodeIntoCSVFile() throws IOException {
        classDependencies.writeNodesIntoCSVFile(classDependencies.all().nodes(),
                System.lineSeparator(), "./nodes.csv");

        List<String> nodesFromCSVFile = Files.readAllLines(Paths.get("./nodes.csv"));
        assertThat(nodesFromCSVFile, containsInAnyOrder(
                is("com.arneam.ClassDependencies"), is("com.arneam.DirExplorer")));
    }

    @Test
    public void shouldWriteEdgesIntoCSVFile() throws IOException {
        classDependencies.writeEdgesIntoCSVFile(classDependencies.allWith(".*(File)").edges(),
                ";", System.lineSeparator(), "./edges.csv");

        List<String> edgesFromCSVFile = Files.readAllLines(Paths.get("./edges.csv"));
        assertThat(edgesFromCSVFile, containsInAnyOrder(
            is("com.arneam.ClassDependencies;java.io.File"),
            is("com.arneam.DirExplorer;java.io.File")));
    }

    @Test
    public void shouldGenerateXMLFromCSVFiles() throws IOException {
        classDependencies.writeNodesIntoCSVFile(classDependencies.all().nodes(),
                System.lineSeparator(), "./nodes.csv");

        //classDependencies.writeEdgesIntoCSVFile(classDependencies.allWith(".*(File)").edges(),
        classDependencies.writeEdgesIntoCSVFile(classDependencies.all().edges(),
                ";", System.lineSeparator(), "./edges.csv");

        classDependencies.generateXMLFromCSVFiles("./nodes.csv", "./edges.csv", "./classDependencies.graphml");

        List<String> xmlFile = Files.readAllLines(Paths.get("./classDependencies.graphml"));
        assertThat(xmlFile.size(), greaterThan(0));
    }

    // todo: generate in graphml format, para gephi
    // https://en.wikipedia.org/wiki/GraphML
    // https://gephi.org/users/supported-graph-formats/graphml-format/
    // gephi api to export to a graph file/pdf/svg https://gephi.org/tutorials/gephi-tutorial-toolkit.pdf
    // java xml parsing: https://github.com/mycila/xmltool; https://github.com/dom4j/dom4j; 

}
