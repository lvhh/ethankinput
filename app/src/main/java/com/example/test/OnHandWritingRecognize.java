package com.example.test;

import com.googlecode.openwnn.legacy.WnnWord;

import java.util.ArrayList;

/**
 * Created by lvhonghe on 16/4/28.
 */
public interface OnHandWritingRecognize {
    public void handWritingRecognized(ArrayList<WnnWord> result);
}
