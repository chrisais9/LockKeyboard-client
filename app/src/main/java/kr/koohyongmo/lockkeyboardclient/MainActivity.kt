package kr.koohyongmo.lockkeyboardclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.KeyboardManager

class MainActivity : AppCompatActivity() {

    private val keyboardManager: KeyboardManager by lazy {
        KeyboardManager(this, window.decorView, R.id.edit_pass)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        keyboardManager.onCreate()

    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardManager.onDestroy()
    }
}