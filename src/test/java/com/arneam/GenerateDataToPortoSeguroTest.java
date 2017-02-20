package com.arneam;

import org.junit.Test;

import java.io.File;

public class GenerateDataToPortoSeguroTest {

    static final String FILE_PATH = "/Users/dma/code/octo/portoprint/portoprint-automovel-service/src/main/java/com/porto/portoprint/automovel/service";

    @Test
    public void execute() {
        File projectDir = new File(FILE_PATH);
        ClassDependencies classDependencies = new ClassDependencies(projectDir);

        classDependencies.writeNodesIntoCSVFile(classDependencies.all().nodes(),
                System.lineSeparator(), "./portoprint-nodes.csv");

        classDependencies.writeEdgesIntoCSVFile(classDependencies.allWith(".*(Dao)").edges(),
                ";", System.lineSeparator(), "./portoprint-edges.csv");

        classDependencies.generateXMLFromCSVFiles("./portoprint-nodes.csv",
                "./portoprint-edges.csv", "./portoprint-classDependencies.graphml");
    }


}
