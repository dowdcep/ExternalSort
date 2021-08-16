package com.example.ExternalSort;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class Utils {
    public static class MemoryUtils {
        public static long estimateAvailbleMemory(){
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long allocated = runtime.totalMemory() - runtime.freeMemory();
            long free = runtime.maxMemory() - allocated;
            return free;
        }

        private static long fitBlockSizeInMemory(long blockSize) {
            long freeMem = estimateAvailbleMemory();

            if (blockSize > freeMem) 
                return fitBlockSizeInMemory(blockSize / 2);
            
            // if (blockSize < freeMem / 2) 
            //     blockSize = freeMem / 2;

            return blockSize;
        }

        public static long esitmateSizeOfBlocks(long fileSize, int maxOfTmpFiles) {
            if (maxOfTmpFiles == 0) {
                throw new ArithmeticException("DivideByZeroException");
            }

            long blockSize = fileSize / maxOfTmpFiles;
            
            if(blockSize == 0)
                blockSize += 1;

            blockSize = fitBlockSizeInMemory(blockSize);
            
            return blockSize;
        }

        // https://www.javamex.com/tutorials/memory/string_memory_usage.shtml
        public static long estimateStringSize(String s) {
            return 8 * (int) ((((s.length()) * 2) + 45) / 8);
        }
    }

    public static class FileUtils {
        private static int MAX_TMPFILES = 10;

        public static List<File> sliceBaseFileToBlocks(File baseFile, Path workingDir) {
            long blockSize = MemoryUtils.esitmateSizeOfBlocks(baseFile.length(), MAX_TMPFILES); //in bytes
            long currentBlockSize = 0; //in bytes
            String bufStr;
            List<File> files = new ArrayList<>();
            TreeSet<String> buffer = new TreeSet<>();

            try {
                LineBuffer lines = new LineBuffer(baseFile);
                while(!lines.isEmpty()) {
                    currentBlockSize = 0;
                    while(currentBlockSize < blockSize && !lines.isEmpty()) {
                        bufStr = lines.getNextString();
                        buffer.add(bufStr+"\n");
                        currentBlockSize += MemoryUtils.estimateStringSize(bufStr);
                    }
                    files.add(createAndWrite(String.join("", buffer), 
                                             getNewTempFileName(workingDir))
                    );
                    buffer.clear();
                }
                lines.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return files;
        }
        
        public static File createAndWrite(String data, File outFile) throws IOException {
            writeToFile(outFile, data, false);
            return outFile;
        }

        public static void writeToFile(File file, String data, boolean append) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(data);
            writer.close();
        }

        public static ArrayList<File> getAllFilesFromWorkingDir(Path workingDir) {
            File folder = workingDir.toFile();
            ArrayList<File> files = new ArrayList<File>();
            Collections.addAll(files, folder.listFiles());
            return files;
        }
    
        public static ArrayList<BufferedReader> getTemporaryFilesReaders(Path workingDir) {
            ArrayList<File> files = getAllFilesFromWorkingDir(workingDir);
            ArrayList<BufferedReader> readers = new ArrayList<>();
            int index = 0;
            while(true) {
                if(index >= files.size())
                    break;
                if (files.get(index).getName().startsWith("."))
                    try {
                        readers.add(new BufferedReader(new FileReader(files.get(index))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                index++;
            }
            files.clear();
            return readers;
        }

        public static void deleteFiles(List<File> files) {
            for (File file : files) {
                file.delete();
            }
        }

        private static File getNewTempFileName(Path workingDir) throws IOException {
            return File.createTempFile("_tmp#", ".tmp", workingDir.toFile());
        }
    }
}
