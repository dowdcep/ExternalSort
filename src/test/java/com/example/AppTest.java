package com.example;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.File;
import java.nio.file.Paths;
import com.example.ExternalSort.ExternalSort;
import com.example.ExternalSort.LineBuffer;
import com.example.ExternalSort.Utils;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void externalSortFileTest()
    {
        String expectedString = "adbcefg";
        File baseFile = new File("src\\test\\java\\com\\example\\testfile1.txt");
        ExternalSort sort = new ExternalSort(baseFile, Paths.get(baseFile.getParent()));
        File results = sort.sortAndMerge();
        String actualString = "",
               bufStr;
        try {
            LineBuffer buffer = new LineBuffer(results);
            while((bufStr = buffer.getNextString()) != null) {
                actualString += bufStr;
            }
            buffer.close();
            Utils.FileUtils.deleteFile(results);
            assertEquals(expectedString, actualString);
        } catch(Exception e) {
            fail();
            e.printStackTrace();
        }
    }
}
