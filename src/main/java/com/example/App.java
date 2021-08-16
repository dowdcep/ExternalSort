package com.example;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.example.ExternalSort.ExternalSort;

public class App
{
    private static void printUsageText() {
        System.out.println("Usage: externalsort inputFile [workingDir]");
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            printUsageText();
            return;
        }

        File inputFile = new File(args[0]);
        Path workingDir = null;
        if(args.length > 1) {
            workingDir = Paths.get(args[1]);
            if(!Files.exists(workingDir))
                throw new IOException("Working directory path does not exists.");
        }

        if(!inputFile.exists())
            throw new IOException("Input file does not exists.");

        ExternalSort sort = new ExternalSort(inputFile, workingDir == null ? 
                                                                      Paths.get(inputFile.getParent()) : 
                                                                      workingDir);
        File outFile = sort.sortAndMerge();                                                                      
        System.out.println(String.format("Done!\nResult was saved in: %s", outFile.getAbsolutePath()));
    } 
}
