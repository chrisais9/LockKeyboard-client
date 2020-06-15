package kr.koohyongmo.lockkeyboard_client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import kr.koohyongmo.lockkeyboard_client.utils.CalculateUtil;

/**
 * Created by KooHyongMo on 2020/06/15
 */
public class LockKeyboardView extends KeyboardView {
    private static final int MOVE_THRESHOLD = 0;
    private static final int TOP_PADDING_DP = 28;
    private static final int HANDLE_COLOR = Color.parseColor("#AAD1D6D9");
    private static final int HANDLE_PRESSED_COLOR = Color.parseColor("#D1D6D9");
    private static final float HANDLE_ROUND_RADIOUS = 20.0f;
    private static final CornerPathEffect HANDLE_CORNER_EFFECT = new CornerPathEffect(HANDLE_ROUND_RADIOUS);
    private static int topPaddingPx;
    private static int width;
    private static Path mHandlePath;
    private static Paint mHandlePaint;
    private static boolean allignBottomCenter = false;


    public LockKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        topPaddingPx = (int) CalculateUtil.INSTANCE.dp2px(context, (float) TOP_PADDING_DP);
        this.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.setOnTouchListener(mKeyboardOntTouchListener);
        this.setPadding(0, (int) CalculateUtil.INSTANCE.dp2px(context, (float) TOP_PADDING_DP), 0, 0);

        mHandlePaint = new Paint();
        mHandlePaint.setColor(HANDLE_COLOR);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setPathEffect(HANDLE_CORNER_EFFECT);

