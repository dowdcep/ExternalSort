package com.example.ExternalSort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * This class is a wrapper on top of BufferedReader that contains the last readed string
 */
public class LineBuffer {

    private BufferedReader reader;
    private String buffer;

    public LineBuffer(File file) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(file));
        readNext();
    }

    public boolean isEmpty() {
        return buffer == null;
    }

    public String getNextString() throws IOException {
        if(isEmpty())
            return null;

        String result = buffer.toString();
        readNext();
        return result;
    }

    public void close() throws IOException {
        reader.close();
    }

    private void readNext() throws IOException {
        buffer = reader.readLine();
    }
}
