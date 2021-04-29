package cecs327.events;

import java.io.IOException;

public interface Event {

    public void unpackData(byte[] data) throws IOException;
    public byte[] packData() throws IOException;

    public int getEventType();
    public String getNodeIP();
    public String getNodeUUID();

}
