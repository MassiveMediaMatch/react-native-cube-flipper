package be.laurensverschuere.CubeFlipper;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by laurensverschuere on 11/08/2017.
 */

public class CubeTransformer implements ViewPager.PageTransformer {

	@Override public void transformPage(View page, float position) {
		//Log.d("TransformPage", "" + position);

		page.setRotationX(0);
		page.setRotationY(0);
		page.setRotation(0);
		page.setScaleX(1);
		page.setScaleY(1);
		page.setPivotX(0);
		page.setPivotY(0);
		page.setTranslationY(0);
		page.setTranslationX(0f);

		page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
		page.setEnabled(false);

		page.setPivotX(position < 0f ? page.getWidth() : 0f);
		page.setPivotY(page.getHeight() * 0.5f);

		if (position > 0) {
			page.setRotationY(position * position * 60);
		}
		else {
			page.setRotationY(- (position * position * 60));
		}

		if (position == 0) {
			page.bringToFront();
		}
	}
}
