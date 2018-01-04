package be.laurensverschuere.CubeFlipper;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.util.Log;

/**
 * Created by laurensverschuere on 11/08/2017.
 */

public class CubeTransformer implements ViewPager.PageTransformer {

	private boolean threeDAnimations = true;

	public void setThreeDAnimations(boolean threeDAnimations) {
		this.threeDAnimations = threeDAnimations;
	}

	@Override public void transformPage(View page, float position) {
//		Log.d("TransformPage", "" + position);

		this.preTransform(page, position);

		if (!threeDAnimations || android.os.Build.MANUFACTURER.equalsIgnoreCase("HUAWEI")) {
			this.accordionTransform(page, position);
		}
		else {
			this.cubeTransform(page, position);
		}

		if (position == 0) {
			page.bringToFront();
		}
	}

	private void preTransform(View page, float position) {
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
	}

	private void cubeTransform(View page, float position) {
		if (position > 0) {
			page.setPivotX(0f);
			page.setPivotY(page.getHeight() * 0.5f);
			page.setRotationY(position * position * 60);
		}
		else {
			page.setPivotX(page.getWidth());
			page.setPivotY(page.getHeight() * 0.5f);
			page.setRotationY(- (position * position * 60));
		}
	}

	private void accordionTransform(View page, float position) {
		if (position > 0) {
			page.setPivotX(0f);
			page.setPivotY(page.getHeight() * 0.5f);
			page.setScaleX(1f - position);
		}
		else {
			page.setPivotX(page.getWidth());
			page.setPivotY(page.getHeight() * 0.5f);
			page.setScaleX(1f + position);
		}
	}
}
