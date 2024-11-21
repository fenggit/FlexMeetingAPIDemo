package io.agora.educore.example.utils;

import android.util.Log;

/**
 * author : felix
 * date : 2024/3/13
 * description :
 */
public class TestLog {
    @Trace
    public String getTestA(String userId) {
        return "Tom";
    }


    @Trace
    public void setTestA(int a, int b) {
        int c = a + b;
        Log.i("TestLog", "c = " + c);
    }

    public void setTestB(int a, int b) {
        Log.i("TestLog", "66666");
    }

}
