package com.example;
import java.io.File;
import java.nio.file.Paths;
import com.example.ExternalSort.ExternalSort;

public class App
{
    public static void main(String[] args)
    {
        File _baseFile = new File("data\\testfile1.txt");
        ExternalSort sort = new ExternalSort(_baseFile, Paths.get(_baseFile.getParent()));
        sort.sortAndMerge();
    }
}
