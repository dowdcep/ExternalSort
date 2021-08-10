package com.example;
import com.example.Deduplicator.Deduplicator;
import com.example.Utils.TestFileCreator;
public class App 
{
    public static void main( String[] args )
    {
        Deduplicator deduplicator = new Deduplicator(100, "data\\testfile1.txt");
        deduplicator.SplitAndSaveDataToFile();
        //TestFileCreator.CreateTestFile(100, 10, "data\\testfile1.txt");
        System.out.println("Done!");
    }
}
