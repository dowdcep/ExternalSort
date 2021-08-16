package com.example.ExternalSort;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.TreeSet;

import com.example.ExternalSort.Utils.FileUtils;

import java.util.List;

public class ExternalSort {
    private File baseFile;
    private Path workingDir;
    private final String DEFAULT_OUT_FILE_NAME = "out.txt";
    long blockSize;

    //TODO: in Utils.MemoryUtils.esitmateSizeOfBlocks(_baseFile.length(), 10) -> refactor
    public ExternalSort(File _baseFile, Path _workingDir) {
        baseFile = _baseFile;
        workingDir = _workingDir;
        blockSize = Utils.MemoryUtils.esitmateSizeOfBlocks(_baseFile.length(), 10);
    }

    public void sortAndMerge() {
        sortAndMerge(DEFAULT_OUT_FILE_NAME);
    }

    public void sortAndMerge(String outFileName) {
        List<File> tmpFiles = FileUtils.sliceBaseFileToBlocks(baseFile, workingDir);
        TreeSet<String> outData = new TreeSet<>();
        File outputFile = new File(workingDir.toString(), outFileName);

        try {
            BatchLineBuffer linesReader = getTmpFilesLineBuffer(tmpFiles);
            tmpFiles.clear();
            String bufStr;

            while(!linesReader.isEmpty()) {
                long currentBlockSize = 0;
                while(currentBlockSize < blockSize && (bufStr = linesReader.getNextLine()) != null) {
                    outData.add(bufStr + "\n");
                    currentBlockSize += Utils.MemoryUtils.estimateStringSize(bufStr);
                }
                Utils.FileUtils.writeToFile(outputFile, String.join("", outData), true);
                outData.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BatchLineBuffer getTmpFilesLineBuffer(List<File> files) throws IOException {
        return BatchLineBuffer.makeFromFilesList(files);
    }
}
