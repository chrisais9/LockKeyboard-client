package kr.koohyongmo.lockkeyboardclient

import android.inputmethodservice.Keyboard
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kr.koohyongmo.lockkeyboardclient.keyboard.LockKeyboardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mCustomKeyboard: LockKeyboardView =
                findViewById<View>(R.id.keyboardview) as LockKeyboardView
        mCustomKeyboard.keyboard = Keyboard(this, R.xml.numkbd)
        mCustomKeyboard.isPreviewEnabled = false // NOTE Do not show the preview balloons
        mCustomKeyboard.registerEditText(R.id.edittext1)
//        mCustomKeyboard.setAllignBottomCenter(true)
    }
}
