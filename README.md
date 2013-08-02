# GeoSample-Android
=================

Kii Geolocation demo for Android, using google maps v2.

This is a bare-bones sample application that shows you how to create a user account then allow us to scan QR codes that include the user current location. These QR codes can be store discount coupons that we can redeem later in a different location. Finally, we can see new and redeemed coupons in a MapView.  are stored within [Kii Cloud](http://developer.kii.com) and retrieved each time the user logs in. This is a sample application to show how you can easily create complex geo-queries built on top of KiiCloud. Coupons are available across devices, and this sample can easily be extended with further functionality, social layers, and better UX to become a full-fledged application.

This sample is built on top of [Kii Cloud](http://developer.kii.com) so you don't need to do any backend setup or coding, you can just plug in the easy-to-use Kii SDK and you're up and running!

###Screenshots

These are some screenshots of the latest build.

<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/login.png" alt="Login" width="25%"/>
<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/signup.png" alt="Sign up" width="25%"/>
<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/scan.png" alt="Scan Tab" width="25%"/>
<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/view.png" alt="View Tab" width="25%"/>
<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/redeem.png" alt="Redeem" width="25%"/>
<img src="https://raw.github.com/KiiPlatform/KiiGeoCoupon/develop/screenshots/map.png" alt="Map Tab" width="25%"/>


### KiiToolkit

 
This sample also utilizes the open-source [KiiToolkit](https://github.com/KiiPlatform/KiiToolkit-Android) library for even faster development. This provides a login view that plugs directly into Kii Cloud, as well as a signup view that allow us to easily create and login users asynchronously to the Kii Cloud!
Getting Started

### ZXing

This sample app uses [Zxing](http://code.google.com/p/zxing/), an open-source library for scanning and barcode image processing implemented in Java and ported to different languages.

### Google Maps v2

Finally, this sample app is using Google Maps v2 API to display a map with the coupon locations.


## How to build

Just simple checkout the source code and import it into Eclipse. There are some additional settings:

### Setup Google Maps v2

You need a Google Maps v2 API key. If you're not familiar with it, please read [Getting started guide](https://developers.google.com/maps/documentation/android/start)
Once you have you key ready, you only have to copy in the manifest file:

AndroidManifest.xml

	<meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="YOUR_GOOGLE_MAPS_KEY" />
            
### Setup KiiCloud

You can get it from [Kii Cloud](http://developer.kii.com).

Then copy the values to:           
src/com/kii / sample / kiigeocoupon / KiiGeoCouponApp.java


	private static final String APP_ID = "YOUR KII APP ID";
	private static final String APP_KEY = "YOUR KII APP KEY";
	
Your app is ready to use!!!

### Next Steps

This is a great way to get your new geolocation app up and running even faster with Kii Cloud, so have fun building, feel free to contribute and let us know what you think!