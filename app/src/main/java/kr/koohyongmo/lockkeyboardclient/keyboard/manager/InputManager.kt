package kr.koohyongmo.lockkeyboardclient.keyboard.manager

import android.util.Log

/**
 * Created by KooHyongMo on 2020/06/19
 */
class InputManager {
    companion object {
        const val TAG = "InputManager"
    }

    fun onKeyCharInput(keyCode: Int) {
        Log.d(TAG, "keycode: $keyCode")
    }

}