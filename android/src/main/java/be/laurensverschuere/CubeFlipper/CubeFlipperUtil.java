package be.laurensverschuere.CubeFlipper;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by lowiehuyghe on 19/10/2017.
 */

public class CubeFlipperUtil {

	private static boolean hasDeterminedRtl;
	private static boolean isRtlEnabled;

	public static boolean getShouldAdaptForRtl() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean isRTL(Context context) {
		return context.getResources().getBoolean(R.bool.isRtl);
	}

	public static boolean getShouldAdaptPagerPositionToRtl(Context context) {
		if (!hasDeterminedRtl) {
			isRtlEnabled = CubeFlipperUtil.getShouldAdaptForRtl()
					&& CubeFlipperUtil.isRTL(context);
			hasDeterminedRtl = true;
		}

		return isRtlEnabled;
	}

	public static int getProcessedPagerAdapterPosition(Context context, int max, int position) {
		if (CubeFlipperUtil.getShouldAdaptPagerPositionToRtl(context)) {
			position = max - position - 1;
			if (position < 0) {
				position = 0;
			}
		}
		return position;
	}

	public static void fixLayoutDirection(View view) {
		ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_LTR);
	}

	public static void resetLayoutDirection(View view) {
		ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_LOCALE);
	}

}
