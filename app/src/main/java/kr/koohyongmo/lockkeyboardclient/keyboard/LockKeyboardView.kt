package kr.koohyongmo.lockkeyboardclient.keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import kr.bitbyte.playkeyboard.rx.RxBus
import kr.bitbyte.playkeyboard.rx.RxEvents
import kr.koohyongmo.lockkeyboardclient.R
import kr.koohyongmo.lockkeyboardclient.keyboard.model.service.SocketService
import kr.koohyongmo.lockkeyboardclient.utils.CalculateUtil.dp2px
import kotlin.math.abs
import kotlin.math.min

/**
 * Created by KooHyongMo on 2020/06/15
 */
class LockKeyboardView(
    context: Context?,
    attrs: AttributeSet?
) : KeyboardView(context, attrs) {

    var isIncognitoMode: Boolean = false
    lateinit var socketService: SocketService

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAlignBottomCenter) {
            val relativeLayoutParams =
                layoutParams as RelativeLayout.LayoutParams
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            layoutParams = relativeLayoutParams
        }
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        Companion.width = xNew
        drawHandle()
    }

    private fun drawHandle() {
        mHandlePath.rewind()
        mHandlePath.moveTo(
            0f,
            topPaddingPx.toFloat()
        )
        mHandlePath.lineTo(
            0f,
            topPaddingPx - 25.toFloat()
        )
        mHandlePath.lineTo(
            Companion.width / 3.toFloat(),
            topPaddingPx - 25.toFloat()
        )
        mHandlePath.lineTo(
            Companion.width / 3.toFloat(),
            0f
        )
        mHandlePath.lineTo(
            2 * Companion.width / 3.toFloat(),
            0f
        )
        mHandlePath.lineTo(
            2 * Companion.width / 3.toFloat(),
            topPaddingPx - 25.toFloat()
        )
        mHandlePath.lineTo(
            Companion.width.toFloat(),
            topPaddingPx - 25.toFloat()
        )
        mHandlePath.lineTo(
            Companion.width.toFloat(),
            topPaddingPx.toFloat()
        )
        mHandlePath.lineTo(
            Companion.width.toFloat(),
            topPaddingPx.toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = mHandlePaint
        val path = mHandlePath
        canvas.drawPath(path, paint)
    }

    val isVisible: Boolean
        get() = this.visibility == View.VISIBLE

    fun show(v: View?) {
        this.visibility = View.VISIBLE
        this.isEnabled = true
    }

    fun hide() {
        this.visibility = View.GONE
        this.isEnabled = false
    }

    fun registerEditText(resid: Int) {
        val edittext = (context as Activity).findViewById<View>(resid) as EditText
        edittext.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) show(v) else hide()
        }
        edittext.setOnClickListener { v ->
            show(v)
        }
        edittext.showSoftInputOnFocus = false
    }

    fun registerSocketService(socketService: SocketService) {
        this.socketService = socketService
    }

    private val mKeyboardOnTouchListener: OnTouchListener = object : OnTouchListener {
        var dx = 0f
        var dy = 0f
        var moveToY = 0
        var moveToX = 0
        var distY = 0
        var distX = 0
        var inScreenCoordinates: Rect? = null
        var handleTouched = false
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            // Use ViewGroup.MarginLayoutParams so as to work inside any layout
            val params = view.layoutParams as MarginLayoutParams
            var performClick = false
            when (event.action) {
                MotionEvent.ACTION_MOVE -> if (handleTouched) {
                    moveToY = (event.rawY - dy).toInt()
                    moveToX = (event.rawX - dx).toInt()
                    distY = moveToY - params.topMargin
                    distX = moveToX - params.leftMargin
                    if (Math.abs(distY) > MOVE_THRESHOLD ||
                        Math.abs(distX) > MOVE_THRESHOLD
                    ) {
                        // Ignore any distance before threshold reached
                        moveToY -= Integer.signum(distY) * min(
                            MOVE_THRESHOLD,
                            abs(distY)
                        )
                        moveToX -= Integer.signum(distX) * min(
                            MOVE_THRESHOLD,
                            abs(distX)
                        )
                        inScreenCoordinates = keepInScreen(moveToY, moveToX)
                        view.y = inScreenCoordinates!!.top.toFloat()
                        view.x = inScreenCoordinates!!.left.toFloat()
                    }
                    performClick = false
                } else {
                    performClick = true
                }
                MotionEvent.ACTION_UP -> if (handleTouched) {
                    // reset handle color
                    mHandlePaint.color = HANDLE_COLOR
                    mHandlePaint.style = Paint.Style.FILL
                    invalidate()
                    performClick = false
                } else {
                    performClick = true
                }
                MotionEvent.ACTION_DOWN -> {
                    handleTouched =
                        event.y <= paddingTop
                    dy = event.rawY - view.y
                    dx = event.rawX - view.x

                    if (handleTouched) {
                        mHandlePaint.color = HANDLE_PRESSED_COLOR
                        mHandlePaint.style = Paint.Style.FILL
                        invalidate()
                        performClick = false
                    } else {
                        performClick = true
                    }
                }
            }
            return !performClick
        }
    }

    private fun moveTo(y: Int, x: Int) {
        val params = layoutParams as MarginLayoutParams
        //        Rect inScreenCoordinates = keepInScreen(y, x);
        params.topMargin = y
        params.leftMargin = x
        layoutParams = params
    }

    fun positionTo(x: Int, y: Int) {
        moveTo(y, x)
    }

    private fun keepInScreen(topMargin: Int, leftMargin: Int): Rect {
        var top = topMargin
        var left = leftMargin
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val height = measuredHeight
        val width = measuredWidth
        val rightCorrection = (parent as View).paddingRight
        val bottomCorrection = (parent as View).paddingBottom
        val leftCorrection = (parent as View).paddingLeft
        val topCorrection = (parent as View).paddingTop
        val rootBounds = Rect()
        (parent as View).getHitRect(rootBounds)
        rootBounds[rootBounds.left + leftCorrection, rootBounds.top + topCorrection, rootBounds.right - rightCorrection] =
            rootBounds.bottom - bottomCorrection
        if (top <= rootBounds.top) top =
            rootBounds.top else if (top + height > rootBounds.bottom) top =
            rootBounds.bottom - height
        if (left <= rootBounds.left) left =
            rootBounds.left else if (left + width > rootBounds.right) left =
            rootBounds.right - width
        return Rect(left, top, left + width, top + height)
    }

    private val mOnKeyboardActionListener: OnKeyboardActionListener =
        object : OnKeyboardActionListener {
            val CodeGrab = -10 //
            val CodeDelete = -5 // Keyboard.KEYCODE_DELETE
            val CodeCancel = -3 // Keyboard.KEYCODE_CANCEL
            val CodePrev = 55000
            val CodeAllLeft = 55001
            val CodeLeft = 55002
            val CodeRight = 55003
            val CodeAllRight = 55004
            val CodeNext = 55005
            val CodeClear = 55006
            val CodeCellUp = 1001
            val CodeCellDown = 1002
            val CodeCellLeft = 1003
            val CodeCellRight = 1004
            val CodeDecimalpoint = 46
            val CodeZero = 48
            override fun onKey(primaryCode: Int, keyCodes: IntArray) {
                val focusCurrent =
                    (getContext() as Activity).window.currentFocus
                if (focusCurrent == null || (focusCurrent.javaClass != EditText::class.java
                            && focusCurrent.javaClass.superclass != EditText::class.java)
                ) return
                val edittext = focusCurrent as EditText
                val editable = edittext.text
                val start = edittext.selectionStart
                val end = edittext.selectionEnd

                if (primaryCode == CodeCancel) {
                    hide()
                } else if (primaryCode == CodeDelete) {
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start)
                    } else if (editable != null && start != end) {
                        editable.delete(start, end)
                    }
                } else if (primaryCode == CodeClear) {
                    editable?.clear()
                } else if (primaryCode == CodeLeft) {
                    if (start > 0) edittext.setSelection(start - 1)
                } else if (primaryCode == CodeRight) {
                    if (start < edittext.length()) edittext.setSelection(start + 1)
                } else if (primaryCode == CodeAllLeft) {
                    edittext.setSelection(0)
                } else if (primaryCode == CodeAllRight) {
                    edittext.setSelection(edittext.length())
                } else if (primaryCode == CodePrev) {
                    @SuppressLint("WrongConstant") val focusNew =
                        edittext.focusSearch(View.FOCUS_BACKWARD)
                    focusNew?.requestFocus()
                } else if (primaryCode == CodeNext) {
                    @SuppressLint("WrongConstant") val focusNew =
                        edittext.focusSearch(View.FOCUS_FORWARD)
                    focusNew?.requestFocus()
                        ?: if (primaryCode == CodeCellUp || primaryCode == CodeCellDown || primaryCode == CodeCellLeft || primaryCode == CodeCellRight) {
                            // TODO
                        } else if (primaryCode == CodeGrab) {
                        }
                } else if (primaryCode == -2){
                    // 키보드 레이아웃 변경 (숫자 <-> 쿼티)
                    RxBus.publish(RxEvents.KeyboardChangeLayout())
                } else {
                    if (start != end) {
                        editable!!.delete(start, end)
                    }
                    if(isIncognitoMode) {
                        editable!!.insert(start, "*")

                    } else {
                        editable!!.insert(start, primaryCode.toChar().toString())
                    }
                }
            }

            override fun onPress(arg0: Int) {}
            override fun onRelease(primaryCode: Int) {}
            override fun onText(text: CharSequence) {}
            override fun swipeDown() {}
            override fun swipeLeft() {}
            override fun swipeRight() {}
            override fun swipeUp() {}
        }

    companion object {
        private const val TAG = "LockKeyboardView"
        private const val MOVE_THRESHOLD = 0
        private const val TOP_PADDING_DP = 28
        private val HANDLE_COLOR = Color.parseColor("#AAD1D6D9")
        private val HANDLE_PRESSED_COLOR = Color.parseColor("#D1D6D9")
        private const val HANDLE_ROUND_RADIOUS = 20.0f
        private val HANDLE_CORNER_EFFECT = CornerPathEffect(HANDLE_ROUND_RADIOUS)
        private var topPaddingPx: Int = 0
        private var width = 0
        private lateinit var mHandlePath: Path
        private lateinit var mHandlePaint: Paint
        var isAlignBottomCenter = true

    }

    init {
        topPaddingPx = dp2px(
            context!!,
            TOP_PADDING_DP.toFloat()
        ).toInt()
        this.onKeyboardActionListener = mOnKeyboardActionListener
        // Hide the standard keyboard initially
        (getContext() as Activity).window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setOnTouchListener(mKeyboardOnTouchListener)
        setPadding(
            0,
            dp2px(
                context,
                TOP_PADDING_DP.toFloat()
            ).toInt(),
            0,
            0
        )
        mHandlePaint = Paint()
        mHandlePaint.color = HANDLE_COLOR
        mHandlePaint.style = Paint.Style.FILL
        mHandlePaint.pathEffect = HANDLE_CORNER_EFFECT
        mHandlePath = Path()
    }
}