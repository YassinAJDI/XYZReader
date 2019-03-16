package com.example.xyzreader.utils;

import androidx.palette.graphics.Palette;

/**
 * @author Yassin Ajdi
 * @since 3/16/2019.
 */
public class UiUtils {

    public static Palette.Swatch getDominantColor(Palette palette) {
        Palette.Swatch result = palette.getDominantSwatch();
        if (palette.getLightVibrantSwatch() != null) {
            result = palette.getLightVibrantSwatch();
        } else if (palette.getLightMutedSwatch() != null) {
            result = palette.getLightMutedSwatch();
        }
        return result;
    }
}
