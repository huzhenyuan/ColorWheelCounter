
package com.zhenyuan.colorwheelcounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;
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
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class TimerPicker extends ImageView {

    private int mHour = -1;

    private int mMinute = 59;

    private int mSecond = 0;

    private final int MAX_HOUR = 24;

    private final int[] mColor = new int[] {
            0x00ffffff, 0x00ffffff, 0xffda00ff, 0xff8b00ff, 0xff6e00ff, 0xff2b00ff, 0xff0000ff,
            0xff3e00ff, 0xff00ffff, 0xff00ffc0, 0xff00ff7a, 0xff00ff33, 0xff00ff00, 0xff2eff00,
            0xff76ff00, 0xff00ff33, 0xffc1ff00, 0xffffff00, 0xffffd400, 0xffff9600, 0xffff3900,
            0xffff0000, 0xffff0039, 0xffff0090, 0xffff00df, 0xffef00ff, 0xffda00ff
    };

    private final int[] mHourColor = new int[] {
            0xffda00ff, 0xff8b00ff, 0xff6e00ff, 0xff2b00ff, 0xff0000ff, 0xff3e00ff, 0xff00ffff,
            0xff00ffc0, 0xff00ff7a, 0xff00ff33, 0xff00ff00, 0xff2eff00, 0xff76ff00, 0xff00ff33,
            0xffc1ff00, 0xffffff00, 0xffffd400, 0xffff9600, 0xffff3900, 0xffff0000, 0xffff0039,
            0xffff0090, 0xffff00df, 0xffef00ff, 0xffda00ff
    };

    private final float[] mHourColorPos = new float[] {
            0f / MAX_HOUR, 1f / MAX_HOUR, 2f / MAX_HOUR, 3f / MAX_HOUR, 4f / MAX_HOUR,
            5f / MAX_HOUR, 6f / MAX_HOUR, 7f / MAX_HOUR, 8f / MAX_HOUR, 9f / MAX_HOUR,
            10f / MAX_HOUR, 11f / MAX_HOUR, 12f / MAX_HOUR, 13f / MAX_HOUR, 14f / MAX_HOUR,
            15f / MAX_HOUR, 16f / MAX_HOUR, 17f / MAX_HOUR, 18f / MAX_HOUR, 19f / MAX_HOUR,
            20f / MAX_HOUR, 21f / MAX_HOUR, 22f / MAX_HOUR, 23f / MAX_HOUR, 24f / MAX_HOUR
    };

    private RectF mMinuteRectF = new RectF();

    private RectF mHourRectF = new RectF();

    private Paint mMinutePaint = new Paint();

    private SweepGradient mMinuteSweepGradient = null;

    private Paint mMinuteShadowPaint = new Paint();

    private SweepGradient mMinuteShadowSweepGradient = null;

    private Paint mCoverPaint = new Paint();

    private SweepGradient mCoverSweepGradient = null;

    private Paint mHourPaint = new Paint();

    private SweepGradient mHourSweepGradient = null;

    private Paint mMaskPaint = new Paint();

    private Paint mXferPaint = new Paint();

    private Bitmap mMaskBitmap = null;

    private Bitmap mHourBitmap = null;

    public TimerPicker(Context context) {
        super(context);
    }

    public TimerPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * 
     */
    public void setProgress(int hour, int minute, int second) {
        if (hour >= MAX_HOUR) {
            mHour = MAX_HOUR;
            mMinute = 0;
            mSecond = 0;
        } else {
            mHour = hour % MAX_HOUR;
            mMinute = minute % 60;
            mSecond = second % 60;
            setPaint();
        }
        invalidate();
    }

    public boolean setProgress(int currMinute, boolean clockwising) {
        // Log.i("clockwising:" + clockwising + ",minute:" + currMinute +
        // ",mMinute:" + mMinute);
        if (clockwising) {
            if (currMinute < 15 && currMinute >= 0 && mMinute > 45 && mMinute <= 59) {
                mHour++;
                if (mHour >= MAX_HOUR) {
                    mHour = MAX_HOUR;
                    mMinute = 0;
                    invalidate();
                } else {
                    mMinute = currMinute;
                    setPaint();
                    invalidate();
                }
                return true;
            } else {
                if (mHour < MAX_HOUR) {
                    mMinute = currMinute;
                    if (mHour >= 0) {
                        invalidate();
                    }
                } else {
                    mHour = MAX_HOUR;
                    mMinute = 0;
                    invalidate();
                }
                return true;
            }
        } else {
            if (mMinute < 15 && mMinute >= 0 && currMinute > 45 && currMinute <= 59) {
                mHour--;
                if (mHour <= -1) {
                    mHour = -1;
                    mMinute = 59;
                    invalidate();
                }
                if (mHour >= 0) {
                    mMinute = currMinute;
                    setPaint();
                    invalidate();
                }
                return true;
            } else {
                if (mHour >= MAX_HOUR) {
                    mHour = MAX_HOUR;
                    mMinute = 0;
                    invalidate();
                } else {
                    mMinute = currMinute;
                    if (mHour >= 0) {
                        invalidate();
                    }
                }
                return true;
            }
        }
    }

    public int[] getProgress() {
        return new int[] {
                mHour, mMinute, mSecond
        };
    }

    private void setPaint() {
        try {
            mMinuteRectF.set(0 + dip2px(20), 0 + dip2px(20), getWidth() - dip2px(20), getHeight()
                    - dip2px(20));
            mHourRectF.set(0, 0, getWidth(), getHeight());

            if (mMinuteShadowSweepGradient == null) {
                mMinuteShadowSweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2,
                        new int[] {
                                0x80000000, 0x00000000, 0x00000000
                        }, new float[] {
                                0.0f, 0.01f, 1.0f
                        });
                mMinuteShadowPaint.setStyle(Style.FILL);
                mMinuteShadowPaint.setShader(mMinuteShadowSweepGradient);
                mMinuteShadowPaint.setAntiAlias(true);
            }

            mMinuteSweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[] {
                    mColor[mHour + 2], mColor[mHour + 3]
            }, new float[] {
                    0.0f, 1.0f
            });
            mMinutePaint.setColor(0xffffffff);
            mMinutePaint.setStyle(Style.FILL);
            mMinutePaint.setShader(mMinuteSweepGradient);
            mMinutePaint.setAntiAlias(true);
            if (mHour == 0) {
                mCoverSweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[] {
                        mColor[mHour], mColor[mHour + 1]
                }, new float[] {
                        0.0f, 1.0f
                });

            } else {
                mCoverSweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, new int[] {
                        mColor[mHour + 1], mColor[mHour + 2]
                }, new float[] {
                        0.0f, 1.0f
                });
            }
            mCoverPaint.setStyle(Style.FILL);
            mCoverPaint.setShader(mCoverSweepGradient);
            mCoverPaint.setAntiAlias(true);

            if (mMaskBitmap == null) {
                RadialGradient r = new RadialGradient(getWidth() / 2, getHeight() / 2,
                        getHeight() / 2, new int[] {
                                0xffffffff, 0x00ffffff
                        }, new float[] {
                                0.8f, 0.9f
                        }, TileMode.CLAMP);
                mMaskPaint.setShader(r);
                mMaskPaint.setAntiAlias(true);
                mMaskBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
                mXferPaint.setAntiAlias(true);
                mXferPaint.setStyle(Style.FILL);
                mXferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                mHourBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            }

            if (mHourSweepGradient == null) {
                mHourSweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, mHourColor,
                        mHourColorPos);

                mHourPaint.setShader(mHourSweepGradient);
                mHourPaint.setAntiAlias(true);
            }
        } catch (Exception e) {
            mMinuteSweepGradient = null;
            mMinuteShadowSweepGradient = null;
            if (mMaskBitmap != null) {
                mMaskBitmap.recycle();
                mMaskBitmap = null;
            }
            mHourSweepGradient = null;
        }
    }

    public void recycleBitmap() {
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
            mHourBitmap.recycle();
            mHourBitmap = null;
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, String fileName) {
        File myCaptureFile = new File(fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNewMask() {
        Canvas c = new Canvas(mMaskBitmap);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        c.drawArc(mHourRectF, 0, mHour * 360 / MAX_HOUR + mMinute * 360 / MAX_HOUR / 60, true,
                mMaskPaint);
    }

    private void createHour() {
        Canvas c = new Canvas(mHourBitmap);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        c.drawArc(mHourRectF, 0, mHour * 360 / MAX_HOUR + mMinute * 360 / MAX_HOUR / 60, true,
                mHourPaint);
        c.drawBitmap(mMaskBitmap, 0, 0, mXferPaint);
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMinuteSweepGradient == null) {
            setPaint();
        }
        if (mHour <= -1) {
            return;
        }

        createNewMask();
        createHour();
        // saveBitmapToFile(mMaskBitmap,"/sdcard/a.png");
        // saveBitmapToFile(mHourBitmap,"/sdcard/b.png");

        canvas.save();
        canvas.rotate(-0.5f, mMinuteRectF.centerX(), mMinuteRectF.centerY());
        canvas.drawBitmap(mHourBitmap, 0, 0, null);
        canvas.restore();

        canvas.drawCircle(mMinuteRectF.centerX(), mMinuteRectF.centerY(),
                mMinuteRectF.height() / 2, mCoverPaint);

        if ((mMinute > 0 && mHour == 0) || mHour > 0) {
            canvas.save();
            canvas.rotate(mMinute * 6 - 1, mMinuteRectF.centerX(), mMinuteRectF.centerY());
            canvas.drawArc(mMinuteRectF, 0, 8, true, mMinuteShadowPaint);
            canvas.restore();
        }

        canvas.save();
        canvas.rotate(-0.5f, mMinuteRectF.centerX(), mMinuteRectF.centerY());
        canvas.drawArc(mMinuteRectF, 0, mMinute * 6, true, mMinutePaint);
        canvas.restore();

    }
}
