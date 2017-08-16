package be.laurensverschuere.CubeFlipper;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.views.viewpager.ReactViewPagerManager;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

public class CubeFlipperViewManager extends ReactViewPagerManager {

	protected static final String REACT_CLASS = "RCTCubeFlipper";

	@Override public String getName() {
		return CubeFlipperViewManager.REACT_CLASS;
	}

	@Override protected CubeFlipperView createViewInstance(ThemedReactContext reactContext) {
		return new CubeFlipperView(reactContext);
	}

}
