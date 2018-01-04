package be.laurensverschuere.CubeFlipper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.viewpager.ReactViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

public class CubeFlipperViewPager extends ViewPager {

	public enum SwipeDirection {
		all, left, right, none ;
	}

	private class CubePagerAdapter extends PagerAdapter {

		private final List<View> mViews = new ArrayList<>();
		private boolean mIsViewPagerInIntentionallyInconsistentState = false;

		void addView(View child, int index) {
			mViews.add(index, child);
			notifyDataSetChanged();
			// This will prevent view pager from detaching views for pages that are not currently selected
			// We need to do that since {@link ViewPager} relies on layout passes to position those views
			// in a right way (also thanks to {@link ReactViewPagerManager#needsCustomLayoutForChildren}
			// returning {@code true}). Currently we only call {@link View#measure} and
			// {@link View#layout} after yoga step.

			// TODO(7323049): Remove this workaround once we figure out a way to re-layout some views on
			// request
			setOffscreenPageLimit(mViews.size());
		}

		void removeViewAt(int index) {
			mViews.remove(index);
			notifyDataSetChanged();

			// TODO(7323049): Remove this workaround once we figure out a way to re-layout some views on
			// request
			setOffscreenPageLimit(mViews.size());
		}

		/**
		 * Replace a set of views to the ViewPager adapter and update the ViewPager
		 */
		void setViews(List<View> views) {
			mViews.clear();
			mViews.addAll(views);
			notifyDataSetChanged();

			// we want to make sure we return POSITION_NONE for every view here, since this is only
			// called after a removeAllViewsFromAdapter
			mIsViewPagerInIntentionallyInconsistentState = false;
		}

		/**
		 * Remove all the views from the adapter and de-parents them from the ViewPager
		 * After calling this, it is expected that notifyDataSetChanged should be called soon
		 * afterwards.
		 */
		void removeAllViewsFromAdapter(ViewPager pager) {
			mViews.clear();
			pager.removeAllViews();
			// set this, so that when the next addViews is called, we return POSITION_NONE for every
			// entry so we can remove whichever views we need to and add the ones that we need to.
			mIsViewPagerInIntentionallyInconsistentState = true;
		}

		View getViewAt(int index) {
			return mViews.get(index);
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public int getItemPosition(Object object) {
			// if we've removed all views, we want to return POSITION_NONE intentionally
			return mIsViewPagerInIntentionallyInconsistentState || !mViews.contains(object) ?
					POSITION_NONE : mViews.indexOf(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = mViews.get(position);
			container.addView(view, 0, generateDefaultLayoutParams());
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public int getProcessedPosition(int position) {
			return CubeFlipperUtil.getProcessedPagerAdapterPosition(getContext(), this.getCount(), position);
		}
	}

	private class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int actualPosition = getAdapter().getProcessedPosition(position);
			mEventDispatcher.dispatchEvent(
					new PageScrollEvent(getId(), actualPosition, positionOffset, mIsCurrentItemFromJs == false));
		}

		@Override
		public void onPageSelected(int position) {
			int actualPosition = getAdapter().getProcessedPosition(position);
			mEventDispatcher.dispatchEvent(new PageSelectedEvent(getId(), actualPosition, mIsCurrentItemFromJs == false));
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			String pageScrollState;
			switch (state) {
				case SCROLL_STATE_IDLE:
					pageScrollState = "idle";
					break;
				case SCROLL_STATE_DRAGGING:
					pageScrollState = "dragging";
					break;
				case SCROLL_STATE_SETTLING:
					pageScrollState = "settling";
					break;
				default:
					throw new IllegalStateException("Unsupported pageScrollState");
			}
			mEventDispatcher.dispatchEvent(
					new PageScrollStateChangedEvent(getId(), pageScrollState));
		}
	}

	public class ScrollerCustomDuration extends Scroller {

		private double mScrollFactor = 1;

		public ScrollerCustomDuration(Context context) {
			super(context);
		}

		public ScrollerCustomDuration(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		//@SuppressLint("NewApi")
		public ScrollerCustomDuration(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}

		/**
		 * Set the factor by which the duration will change
		 */
		public void setScrollDurationFactor(double scrollFactor) {
			mScrollFactor = scrollFactor;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
		}

	}

	private final EventDispatcher mEventDispatcher;
	private boolean mScrollEnabled = true;
	private boolean mIsCurrentItemFromJs = false;
	private float initialXValue;
	private SwipeDirection direction;
	private ScrollerCustomDuration mScroller = null;
	private CubeTransformer mPageTransformer = null;

