package com.zhenyuan.colorwheelcounter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TimerPicker mTimerPicker;
	private TextView mShowTimeView;
	private TimerStartTip mTimerStartTip;
	private ImageView mTimerFaceOuter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTimerPicker = (TimerPicker) this.findViewById(R.id.timerPicker1);
		mTimerStartTip = (TimerStartTip) this.findViewById(R.id.timerStartTip1);
		mTimerFaceOuter = (ImageView) this
				.findViewById(R.id.timerFaceOuter1);
		mShowTimeView = (TextView) this.findViewById(R.id.timerShow1);
		mTimerStartTip.doUpdate(true);
		mTimerFaceOuter.setOnTouchListener(new OnTouchListener() {
			private float centerX = 0.0f, centerY = 0.0f;

			private float lastX = 0.0f, lastY = 0.0f;

			/*
			 * Vx' cosTheta -sinTheta Vx { } = { } * { } Vy' sinTheta cosTheta
			 * Vy sinTheta = (Vx*Vy'-Vx'*Vy)/(Vx*Vx+Vy*Vy); normalized direction
			 * should avoid Vx to be zero, we select (1,1), that is angle 45
			 */

			private int normalizeToMinute(float x, float y) {
				float arrowX = x - centerX;
				float arrowY = y - centerY;
				double length = Math.sqrt(arrowX * arrowX + arrowY * arrowY);
				float normalArrowX = (float) (length / Math.sqrt(2));
				float normalArrowY = (float) (length / Math.sqrt(2));
				float sinTheta = (normalArrowX * arrowY - arrowX * normalArrowY)
						/ (normalArrowX * normalArrowX + normalArrowY
								* normalArrowY);
				int theta = (int) (Math.asin(sinTheta) * 360 / (Math.PI * 2));
				if (sinTheta > 0) {
					if (Math.abs(arrowY) > Math.abs(arrowX))
						theta = theta + 45;
					else
						theta = 180 - theta + 45;
				} else {
					if (Math.abs(arrowY) > Math.abs(arrowX))
						theta = 180 + 45 - theta;
					else
						theta = (theta + 45 + 360) % 360;
				}
				return ((theta + 90) % 360) / 6;
			}

			private boolean isClockwising(float x, float y) {
				float arrowX = x - centerX;
				float arrowY = y - centerY;
				double length = Math.sqrt(arrowX * arrowX + arrowY * arrowY);
				float lastArrowX = lastX - centerX;
				float lastArrowY = lastY - centerY;
				double lastLength = Math.sqrt(lastArrowX * lastArrowX
						+ lastArrowY * lastArrowY);
				float lastArrowScaledX = (float) (lastArrowX * length / lastLength);
				float lastArrowScaledY = (float) (lastArrowY * length / lastLength);
				float sinTheta = (lastArrowScaledX * arrowY - arrowX
						* lastArrowScaledY)
						/ (lastArrowScaledX * lastArrowScaledX + lastArrowScaledY
								* lastArrowScaledY);
				return sinTheta > 0;
			}

			private void setShowTime() {
				int[] progress = mTimerPicker.getProgress();
				String format = "%1$02d:%2$02d";
				if (progress[0] >= 0) {
					mShowTimeView.setText(String.format(format, progress[0],
							progress[1]));
				} else {
					mShowTimeView.setText(String.format(format, 0, 0));
				}
			}

			@Override
			public boolean onTouch(final View arg0, final MotionEvent ev) {
				mTimerStartTip.setVisibility(View.GONE);
				mTimerStartTip.doUpdate(false);
				switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					int[] location = new int[2];
					mTimerFaceOuter.getLocationOnScreen(location);
					centerX = location[0] + mTimerFaceOuter.getWidth() / 2;
					centerY = location[1] + mTimerFaceOuter.getHeight() / 2;
					mTimerPicker.setProgress(
							normalizeToMinute(ev.getRawX(), ev.getRawY()),
							isClockwising(ev.getRawX(), ev.getRawY()));
					lastX = ev.getRawX();
					lastY = ev.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					mTimerPicker.setProgress(
							normalizeToMinute(ev.getRawX(), ev.getRawY()),
							isClockwising(ev.getRawX(), ev.getRawY()));
					lastX = ev.getRawX();
					lastY = ev.getRawY();
					setShowTime();
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					break;
				}
				return true;
			}
		});
	}

}
