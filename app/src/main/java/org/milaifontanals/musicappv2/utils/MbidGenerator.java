package org.milaifontanals.musicappv2.utils;

import java.util.UUID;

public class MbidGenerator {

    public static String generateMbid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 36);
    }
}