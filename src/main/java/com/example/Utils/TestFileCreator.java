package com.example.Utils;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;

public class TestFileCreator {
    public static void CreateTestFile(int amountOfRows, int amountOfSymbolsInRow, String filePath){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder resultString = new StringBuilder();
        Random rnd = new Random();
        int lowerBound = 10;

        for(int row = 0; row < amountOfRows; row++){
            StringBuilder str = new StringBuilder();
            for (int symbol = 0; symbol < amountOfSymbolsInRow; symbol++){
                int index = (int)(rnd.nextFloat() * alphabet.length());
                str.append(alphabet.charAt(index));
            }
            amountOfSymbolsInRow = rnd.nextInt(20-lowerBound) + lowerBound;
            resultString.append(str + "\n");
        }

        File outFile = new File(filePath); //"data\\testfile1.txt"
        FileWriter writer;
        try {
            outFile.createNewFile();
            writer = new FileWriter(outFile, true);
            writer.write(resultString.toString());
            writer.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
