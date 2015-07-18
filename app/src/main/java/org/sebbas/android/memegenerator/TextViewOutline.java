package org.sebbas.android.memegenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewOutline extends TextView {

    private Paint mTextPaint;
    private Paint mTextPaintOutline;
    private String mText = "";
    private Context mContext;

    public TextViewOutline(Context context) {
        this(context, null, 0);
    }

    public TextViewOutline(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewOutline(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mTextPaint = new Paint();
        mTextPaintOutline = new Paint();
    }

    public void setText(String text) {

        // set text size and colors
        float size = Utils.spToPixels(getContext(), (float) 25);
        int outlineColor = 0xFF000000;
        int innerColor = 0xFFFFFFFF;
        Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/impact.ttf");

        mText = text.toString();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(size);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(innerColor);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTypeface(typeFace);

        mTextPaintOutline.setAntiAlias(true);
        mTextPaintOutline.setTextSize(size);
        mTextPaintOutline.setColor(outlineColor);
        mTextPaintOutline.setTextAlign(Align.CENTER);
        mTextPaintOutline.setStyle(Paint.Style.STROKE);
        mTextPaintOutline.setStrokeWidth(7);
        mTextPaintOutline.setTypeface(typeFace);

        setPadding(3, 3, 3, 3);

        requestLayout();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mText, getWidth()/2, (getHeight() - (int) mTextPaint.ascent())/2, mTextPaintOutline);
        canvas.drawText(mText, getWidth()/2, (getHeight() - (int) mTextPaint.ascent())/2, mTextPaint);
    }
}