	public CubeFlipperViewPager(ReactContext reactContext) {
		super(reactContext);

		try {
			Field scroller = ViewPager.class.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);

			mScroller = new ScrollerCustomDuration(getContext(),
					(Interpolator) interpolator.get(null));
			scroller.set(this, mScroller);
		} catch (Exception e) {
		}

		mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
		mIsCurrentItemFromJs = false;
		addOnPageChangeListener(new PageChangeListener());

		CubePagerAdapter adapter = new CubePagerAdapter();
		setAdapter(adapter);
		int startPosition = adapter.getProcessedPosition(0);
		if (startPosition > 0) {
			this.setCurrentItem(startPosition, false);
		}

		mPageTransformer = new CubeTransformer();
		this.setPageTransformer(false, mPageTransformer);

		CubeFlipperUtil.fixLayoutDirection(this);
	}

	/**
	 * Set the factor by which the duration will change
	 */
	public void setScrollDurationFactor(double scrollFactor) {
		mScroller.setScrollDurationFactor(scrollFactor);
	}

	@Override
	public CubePagerAdapter getAdapter() {
		return (CubePagerAdapter) super.getAdapter();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mScrollEnabled) {
			return false;
		}

		if (!IsSwipeAllowed(ev)) {
			return false;
		}

		if (super.onInterceptTouchEvent(ev)) {
			NativeGestureUtil.notifyNativeGestureStarted(this, ev);
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mScrollEnabled) {
			return false;
		}

		if (!IsSwipeAllowed(ev)) {
			return false;
		}

		return super.onTouchEvent(ev);
	}

	private boolean IsSwipeAllowed(MotionEvent event) {
		if(this.direction == SwipeDirection.all) {
			return true;
		}

		if(this.direction == SwipeDirection.none ) {//disable any swipe
			return false;
		}

		if(event.getAction()==MotionEvent.ACTION_DOWN) {
			initialXValue = event.getX();
			return true;
		}

		if(event.getAction()==MotionEvent.ACTION_MOVE) {
			try {
				float diffX = event.getX() - initialXValue;
				if (diffX > 0) {
					// swipe from left to right detected
					if (!CubeFlipperUtil.getShouldAdaptPagerPositionToRtl(getContext())) {
						if (direction == SwipeDirection.right) {
							return false;
						}
					} else if (direction == SwipeDirection.left) {
						return false;
					}
				} else if (diffX < 0) {
					// swipe from right to left detected
					if (!CubeFlipperUtil.getShouldAdaptPagerPositionToRtl(getContext())) {
						if (direction == SwipeDirection.left) {
							return false;
						}
					} else if (direction == SwipeDirection.right) {
						return false;
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return true;
	}

	public void setAllowedSwipeDirection(SwipeDirection direction) {
		this.direction = direction;
	}

	public void setThreeDAnimations(boolean threeDAnimations) {
		mPageTransformer.setThreeDAnimations(threeDAnimations);
	}

	public void setCurrentItemFromJs(int item, boolean animated, double duration) {
		int actualItem = getAdapter().getProcessedPosition(item);

		mIsCurrentItemFromJs = true;
		setScrollDurationFactor(duration);
		setCurrentItem(actualItem, animated);
		setScrollDurationFactor(1); // reset
		mIsCurrentItemFromJs = false;
	}

	public void setScrollEnabled(boolean scrollEnabled) {
		mScrollEnabled = scrollEnabled;
	}

	/*package*/ void addViewToAdapter(View child, int index) {
		int actualIndex = getAdapter().getProcessedPosition(index);
		getAdapter().addView(child, actualIndex);

		int currentActualIndex = getAdapter().getProcessedPosition(getCurrentItem());
		if (currentActualIndex > 0) {
			setCurrentItem(getAdapter().getProcessedPosition(0), false);
		}
	}

	/*package*/ void removeViewFromAdapter(int index) {
		int actualIndex = getAdapter().getProcessedPosition(index);
		getAdapter().removeViewAt(actualIndex);
	}

	/*package*/ int getViewCountInAdapter() {
		return getAdapter().getCount();
	}

	/*package*/ View getViewFromAdapter(int index) {
		int actualIndex = getAdapter().getProcessedPosition(index);
		return getAdapter().getViewAt(actualIndex);
	}

	public void setViews(List<View> views) {
		getAdapter().setViews(views);
	}

	public void removeAllViewsFromAdapter() {
		getAdapter().removeAllViewsFromAdapter(this);
	}
}
