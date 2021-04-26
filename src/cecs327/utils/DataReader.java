package cecs327.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DataReader {
    private ByteArrayInputStream bais;
    private DataInputStream dis;

    public DataReader(byte[] data) {
        this.bais = new ByteArrayInputStream(data);
        this.dis = new DataInputStream(new BufferedInputStream(bais));
    }

    public final int read(byte[] buffer) throws IOException {
        return dis.read(buffer);
    }

    public final int read(byte[] buffer, int start, int len) throws IOException {
        return dis.read(buffer, start, len);
    }

    public final int readInt() throws IOException {
        return dis.readInt();
    }

    public final void readFully(byte[] buffer) throws IOException {
        dis.readFully(buffer);
    }

    public final void readFully(byte[] buffer, int start, int len) throws IOException {
        dis.readFully(buffer,  start, len);
    }

    public void close() throws IOException {
        this.bais.close();
        this.dis.close();
    }




}
