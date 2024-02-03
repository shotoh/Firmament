package io.github.shotoh.firmament.utils;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import org.apache.hc.client5.http.utils.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NBTUtils {
    public static byte[] decodeBase64(String itemBytes) {
        return Base64.decodeBase64(itemBytes);
    }

    public static NamedTag decodeNBT(String itemBytes) {
        byte[] buffer = decodeBase64(itemBytes);
        try(ByteArrayInputStream byt = new ByteArrayInputStream(buffer)) {
            return new NBTDeserializer(false).fromStream(byt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