        mHandlePath = new Path();

    }

    public static boolean isAllignBottomCenter() {
        return allignBottomCenter;
    }

    public static void setAllignBottomCenter(boolean allignBottomCenter) {
        LockKeyboardView.allignBottomCenter = allignBottomCenter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAllignBottomCenter()) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            setLayoutParams(relativeLayoutParams);
        }
    }

    @Override
    public void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        width = xNew;
        drawHandle();
    }

    private void drawHandle() {
        mHandlePath.rewind();
        mHandlePath.moveTo(0, topPaddingPx);
        mHandlePath.lineTo(0, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx);
        // Draw this line twice to fix strange artifact in API21
        mHandlePath.lineTo(width, topPaddingPx);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = mHandlePaint;
        Path path = mHandlePath;
        canvas.drawPath(path, paint);

    }


    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }


    public void show(View v) {

        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);
        // TODO: Correct Position Keyboard
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
//        params.topMargin = v.getTop() + v.getHeight();
//        params.leftMargin = v.getLeft();
//        setLayoutParams(params);
    }

    public void hide() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }

    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext = (EditText) ((Activity) getContext()).findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) show(v);
                else hide();
            }
        });

        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                show(v);
            }
        });

        // Disable standard keyboard hard way
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edittext.setShowSoftInputOnFocus(false);
        } else {
            //For sdk versions [14-20]
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(edittext, false);
            } catch (Exception e) {
                // ignore
            }
        }

    }

    private OnTouchListener mKeyboardOntTouchListener = new OnTouchListener() {
        float dx;
        float dy;
        int moveToY;
        int moveToX;
        int distY;
        int distX;
        Rect inScreenCoordinates;
        boolean handleTouched = false;


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Use ViewGroup.MarginLayoutParams so as to work inside any layout
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            boolean performClick = false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (handleTouched) {
                        moveToY = (int) (event.getRawY() - dy);
                        moveToX = (int) (event.getRawX() - dx);
                        distY = moveToY - params.topMargin;
                        distX = moveToX - params.leftMargin;

                        if (Math.abs(distY) > MOVE_THRESHOLD ||
                                Math.abs(distX) > MOVE_THRESHOLD) {
                            // Ignore any distance before threshold reached
                            moveToY = moveToY - Integer.signum(distY) * Math.min(MOVE_THRESHOLD, Math.abs(distY));
                            moveToX = moveToX - Integer.signum(distX) * Math.min(MOVE_THRESHOLD, Math.abs(distX));

                            inScreenCoordinates = keepInScreen(moveToY, moveToX);
                            view.setY(inScreenCoordinates.top);
                            view.setX(inScreenCoordinates.left);
                        }
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (handleTouched) {
                        // reset handle color
                        mHandlePaint.setColor(HANDLE_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();

                        performClick = false;
                    } else {
                        performClick = true;
                    }

                    break;

                case MotionEvent.ACTION_DOWN:
                    handleTouched = event.getY() <= getPaddingTop(); // Allow move only wher touch on top padding
                    dy = event.getRawY() - view.getY();
                    dx = event.getRawX() - view.getX();

                    //change handle color on tap
                    if (handleTouched) {
                        mHandlePaint.setColor(HANDLE_PRESSED_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;
            }
            return !performClick;
        }


    };

    private void moveTo(int y, int x) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        ;
//        Rect inScreenCoordinates = keepInScreen(y, x);
        params.topMargin = y;
        params.leftMargin = x;
        setLayoutParams(params);
    }


    public void positionTo(int x, int y) {
        moveTo(y, x);
    }

    private Rect keepInScreen(int topMargin, int leftMargin) {
        int top = topMargin;
        int left = leftMargin;
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        //TODO: Try to explain this !!!
        int rightCorrection = ((View) getParent()).getPaddingRight();
        int botomCorrection = ((View) getParent()).getPaddingBottom();
        int leftCorrection = ((View) getParent()).getPaddingLeft();
        int topCorrection = ((View) getParent()).getPaddingTop();

        Rect rootBounds = new Rect();
        ((View) getParent()).getHitRect(rootBounds);
        rootBounds.set(rootBounds.left + leftCorrection, rootBounds.top + topCorrection, rootBounds.right - rightCorrection, rootBounds.bottom - botomCorrection);

        if (top <= rootBounds.top)
            top = rootBounds.top;
        else if (top + height > rootBounds.bottom)
            top = rootBounds.bottom - height;

        if (left <= rootBounds.left)
            left = rootBounds.left;
        else if (left + width > rootBounds.right)
            left = rootBounds.right - width;

//            Log.e("x0:"+rootBounds.left+" y0:"+rootBounds.top+" Sx:"+rootBounds.right+" Sy:"+rootBounds.bottom, "INPUT:left:"+leftMargin+" top:"+topMargin+
//                    " OUTPUT:left:"+left+" top:"+top+" right:"+(left + getWidth())+" bottom:"+(top + getHeight()));
        return new Rect(left, top, left + width, top + height);
//        return new Rect(leftMargin, topMargin, leftMargin + width, topMargin + height);
    }

    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
        public final static int CodeGrab = -10; //
        public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
        public final static int CodePrev = 55000;
        public final static int CodeAllLeft = 55001;
        public final static int CodeLeft = 55002;
        public final static int CodeRight = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext = 55005;
        public final static int CodeClear = 55006;

        public final static int CodeCellUp = 1001;
        public final static int CodeCellDown = 1002;
        public final static int CodeCellLeft = 1003;
        public final static int CodeCellRight = 1004;
        public final static int CodeDecimalpoint = 46;
        public final static int CodeZero = 48;

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText or extension of EditText and its Editable
            View focusCurrent = ((Activity) getContext()).getWindow().getCurrentFocus();
            if (focusCurrent == null || (focusCurrent.getClass() != EditText.class
                    && focusCurrent.getClass().getSuperclass() != EditText.class)) return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            int end = edittext.getSelectionEnd();
            // Apply the key to the edittext
            if (primaryCode == CodeCancel) {
                hide();
            } else if (primaryCode == CodeDelete) {
                if (editable != null && start > 0) {
                    editable.delete(start - 1, start);
                } else if (editable != null && start != end) { // delete selection
                    editable.delete(start, end);
                }
            } else if (primaryCode == CodeClear) {
                if (editable != null) editable.clear();
            } else if (primaryCode == CodeLeft) {
                if (start > 0) edittext.setSelection(start - 1);
            } else if (primaryCode == CodeRight) {
                if (start < edittext.length()) edittext.setSelection(start + 1);
            } else if (primaryCode == CodeAllLeft) {
                edittext.setSelection(0);
            } else if (primaryCode == CodeAllRight) {
                edittext.setSelection(edittext.length());
            } else if (primaryCode == CodePrev) {
                @SuppressLint("WrongConstant") View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
                if (focusNew != null) focusNew.requestFocus();
            } else if (primaryCode == CodeNext) {
                @SuppressLint("WrongConstant") View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                if (focusNew != null) focusNew.requestFocus();
                else if (primaryCode == CodeCellUp || primaryCode == CodeCellDown || primaryCode == CodeCellLeft || primaryCode == CodeCellRight) {
                    // TODO
                } else if (primaryCode == CodeGrab) {

                }
            } else {
                if (start != end) {
                    editable.delete(start, end);
                }
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }
    };
}
