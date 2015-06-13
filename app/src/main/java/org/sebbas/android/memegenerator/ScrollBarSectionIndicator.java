package org.sebbas.android.memegenerator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

public class ScrollBarSectionIndicator extends SectionTitleIndicator<Character> {

    private static final String TAG = "ScrollBarSectionIndicator";

    public ScrollBarSectionIndicator(Context context) {
        super(context);
    }

    public ScrollBarSectionIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollBarSectionIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(Character character) {
        setTitleText(character + "");
    }

}