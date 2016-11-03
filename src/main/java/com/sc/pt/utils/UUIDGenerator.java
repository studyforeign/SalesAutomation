package com.sc.pt.utils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/17/15
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class UUIDGenerator {
    public UUIDGenerator() {
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;
    }

}
