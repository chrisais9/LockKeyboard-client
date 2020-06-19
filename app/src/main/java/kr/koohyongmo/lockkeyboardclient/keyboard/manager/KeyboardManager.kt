package kr.koohyongmo.lockkeyboardclient.keyboard.manager

import android.content.Context
import android.inputmethodservice.Keyboard
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.IdRes
import kr.koohyongmo.lockkeyboardclient.R
import kr.koohyongmo.lockkeyboardclient.keyboard.LockKeyboardView
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.base.BaseManager

/**
 * Created by KooHyongMo on 2020/06/20
 */
class KeyboardManager(
    private val attachedView: View,
    private val context: Context,
    @IdRes private val editTextId: Int
) : BaseManager() {
    lateinit var keyboardView: LockKeyboardView

    override fun onCreate() {
        keyboardView = attachedView.findViewById(R.id.keyboardview)

        keyboardView.keyboard = Keyboard(context, R.xml.numkbd)
        keyboardView.isPreviewEnabled = false // NOTE Do not show the preview balloons
        keyboardView.registerEditText(editTextId)
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }
}