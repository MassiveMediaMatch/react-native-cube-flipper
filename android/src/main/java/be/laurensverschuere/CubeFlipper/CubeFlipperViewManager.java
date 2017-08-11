package be.laurensverschuere.CubeFlipper;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

public class CubeFlipperViewManager extends ViewGroupManager<CubeFlipperView> {

	@Override public String getName() {
		return CubeFlipperModule.REACT_CLASS;
	}

	@Override protected CubeFlipperView createViewInstance(ThemedReactContext reactContext) {
		return new CubeFlipperView(reactContext);
	}
}
