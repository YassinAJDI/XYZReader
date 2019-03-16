package com.example.xyzreader.utils;

import androidx.palette.graphics.Palette;

/**
 * @author Yassin Ajdi
 * @since 3/16/2019.
 */
public class UiUtils {

    public static Palette.Swatch getDominantColor(Palette palette) {
        Palette.Swatch result = palette.getDominantSwatch();
        if (palette.getDarkVibrantSwatch() != null) {
            result = palette.getDarkVibrantSwatch();
        } else if (palette.getDarkMutedSwatch() != null) {
            result = palette.getDarkMutedSwatch();
        }
        return result;
    }
}
