package cecs327.utils;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;

import java.util.UUID;

/**
 * The UUIDUtils will generate a unique UUID based
 * on the IP address. So, in a LAN, each nodes will
 * have a unique ID. And each time a node joins the
 * network, it will always have a same ID belonging
 * to it.
 */
public class UUIDUtils {
    private static final NameBasedGenerator generator;

    static {
        generator = Generators.nameBasedGenerator();
    }

    public static UUID getNameBasedUUID() {
        UUID id = null;
        try {
            id =  generator.generate(IPUtils.getLocalIP());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

}
