
package huffman;

import java.io.*;

public class BitOutputStream {
    private FileOutputStream output;
    private int digits;     // a buffer used to build up next set of digits
    private int numDigits;  // how many digits are currently in the buffer

    private static final int BYTE_SIZE = 8;  // digits per byte

    // pre : given file name is legal
    // post: creates a BitOutputStream sending output to the file
    public BitOutputStream(String file) {
        try {
            output = new FileOutputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = numDigits = 0;
    }

    // post: writes given bit to output
    public void writeBit(int bit) {
        if (bit < 0 || bit > 1)
            throw new IllegalArgumentException("Illegal bit: " + bit);
        digits += bit << numDigits;
        numDigits++;
        if (numDigits == BYTE_SIZE)
            flush();
    }
    public void writeBy(String b)
    {
        byte[] bb=b.getBytes();
        for(int i=0;i<bb.length;i++)
        {
            digits=bb[i];
            flush();

        }
        
    }

    // post: Flushes the buffer.  If numDigits < BYTE_SIZE, this will 
    //       effectively pad the output with extra 0's, so this should
    //       be called only when numDigits == BYTE_SIZE or when we are
    //       closing the output.
    private void flush() {
        try {
            output.write(digits);
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        digits = 0;
        numDigits = 0;
    }

    // post: output is closed
    public void close() {
        if (numDigits > 0)
            flush();
        try {
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    // included to ensure that the stream is closed
    protected void finalize() {
        close();
    }
}
