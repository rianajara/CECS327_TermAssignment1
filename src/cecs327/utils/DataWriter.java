package cecs327.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The DataWriter class encapsulate ByteArrayOutputStream and
 * DataOutputStream together, which reduce the redundancy code
 * and provides a more convenient way to encapsulate data
 */
public class DataWriter {
    ByteArrayOutputStream baos;
    DataOutputStream dos;

    public DataWriter() {
        this.baos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(new BufferedOutputStream(baos));
    }

    public synchronized void write(byte[] data) throws IOException {
        dos.write(data);
    }

    public synchronized void write(byte[] data, int start, int len) throws IOException {
        dos.write(data, start, len);
    }

    public void flush() throws IOException {
        dos.flush();
    }

    public final void writeInt(int i) throws IOException {
        dos.writeInt(i);
    }

    public byte[] toByteArray() {
        return baos.toByteArray();
    }

    public void close() throws IOException {
        baos.close();
        dos.close();
    }
}
