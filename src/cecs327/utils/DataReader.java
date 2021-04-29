package cecs327.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * The DataReader class encapsulate ByteArrayInputStream and
 * DataInputStream together, which reduce the redundancy code
 * and provides a more convenient way to read data from byte
 * array
 */
public class DataReader {
    private ByteArrayInputStream bais;
    private DataInputStream dis;

    public DataReader(byte[] data) {
        this.bais = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(new BufferedInputStream(bais));
    }

    public final int readInt() throws IOException {
        return dis.readInt();
    }

    public final void readFully(byte[] buffer) throws IOException {
        dis.readFully(buffer);
    }

    public void close() throws IOException {
        this.bais.close();
        this.dis.close();
    }
}
