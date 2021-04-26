package cecs327.utils;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;

import java.net.InetAddress;
import java.util.UUID;

public class UUIDUtils {
    private static final NameBasedGenerator generator;

    static {
        generator = Generators.nameBasedGenerator();
    }

    public static UUID getNameBasedUUID() {
        UUID id = null;
        try {
            id =  generator.generate(InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

}
