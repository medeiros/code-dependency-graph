package com.arneam;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ClassDependenciesTest {

    //static final String FILE_PATH = "/home/daniel/code/opensource/code-dependency-graph/src/main/java";
    static final String FILE_PATH = "/Users/dma/code/opensource/code-dependency-graph/src/main/java";

    private static final String NODES_CSV_FILE = "./nodes.csv";
    private static final String EDGES_CSV_FILE = "./edges.csv";
    private static final String GRAPHML_FILE = "./classDependencies.graphml";
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
    public void shouldLoadAllClassDependenciesOfClassDependenciesClass() {
        Map<String, Set<String>> dependencies = classDependencies.all().data();
        assertThat(dependencies, hasEntry(is("com.arneam.ClassDependencies"), containsInAnyOrder(
            "com.github.javaparser.JavaParser",
            "com.github.javaparser.ast.ImportDeclaration",
            "com.github.javaparser.ast.PackageDeclaration",
            "com.github.javaparser.ast.visitor.VoidVisitorAdapter",
            "com.mycila.xmltool.XMLDoc",
            "com.mycila.xmltool.XMLTag",
            "org.apache.commons.lang3.StringUtils",
            "org.apache.commons.lang3.tuple.ImmutablePair",
            "org.apache.commons.lang3.tuple.Pair",
            "java.io.BufferedWriter",
            "java.io.File",
            "java.io.IOException",
            "java.nio.charset.Charset",
            "java.nio.file.Files",
            "java.nio.file.Paths",
            "java.util.Set",
            "java.util.HashSet",
            "java.util.List",
            "java.util.Map",
            "java.util.HashMap",
            "java.util.stream.Collectors")));
    }

    @Test
    public void shouldLoadAllClassDependenciesOfClassDependenciesWhichEndsWithMap() {
        Map<String, Set<String>> dependencies = classDependencies.allWith(".*(Map)").data();
        assertThat(dependencies, hasEntry(is("com.arneam.ClassDependencies"), containsInAnyOrder(
            "java.util.HashMap",
            "java.util.Map")));
    }

    @Test
    public void shouldLoadAllClassDependenciesOfDirExplorerClass() {
        Map<String, Set<String>> dependencies = classDependencies.all().data();
        assertThat(dependencies, hasEntry(is("com.arneam.DirExplorer"), contains("java.io.File")));
    }

    @Test
    public void shouldGenerateNodesOfAllDependencies() { // also called vertex (plural: vertices)
        Set<String> nodes = classDependencies.all().nodes();
        assertThat(nodes, containsInAnyOrder(
            "com.github.javaparser.JavaParser",
            "com.github.javaparser.ast.ImportDeclaration",
            "com.github.javaparser.ast.PackageDeclaration",
            "com.github.javaparser.ast.visitor.VoidVisitorAdapter",
            "com.mycila.xmltool.XMLDoc",
            "com.mycila.xmltool.XMLTag",
            "org.apache.commons.lang3.StringUtils",
            "org.apache.commons.lang3.tuple.ImmutablePair",
            "org.apache.commons.lang3.tuple.Pair",
            "java.io.BufferedWriter",
            "java.io.File",
            "java.io.IOException",
            "java.nio.charset.Charset",
            "java.nio.file.Files",
            "java.nio.file.Paths",
            "java.util.Set",
            "java.util.HashSet",
            "java.util.List",
            "java.util.Map",
            "java.util.HashMap",
            "java.util.stream.Collectors",
            "com.arneam.ClassDependencies",
            "com.arneam.DirExplorer"));
    }

    @Test
    public void shouldGenerateNodesOfFilteredDependencies() { // also called vertex (plural: vertices)
        Set<String> nodes = classDependencies.allWith(".*(File)").nodes();
        assertThat(nodes, containsInAnyOrder(
            "com.arneam.ClassDependencies",
            "com.arneam.DirExplorer",
            "java.io.File"));
    }

    @Test
    public void shouldGenerateEdgesOfAllDependencies() { // edge = pair of vertices or nodes
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
                    hasProperty("value", is("com.mycila.xmltool.XMLDoc"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("com.mycila.xmltool.XMLTag"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("org.apache.commons.lang3.StringUtils"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("org.apache.commons.lang3.tuple.ImmutablePair"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("org.apache.commons.lang3.tuple.Pair"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.BufferedWriter"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.IOException"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.charset.Charset"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.file.Files"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.nio.file.Paths"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.Set"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.HashSet"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.Map"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.HashMap"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.List"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.util.stream.Collectors"))),
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                hasProperty("value", is("java.io.File"))),
            allOf(hasProperty("key", is("com.arneam.DirExplorer")),
                    hasProperty("value", is("java.io.File")))
            )
        );
    }

    @Test
    public void shouldGenerateEdgesOfFilteredDependencies() {
        Set<Pair<String, String>> edges = classDependencies.allWith(".*(File)").edges();
        assertThat(edges, containsInAnyOrder(
            allOf(hasProperty("key", is("com.arneam.ClassDependencies")),
                    hasProperty("value", is("java.io.File"))),
            allOf(hasProperty("key", is("com.arneam.DirExplorer")),
                    hasProperty("value", is("java.io.File")))));
    }

    @Test
    public void shouldGenerateCSVDataOfNodesOfClassDependenciesClass() {
        String csv = classDependencies.toCSVFormat(classDependencies.all().nodes(), System.lineSeparator());

        assertThat(Arrays.asList(csv.split(System.lineSeparator())), containsInAnyOrder(
            "com.github.javaparser.JavaParser",
            "com.github.javaparser.ast.ImportDeclaration",
            "com.github.javaparser.ast.PackageDeclaration",
            "com.github.javaparser.ast.visitor.VoidVisitorAdapter",
            "com.mycila.xmltool.XMLDoc",
            "com.mycila.xmltool.XMLTag",
            "org.apache.commons.lang3.StringUtils",
            "org.apache.commons.lang3.tuple.ImmutablePair",
            "org.apache.commons.lang3.tuple.Pair",
            "java.io.BufferedWriter",
            "java.io.File",
            "java.io.IOException",
            "java.nio.charset.Charset",
            "java.nio.file.Files",
            "java.nio.file.Paths",
            "java.util.Set",
            "java.util.HashSet",
            "java.util.List",
            "java.util.Map",
            "java.util.HashMap",
            "java.util.stream.Collectors",
            "com.arneam.ClassDependencies",
            "com.arneam.DirExplorer"));
    }

    @Test
    public void shouldGenerateCSVDataOfEdges() {
        assertThat(classDependencies.toCSVFormat(classDependencies.allWith(".*(File)").edges(), ";",
            System.lineSeparator()), anyOf(
                is("com.arneam.ClassDependencies;java.io.File\ncom.arneam.DirExplorer;java.io.File\n"),
                is("com.arneam.DirExplorer;java.io.File\ncom.arneam.ClassDependencies;java.io.File\n")));
    }

    @Test
    public void shouldWriteNodesIntoCSVFile() throws IOException {
        classDependencies.writeNodesIntoCSVFile(classDependencies.all().nodes(),
                System.lineSeparator(), NODES_CSV_FILE);

        List<String> nodesFromCSVFile = Files.readAllLines(Paths.get(NODES_CSV_FILE));
        assertThat(nodesFromCSVFile.size(), equalTo(23));
    }

    @Test
    public void shouldWriteEdgesIntoCSVFile() throws IOException {
        classDependencies.writeEdgesIntoCSVFile(classDependencies.all().edges(),
                ";", System.lineSeparator(), EDGES_CSV_FILE);

        List<String> edgesFromCSVFile = Files.readAllLines(Paths.get(EDGES_CSV_FILE));
        assertThat(edgesFromCSVFile.size(), equalTo(22));
    }

    @Test
    public void shouldGenerateGraphMLFromCSVFiles() throws IOException {
        classDependencies.writeNodesIntoCSVFile(classDependencies.all().nodes(),
                System.lineSeparator(), NODES_CSV_FILE);

        classDependencies.writeEdgesIntoCSVFile(classDependencies.all().edges(),
                ";", System.lineSeparator(), EDGES_CSV_FILE);

        classDependencies.generateGraphMLFromCSVFiles(NODES_CSV_FILE, EDGES_CSV_FILE,
                GRAPHML_FILE);

        List<String> xmlFile = Files.readAllLines(Paths.get(GRAPHML_FILE));
        assertThat(xmlFile.size(), greaterThan(0));
    }

}
