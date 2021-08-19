package com.example.ExternalSort;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import com.example.ExternalSort.Utils.FileUtils;
import java.util.ArrayList;
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

    
    public File sortAndMerge() {
        return sortAndMerge(DEFAULT_OUT_FILE_NAME);
    }

    public File sortAndMerge(String outFileName) {
        List<File> tmpFiles = FileUtils.sliceBaseFileToBlocks(baseFile, workingDir);
        List<String> outData = new ArrayList<>();
        File outputFile = new File(workingDir.toString(), outFileName);

        try {
            BatchLineBuffer linesReader = getTmpFilesLineBuffer(tmpFiles);
            String bufStr;
            

            while(!linesReader.isEmpty()) {
                long currentBlockSize = 0;

                while(currentBlockSize < blockSize && (bufStr = linesReader.getNextLine()) != null) {
                    if(!linesReader.checkStringInHashes(bufStr)) {
                        outData.add(bufStr + "\n");
                        currentBlockSize += Utils.MemoryUtils.estimateStringSize(bufStr);
                    }
                }
                Utils.FileUtils.writeToFile(outputFile, String.join("", outData), true);
                outData.clear();
            }
            linesReader.closeAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.FileUtils.deleteFiles(tmpFiles);
        return outputFile;
    }

    private BatchLineBuffer getTmpFilesLineBuffer(List<File> files) throws IOException {
        return BatchLineBuffer.makeFromFilesList(files);
    }
}
