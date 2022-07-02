package com.example.inventory.util

import android.content.Context
import android.graphics.Typeface

class TypeFaceUtil {
    fun overridefonts(
        context: Context,
        defaultFontToOverride: String,
        customFontFileNameInAssets: String
    ) {
        val customTypeface = Typeface.createFromAsset(context.assets, customFontFileNameInAssets)
        val defaultTypefaceField = Typeface::class.java.getDeclaredField(defaultFontToOverride)
        defaultTypefaceField.isAccessible = true
        defaultTypefaceField.set(null, customTypeface)
    }
}