package com.example.ExternalSort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper for LineBuffer, 
 * which contains a list of buffers from which lines are read in turn.
 */
public class BatchLineBuffer {
    private ArrayList<LineBuffer> lBuffers;
    private int pointer;

    public BatchLineBuffer(ArrayList<LineBuffer> buffers) {
        lBuffers = buffers;
        pointer = 0;
    }
    
    public BatchLineBuffer() {
        lBuffers = new ArrayList<>();
        pointer = 0;
    }

    public boolean isEmpty() {
        return lBuffers.size() == 0;
    }

    public String getNextLine() throws IOException {

        checkPointer();
        if(pointer == -1)
            return null;

        String out = lBuffers.get(pointer).getNextString();

        if(out == null) {
            removeBufferAt(pointer);
            return getNextLine();
        }

        movePointer();
        return out;
    }


    public void closeAll() throws IOException {
        for(LineBuffer buffer : lBuffers) {
            buffer.close();
        }
    }

    private void removeBufferAt(int index) throws IOException{
        lBuffers.get(index).close();
        lBuffers.remove(index);
        pointer = 0;
    }

    private void checkPointer() {
        if(isEmpty()) {
            pointer = -1;
        }

        if(pointer == lBuffers.size())
            pointer = 0;
    }

    private void movePointer() {
        pointer++;
        checkPointer();
    }

    /**
     * This creates a LineBuffer instances from list of file that was given
     * 
     * @param files list of files from which the LineBuffer is created
     * @return created BatchLineBuffer
     * @throws IOException
     */
    public static BatchLineBuffer makeFromFilesList(List<File> files) throws IOException {
        BatchLineBuffer linesBuffer = new BatchLineBuffer();
        linesBuffer.pointer = 0;
        for(File file : files) {
            linesBuffer.lBuffers.add(new LineBuffer(file));
        }
        return linesBuffer;
    }
}
