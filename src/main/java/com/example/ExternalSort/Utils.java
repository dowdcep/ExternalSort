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

    /**
     * Simple class used to estimate memory usage.
    */
    public static class MemoryUtils {

        /**
         *  Estimates the size of available
         * 
         *  @return The size of available memory in bytes;
        */
        public static long estimateAvailableMemory() {
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long allocated = runtime.totalMemory() - runtime.freeMemory();
            long free = runtime.maxMemory() - allocated;
            return free;
        }

        /**
         * Check estimated blockSize if it fits in memory.
         * 
         * 
         * @param blockSize Estimated blockSize
         * @return The estimated blockSize in bytes;
         */
        private static long fitBlockSizeInMemory(long blockSize) {
            long freeMem = estimateAvailableMemory();

            if (blockSize > freeMem) 
                return fitBlockSizeInMemory(blockSize / 2);
            
            if (blockSize < freeMem / 3) 
                blockSize = freeMem / 3;

            return blockSize;
        }

        /**
         * Estimate size of blocks, which be used to slice the inputFile to temporary files.
         * This is necessary to process a file, that doesn't fit into RAM.
         * 
         * @param fileSize Size of input file (in bytes);
         * @param maxOfTmpFiles amount of files which we can create
         * @return esitmated blockSize
         */
        public static long esitmateSizeOfBlocks(long fileSize, int maxOfTmpFiles) {
            if (maxOfTmpFiles == 0) {
                throw new ArithmeticException("DivideByZeroException");
            }

            long blockSize = fileSize / maxOfTmpFiles;

            blockSize = fitBlockSizeInMemory(blockSize);
            
            return blockSize;
        }


        /**
         * This method estimates string size (in bytes).
         * 
         * This formula was taken from https://www.javamex.com/tutorials/memory/string_memory_usage.shtml;
         * 
         * @param s string to check
         * @return amount of RAM used to this string
         */
        public static long estimateStringSize(String s) {
            return 8 * (int) ((((s.length()) * 2) + 45) / 8);
        }
    }

    /**
     *  This utils contains methods that makes easier working with file system.
     */
    public static class FileUtils {
        private static int MAX_TMPFILES = 10;

        /**
         * This method slice file data into blocks which write into temporary file.
         * Temporary files contains sorted data.
         * 
         * @param baseFile the input file to be processed
         * @param workingDir working directory where the tmp files and output file will be created.
         * @return list of temporary files
         */
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
        
        /**
         * This method creates new file and write input data to this file.
         * 
         * @param data
         * @param outFile 
         * @return file that was created
         * @throws IOException
         */
        public static File createAndWrite(String data, File outFile) throws IOException {
            writeToFile(outFile, data, false);
            return outFile;
        }

        /**
         * This writes input data to file
         * 
         * @param file output file
         * @param data data to be saved to file
         * @param append Pass True if you allow to append to file
         * @throws IOException
         */
        public static void writeToFile(File file, String data, boolean append) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(data);
            writer.close();
        }

        public static void writeToFile(File file, ArrayList<byte[]> data) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            String str;
            for(byte[] line : data) {
                str = new String(line) + "\n";
                writer.write(str);
            }
            writer.close();
        }

        /**
         * Returns files located that are in the given path
         * 
         * @param workingDir path from where get files
         * @return list of all files located in directory
         */
        public static ArrayList<File> getAllFilesFromWorkingDir(Path workingDir) {
            File folder = workingDir.toFile();
            ArrayList<File> files = new ArrayList<File>();
            Collections.addAll(files, folder.listFiles());
            return files;
        }


        /**
         * This returns all temporary files located in the given directory
         * 
         * TODO: add mask to filter files
         * 
         * @param workingDir Working directory from where get files
         * @return list of all temporary files
         */
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

        /**
         * Remove given files
         * 
         * @param files list of files that needed to be removed
         */
        public static void deleteFiles(List<File> files) {
            for (File file : files) {
                file.delete();
            }
        }

        /**
         * Remove given file
         * 
         * @param file file that needed to be removed
         */
        public static void deleteFile(File file) {
           file.delete();
        }

        /**
         * This creates new temporary file
         * 
         * @param workingDir directory where tmp file will be created
         * @return created file
         * @throws IOException
         */
        private static File getNewTempFileName(Path workingDir) throws IOException {
            return File.createTempFile("_tmp#", ".tmp", workingDir.toFile());
        }
    }
}
