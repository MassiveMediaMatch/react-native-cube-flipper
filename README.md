
# react-native-cube-flipper

## Getting started

`$ npm install react-native-cube-flipper --save`

### Mostly automatic installation

`$ react-native link react-native-cube-flipper`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-cube-flipper` and add `RNCubeFlipper.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNCubeFlipper.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import be.laurensverschuere.CubeFlipperPackage;` to the imports at the top of the file
  - Add `new CubeFlipperPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-cube-flipper'
  	project(':react-native-cube-flipper').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-cube-flipper/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-cube-flipper')
  	```

## Usage
```javascript
import CubeFlipper from 'react-native-cube-flipper';
```
  