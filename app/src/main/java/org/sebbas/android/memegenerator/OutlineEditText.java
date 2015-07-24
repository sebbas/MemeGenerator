package org.sebbas.android.memegenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

public class OutlineEditText extends EditText {

    public static final String TAG = "OutlineEditText";
    private static final String ARG_TEXT = "ARG_TEXT";

    private Paint mTextPaint;
    private Paint mTextPaintOutline;
    private String mText;
    private int mInnerColor;
    private int mOutlineColor;
    private Typeface mTypeface;
    private float mTextSize;

    public OutlineEditText(Context context) {
        super(context);

        initDefaultValues();
        setupText();
    }

    public OutlineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        initDefaultValues();
        setupText();
    }

    public OutlineEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initDefaultValues();
        setupText();
    }

    private void setupText() {

        mTextPaint = new Paint();
        mTextPaintOutline = new Paint();

        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mInnerColor);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTypeface(mTypeface);

        mTextPaintOutline.setAntiAlias(true);
        mTextPaintOutline.setTextSize(mTextSize);
        mTextPaintOutline.setColor(mOutlineColor);
        mTextPaintOutline.setTextAlign(Align.CENTER);
        mTextPaintOutline.setStyle(Paint.Style.STROKE);
        mTextPaintOutline.setStrokeWidth(7);
        mTextPaintOutline.setTypeface(mTypeface);

        // Make sure that underlying text is setup correctly
        setTextSize(36);
        setText(mText);
        setTextColor(getResources().getColor(android.R.color.transparent));
        setBackground(null);
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        setPadding(3, 3, 3, 3);

        requestLayout();
        invalidate();
    }

    private void initDefaultValues() {

        mInnerColor = 0xFFFFFFFF;
        mOutlineColor = 0xFF000000;
    }

    private void setText(String text) {
        mText = text.toString();
    }

    public void changeTextColor(int innerColor, int outlineColor) {
        mInnerColor = innerColor;
        mOutlineColor = outlineColor;
        setupText();
    }

    public void changeTypeface(Typeface typeface) {
        mTypeface = typeface;
        setupText();
    }

    public void changeTextSize(float textSize) {
        mTextSize = textSize;
        setupText();
    }

    public void changeText(String text) {
        mText = text;
        setupText();
    }

    public void changeAll(int innerColor, int outlineColor, Typeface typeface, float textSize, String text) {
        mInnerColor = innerColor;
        mOutlineColor = outlineColor;
        mTypeface = typeface;
        mTextSize = Utils.spToPixels(getContext(), (float) textSize);
        mText = text.toString();
        setupText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mText, getWidth()/2, (getHeight() - (int) mTextPaint.ascent())/2, mTextPaintOutline);
        canvas.drawText(mText, getWidth()/2, (getHeight() - (int) mTextPaint.ascent())/2, mTextPaint);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setText(text.toString());
    }
}
