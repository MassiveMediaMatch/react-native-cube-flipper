package be.laurensverschuere.CubeFlipper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.View;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

public class CubeFlipperView extends ViewPager {
	private int currentIndex = 0;
	private int nextIndex = 0;
	private int numberOfChilds = 0;
	private View currentView;
	private View nextView;
	private float downX;
	private boolean moving = false;
	private PagerAdapter mAdapter;
	//private ArrayList<View> childs = new ArrayList<>();

	public CubeFlipperView(Context context) {
		super(context);

		this.mAdapter = new PagerAdapter() {
			@Override public int getCount() {
				return getChildCount();
			}

			@Override public Object instantiateItem(ViewGroup container, int position) {
				//return super.instantiateItem(container, position);
				View view = getChildAt(position);
				//container.addView(view);

				return view;
			}

			@Override public void destroyItem(ViewGroup container, int position, Object object) {
				//container.removeViewAt(position);
			}

			@Override public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}
		};

		this.setAdapter(this.mAdapter);
		this.setPageTransformer(false, new CubeTransformer());
	}

	@Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);

		this.mAdapter.notifyDataSetChanged();
	}

	@Override public void removeView(View view) {
		super.removeView(view);

		this.mAdapter.notifyDataSetChanged();
	}

	/*
	@Override public boolean onTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);

		Log.d("onTouchEvent", ev.toString());
		Log.d("x", "" + ev.getRawX());

		switch (action) {
			case MotionEvent.ACTION_MOVE:
				if (ev.getRawX() > this.downX) {
					Log.d("Direction", "right");



					return true;
				}
				else if (ev.getRawX() < this.downX) {
					Log.d("Direction", "left");

					this.nextIndex = 0;
					if (this.currentIndex + 1 < this.numberOfChilds) {
						this.nextIndex = this.currentIndex + 1;
					}

					if (!this.moving) {
						this.currentView = this.getChildAt(this.currentIndex);
						this.nextView = this.getChildAt(this.nextIndex);

						this.moving = true;
					}
					else {
						Log.d("Direction left", "moving");
						//this.getChildAt(this.currentIndex).setTranslationX(ev.getRawX() - this.downX);

						float position = - this.getWidth()/ev.getRawX();
						//this.getChildAt(this.currentIndex).setPivotX(0f);
						//this.getChildAt(this.currentIndex).setPivotY(this.getHeight() * 0.5f);
						//this.getChildAt(this.currentIndex).setRotationY(90f * position);


						//this.getChildAt(this.currentIndex).setPivotX(position < 0f ? this.getChildAt(this.currentIndex).getWidth() : 0f);
						//this.getChildAt(this.currentIndex).setPivotY(this.getChildAt(this.currentIndex).getHeight() * 0.5f);
						//
						//if (position > 0) {
						//	this.getChildAt(this.currentIndex).setRotationY(position * position * 60);
						//}
						//else {
						//	this.getChildAt(this.currentIndex).setRotationY(- (position * position * 60));
						//}
					}

					return true;
				}
				break;

			case MotionEvent.ACTION_UP:
				this.moving = false;
				break;
		}

		return false;
	}

	@Override public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = MotionEventCompat.getActionMasked(ev);

		Log.d("onInterceptTouchEvent", ev.toString());
		Log.d("downX", "" + this.downX);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				this.downX = ev.getRawX();

				break;

			case MotionEvent.ACTION_MOVE:
				return true;
		}

		return false;
	}
*/

	@Override public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
		super.updateViewLayout(view, params);

		this.mAdapter.notifyDataSetChanged();
	}
}
