package com.example.Deduplicator;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;

public class Deduplicator {
    private int chunkSize;
    private Path sourceFile;
    private ArrayList<Path> chunkedDataFilePathes;

    // TODO: create multiple constructors for Path, String, File
    public Deduplicator(int _chunkSize, Path _sourceFile) {
        chunkSize = _chunkSize;
        sourceFile = _sourceFile;
        chunkedDataFilePathes = new ArrayList<Path>();
    }

    public Deduplicator(int _chunkSize, String _sourceFile) {
        chunkSize = _chunkSize;
        sourceFile = Paths.get(_sourceFile);
        chunkedDataFilePathes = new ArrayList<Path>();
    }

    public void SplitAndSaveDataToFile() {
        int chunkNumber = 0;
        String chunkFileBasePath = sourceFile.getParent() + "\\.buff$.txt";
        ByteBuffer buffer = ByteBuffer.allocateDirect(chunkSize); // Find out difference with allocate and allocateDirect

        
        try (FileChannel inputChannel = new FileInputStream(sourceFile.toFile()).getChannel()) {
            while(inputChannel.read(buffer) != -1){
                Path chunkFilePath = Paths.get(chunkFileBasePath.replaceAll("\\$", String.valueOf(chunkNumber++)));
                SaveToFile(buffer, chunkFilePath);
            }

            inputChannel.close();
            buffer.clear();
        } catch (IOException e) {
            buffer.clear();
            System.out.println(e);
        }
    }

    // private
    private void SaveToFile(ByteBuffer buffer, Path outputPath) {
        try (FileChannel outputChannel = new FileOutputStream(outputPath.toFile()).getChannel();) {
            buffer.flip();
            outputChannel.write(buffer);
            buffer.clear();
            outputChannel.close();
        } catch (IOException e) {
            buffer.clear();
            System.out.println(e);
        } 
    }
}
