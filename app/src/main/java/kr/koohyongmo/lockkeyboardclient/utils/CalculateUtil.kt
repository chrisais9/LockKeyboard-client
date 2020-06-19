package kr.koohyongmo.lockkeyboardclient.utils

import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by KooHyongMo on 2020/06/15
 */
object CalculateUtil {

    fun dp2px(context: Context, dp: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}