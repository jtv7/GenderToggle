[![Release](https://jitpack.io/v/jtv7/GenderToggle.svg)](https://jitpack.io/#jtv7/GenderToggle)

# GenderToggle

This library is a custom Switch toggle widget inspired by this [dribbble shot](https://dribbble.com/shots/3461813-Gender-toggle). 

![GifSample](https://raw.githubusercontent.com/jtv7/GenderToggle/master/toggle.gif)

## Gradle 
Add the JitPack repository in your build.gradle at the end of repositories:
```
allprojects {
    repositories {
    	...
        maven { url 'https://jitpack.io' }
    }
}
```
And add the dependencies
```
dependencies {
    compile 'com.github.jtv7:GenderToggle:1.0'
}
```
## Sample
Please see the [sample app](app/src/main) for a library usage example.

## Wiki
#### Usage:
Add GenderToggle to your view hieararchy. Either programatically or using xml:
```xml
<com.jtv7.gendertogglelib.GenderToggle
        android:id="@+id/genderToggle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:gt_default_selection="male" />
```

### API
#### General

Default selection can be set using:
```xml
<com.jtv7.gendertogglelib.GenderToggle
  app:gt_default_selection="male|female" />
```
Enabling/disabling glow:
```xml
<com.jtv7.gendertogglelib.GenderToggle
  app:gt_glow_enabled="true|false" />
```
Setting custom male color:
```xml
<com.jtv7.gendertogglelib.GenderToggle
  app:gt_male_color="#00F8FC" />
```
Setting custom female color:
```xml
<com.jtv7.gendertogglelib.GenderToggle
  app:gt_female_color="#FB4D7A" />
```

Can also be set with the following setter methods:
```java
genderToggle.setChecked(GenderToggle.Checked.MALE);       //Default selection
genderToggle.setGlowEnabled(true);                        //Glow enabled
genderToggle.setMaleColor(Color.parseColor("#00F8FC"));   //Male color
genderToggle.setFemaleColor(Color.parseColor("#FB4D7A")); //Female color                            
```

#### Other

Getting current checked state:
```java
switch (genderToggle.getChecked()) {
            case MALE:
                //Male checked
                break;
            case FEMALE:
                //Female checked
                break;
                
        }
```
Or
```java
genderToggle.isChecked() //True = female, False = male
```
#### Callback
To listen for the check changed events use:
```java
genderToggle.setCheckedChangeListener(this);

public interface GenderCheckedChangeListener {
        void onCheckChanged(Checked current);
    }

enum Checked { MALE, FEMALE }
```

#### Hardware Acceleration
This will force hardware acceleration for the drawing animations. This is useful on slower devices, but glow will not work with this enabled.

```java
genderToggle.useHardwareAcceleration();
```

## License
```
Copyright 2018 jtv7

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
