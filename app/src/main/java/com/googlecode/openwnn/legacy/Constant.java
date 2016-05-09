package com.googlecode.openwnn.legacy;

/**
 * Created by lvhonghe on 16/4/11.
 */
public class Constant {

//    public static final String dataLocation = "/data/data/com.example.test/";
    public static final String dataLocation = "/data/data/cn.com.ethank.yungePad/";

    public static String composeLocation(String fileName) {
        return new StringBuilder().append(dataLocation).append(fileName).toString();
    }
}
