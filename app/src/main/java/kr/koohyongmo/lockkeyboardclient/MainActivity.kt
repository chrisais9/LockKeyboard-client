package kr.koohyongmo.lockkeyboardclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.KeyboardManager

class MainActivity : AppCompatActivity() {

    lateinit var keyboardManager: KeyboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyboardManager = KeyboardManager(window.decorView, applicationContext, R.id.edit_pass)
        keyboardManager.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardManager.onDestroy()
    }
}
