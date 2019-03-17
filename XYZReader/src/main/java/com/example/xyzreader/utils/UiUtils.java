package com.example.xyzreader.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.palette.graphics.Palette;

/**
 * @author Yassin Ajdi
 * @since 3/16/2019.
 */
public class UiUtils {

    public static Palette.Swatch getDominantColor(Palette palette) {
        Palette.Swatch result = palette.getDominantSwatch();
        if (palette.getVibrantSwatch() != null) {
            result = palette.getVibrantSwatch();
        } else if (palette.getMutedSwatch() != null) {
            result = palette.getMutedSwatch();
        }
        return result;
    }

    // convert dip to float
    public static float dipToPixels(Context context, float dipValue){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  dipValue, metrics);
    }
}
