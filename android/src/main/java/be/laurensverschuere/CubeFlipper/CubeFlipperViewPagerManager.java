package be.laurensverschuere.CubeFlipper;

import android.view.View;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.viewpager.ReactViewPager;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

@ReactModule(name = CubeFlipperViewPagerManager.REACT_CLASS)
public class CubeFlipperViewPagerManager extends ViewGroupManager<CubeFlipperViewPager> {

	protected static final String REACT_CLASS = "RCTCubeFlipper";
	public static final int COMMAND_SET_PAGE = 1;
	public static final int COMMAND_SET_PAGE_WITHOUT_ANIMATION = 2;

	@Override public String getName() {
		return CubeFlipperViewPagerManager.REACT_CLASS;
	}

	@Override protected CubeFlipperViewPager createViewInstance(ThemedReactContext reactContext) {
		return new CubeFlipperViewPager(reactContext);
	}


	@ReactProp(name = "scrollEnabled", defaultBoolean = true)
	public void setScrollEnabled(CubeFlipperViewPager viewPager, boolean value) {
		viewPager.setScrollEnabled(value);
	}

	@Override
	public boolean needsCustomLayoutForChildren() {
		return true;
	}

	@Override
	public Map getExportedCustomDirectEventTypeConstants() {
		return MapBuilder.of(
				PageScrollEvent.EVENT_NAME, MapBuilder.of("registrationName", "onPageScroll"),
				PageScrollStateChangedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onPageScrollStateChanged"),
				PageSelectedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onPageSelected"));
	}

	@Override
	public Map<String,Integer> getCommandsMap() {
		return MapBuilder.of(
				"setPage",
				COMMAND_SET_PAGE,
				"setPageWithoutAnimation",
				COMMAND_SET_PAGE_WITHOUT_ANIMATION);
	}

	@Override
	public void receiveCommand(
			CubeFlipperViewPager viewPager,
			int commandType,
			@Nullable ReadableArray args) {
		Assertions.assertNotNull(viewPager);
		Assertions.assertNotNull(args);
		switch (commandType) {
			case COMMAND_SET_PAGE: {
				viewPager.setCurrentItemFromJs(args.getInt(0), true);
				return;
			}
			case COMMAND_SET_PAGE_WITHOUT_ANIMATION: {
				viewPager.setCurrentItemFromJs(args.getInt(0), false);
				return;
			}
			default:
				throw new IllegalArgumentException(String.format(
						"Unsupported command %d received by %s.",
						commandType,
						getClass().getSimpleName()));
		}
	}

	@Override
	public void addView(CubeFlipperViewPager parent, View child, int index) {
		parent.addViewToAdapter(child, index);
	}

	@Override
	public int getChildCount(CubeFlipperViewPager parent) {
		return parent.getViewCountInAdapter();
	}

	@Override
	public View getChildAt(CubeFlipperViewPager parent, int index) {
		return parent.getViewFromAdapter(index);
	}

	@Override
	public void removeViewAt(CubeFlipperViewPager parent, int index) {
		parent.removeViewFromAdapter(index);
	}
}
