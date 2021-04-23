package utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class UUIDUtils {
    private static TimeBasedGenerator generator;

    static {
        generator = Generators.timeBasedGenerator();
    }

    public static UUID getTimeBasedUUID() {
        return generator.generate();
    }

}
