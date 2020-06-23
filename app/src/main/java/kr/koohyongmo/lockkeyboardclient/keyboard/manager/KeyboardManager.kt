package kr.koohyongmo.lockkeyboardclient.keyboard.manager

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.reactivex.disposables.CompositeDisposable
import kr.bitbyte.playkeyboard.rx.RxBus
import kr.bitbyte.playkeyboard.rx.RxEvents
import kr.koohyongmo.lockkeyboardclient.R
import kr.koohyongmo.lockkeyboardclient.keyboard.LockKeyboardView
import kr.koohyongmo.lockkeyboardclient.keyboard.manager.base.BaseManager
import kr.koohyongmo.lockkeyboardclient.keyboard.model.service.SocketService

/**
 * Created by KooHyongMo on 2020/06/20
 */
class KeyboardManager(
    private val activity: AppCompatActivity,
    private val attachedView: View,
    @IdRes private val editTextId: Int
) : BaseManager() {
    val TAG = "KeyboardManager"

    private lateinit var keyboardView: LockKeyboardView

    private val socketService = SocketService(activity)

    private lateinit var titleIncognitoSetting: TextView
    private lateinit var switchIncognitoSetting: SwitchCompat
    private lateinit var iconIncognitoSetting: ImageView

    private var currentKeyboardMode = 1

    private val keyboardLayoutChangeDisposable =
        RxBus.listen(RxEvents.KeyboardChangeLayout::class.java).subscribe {
            changeLayout()
        }


    override fun onCreate() {
        keyboardView = attachedView.findViewById(R.id.keyboardview)


        iconIncognitoSetting = attachedView.findViewById(R.id.icon_incognito_setting)
        titleIncognitoSetting = attachedView.findViewById(R.id.title_incognito_setting)
        switchIncognitoSetting = attachedView.findViewById(R.id.switch_incognito_setting)

        socketService.listen()

        keyboardView.keyboard = Keyboard(activity.applicationContext, R.xml.numkbd)

        keyboardView.isPreviewEnabled = false
        keyboardView.registerEditText(editTextId)
        keyboardView.registerSocketService(socketService)

        switchIncognitoSetting.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleIncognitoMode(isChecked)
        }



        Glide.with(activity)
            .load(R.drawable.siren)
            .apply(RequestOptions().dontAnimate())
            .into(iconIncognitoSetting)



    }


    fun hideKeyboard(): Boolean {
        if(keyboardView.isVisible) {
            keyboardView.hide()
            return false
        }
        return true
    }

    private fun changeLayout() {
        if(currentKeyboardMode == 1) {
            currentKeyboardMode = 0
            keyboardView.keyboard = Keyboard(activity.applicationContext, R.xml.quertykbd)
            keyboardView.invalidate()
        } else {
            currentKeyboardMode = 1
            keyboardView.keyboard = Keyboard(activity.applicationContext, R.xml.numkbd)
            keyboardView.invalidate()
        }
    }

    private fun toggleIncognitoMode(isActivated: Boolean) {
        if(isActivated) {
            titleIncognitoSetting.setText(R.string.title_incognito_on)
            titleIncognitoSetting.typeface = Typeface.DEFAULT_BOLD
            Glide.with(activity)
                .load(R.drawable.siren)
                .apply(RequestOptions().centerCrop())
                .into(iconIncognitoSetting)

            keyboardView.isIncognitoMode = true

        } else {
            titleIncognitoSetting.setText(R.string.title_incognito_off)
            titleIncognitoSetting.typeface = Typeface.DEFAULT

            Glide.with(activity)
                .load(R.drawable.siren)
                .apply(RequestOptions().dontAnimate())
                .into(iconIncognitoSetting)

            keyboardView.isIncognitoMode = false
        }
    }



    override fun onDestroy() {
        socketService.disconnect()

        if(!keyboardLayoutChangeDisposable.isDisposed) {
            keyboardLayoutChangeDisposable.dispose()
        }
    }
}