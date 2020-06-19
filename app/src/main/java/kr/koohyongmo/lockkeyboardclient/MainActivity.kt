package kr.koohyongmo.lockkeyboardclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.KeyboardManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyboardManager = KeyboardManager(window.decorView, applicationContext, R.id.edit_pass)
        keyboardManager.onCreate()
    }
}
