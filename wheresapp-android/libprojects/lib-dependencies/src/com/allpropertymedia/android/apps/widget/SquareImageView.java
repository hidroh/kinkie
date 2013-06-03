package com.allpropertymedia.android.apps.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A square {@code ImageView} which maintains a predefined aspect ratio,
 * here is square {@code ImageView}
 *  
 * @author ichen
 *
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
