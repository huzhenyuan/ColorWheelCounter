
package com.zhenyuan.colorwheelcounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TimerStartTip extends ImageView {
    private Bitmap mStartTipBitmap = null;

    private Paint mPaint = new Paint();

    private int[] mAlpha = new int[] {
            -100, -50, 0, 50, 100, 150, 200, 250
    };

    private Handler mHandler = new Handler();

    public TimerStartTip(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        InputStream is = context.getResources().openRawResource(R.drawable.timer_start_tip);
        mStartTipBitmap = BitmapFactory.decodeStream(is);
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPaint.setAntiAlias(true);
    }

    public TimerStartTip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimerStartTip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void recycleBitmap() {
        if (mStartTipBitmap != null) {
            mStartTipBitmap.recycle();
            mStartTipBitmap = null;
        }
    }

    public void doUpdate(boolean go) {
        invalidate();
        if (go) {
            for (int i = 0; i < mAlpha.length; i++) {
                mAlpha[i] -= 50;
                if (mAlpha[i] < -300) {
                    mAlpha[i] = 250;
                }
            }
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    doUpdate(true);
                }
            }, 100);
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        for (int i = 0; i < mAlpha.length; i++) {
            if (mAlpha[i] > 0) {
                mPaint.setAlpha(mAlpha[i]);
                canvas.drawBitmap(mStartTipBitmap, 0, 0, mPaint);
            }
            canvas.rotate(8, getWidth() / 2, getHeight() / 2);
        }
        canvas.restore();
    }
}
