package be.laurensverschuere.CubeFlipper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by laurensverschuere on 16/08/2017.
 */

public class PageSelectedEvent extends Event<PageSelectedEvent> {

	public static final String EVENT_NAME = "topPageSelected";

	private final int mPosition;
	private final boolean mManualScroll;

	protected PageSelectedEvent(int viewTag, int position, boolean manualScroll) {
		super(viewTag);
		mPosition = position;
		mManualScroll = manualScroll;
	}

	@Override
	public String getEventName() {
		return EVENT_NAME;
	}

	@Override
	public void dispatch(RCTEventEmitter rctEventEmitter) {
		rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
	}

	private WritableMap serializeEventData() {
		WritableMap eventData = Arguments.createMap();
		eventData.putInt("position", mPosition);
		eventData.putBoolean("manualScroll", mManualScroll);
		return eventData;
	}
}