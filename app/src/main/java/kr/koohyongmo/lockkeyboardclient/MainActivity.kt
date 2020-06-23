package kr.koohyongmo.lockkeyboardclient

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.KeyboardManager

class MainActivity : AppCompatActivity() {

    private val keyboardManager: KeyboardManager by lazy {
        KeyboardManager(this, window.decorView, R.id.edit_pass)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        keyboardManager.onCreate()

        Glide.with(this)
            .load(R.drawable.logo)
            .into(logo)

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return if(keyCode == KeyEvent.KEYCODE_BACK && !keyboardManager.hideKeyboard()) {
            return true
        } else super.onKeyUp(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardManager.onDestroy()
    }
}