package be.laurensverschuere.CubeFlipper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.View;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.views.viewpager.ReactViewPager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by laurensverschuere on 10/08/2017.
 */

public class CubeFlipperView extends ReactViewPager {
	public CubeFlipperView(ReactContext reactContext) {
		super(reactContext);

		this.setPageTransformer(false, new CubeTransformer());
	}
}
