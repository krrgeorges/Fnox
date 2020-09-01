package com.example.fnox;

import android.content.Context;

public class Functions {

    public static int dpToPx(Context c, int dp) {
        return (int) (dp * c.getResources().getDisplayMetrics().density);
    }

    public static boolean validateText(String text){
        if(text.split(" ").length >= 2) {
            return true;
        }
        return false;
    }
}
