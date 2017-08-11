
package be.laurensverschuere.CubeFlipper;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class CubeFlipperModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  public static final String REACT_CLASS = "CubeFlipper";

  public CubeFlipperModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return REACT_CLASS;
  }
}