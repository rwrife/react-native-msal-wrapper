
# react-native-msal-wrapper

## Getting started

`$ npm install react-native-msal-wrapper --save`

### Mostly automatic installation

`$ react-native link react-native-msal-wrapper`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-msal-wrapper` and add `RNMsalWrapper.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMsalWrapper.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.microsoft.rnmsalwrapper.RNMsalWrapperPackage;` to the imports at the top of the file
  - Add `new RNMsalWrapperPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-msal-wrapper'
  	project(':react-native-msal-wrapper').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-msal-wrapper/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-msal-wrapper')
  	```


## Usage
```javascript
import RNMsalWrapper from 'react-native-msal-wrapper';

// TODO: What to do with the module?
RNMsalWrapper;
```
  