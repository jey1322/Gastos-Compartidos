package com.strainteam.gastoscompartidos.utils

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller


class AnimationRecycler(context: Context) : LinearSmoothScroller(context){
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START // Hace que el desplazamiento se detenga en la posición deseada
    }

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return 30f / displayMetrics.densityDpi // Ajusta la velocidad de desplazamiento
    }
}