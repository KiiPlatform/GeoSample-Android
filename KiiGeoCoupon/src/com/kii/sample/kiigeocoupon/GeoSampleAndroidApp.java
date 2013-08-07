package com.kii.sample.kiigeocoupon;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.tool.login.KiiLoginFragment;

public class GeoSampleAndroidApp extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String APP_ID = "YOUR KII APP ID";
	private static final String APP_KEY = "YOUR KII APP KEY";
	public static final String PREFS_NAME = "KiiGeoCouponPrefs";
	public static final String USER_EMAIL = "email";
	public static final String USER_PASSWORD = "password";
	public static final String USER_BUCKET = "Coupons";
	private static final String TAG = "GeoSampleAndroidApp";
	private static final String TAB_SELECTED = "selected_tab";
	private static final String KII_USER_URI = "kiiuseruri";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final long UPDATE_INTERVAL = 1000 * 5;
	private static final long FASTEST_INTERVAL = 1000 * 1;

	private ScanCouponFragment _couponFragment;
	private ViewCouponsFragment _redeemFragment;
	private CouponMapFragment _mapFragment;
	private LocationClient _locationClient;
	private LocationRequest _locationRequest;
	private Location _cachedLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kii_geo_coupon_app);
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		Kii.initialize(APP_ID, APP_KEY, Kii.Site.US);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab tab1 = actionBar.newTab().setText(R.string.scan_coupons);
		ActionBar.Tab tab2 = actionBar.newTab().setText(R.string.view_coupons);
		ActionBar.Tab tab3 = actionBar.newTab().setText(R.string.map);
		_couponFragment = new ScanCouponFragment();
		_redeemFragment = new ViewCouponsFragment();
		_mapFragment = new CouponMapFragment();

		tab1.setTabListener(new MyTabsListener(_couponFragment));
		tab2.setTabListener(new MyTabsListener(_redeemFragment));
		tab3.setTabListener(new MyTabsListener(_mapFragment));
		actionBar.addTab(tab1);
		actionBar.addTab(tab2);
		actionBar.addTab(tab3);
		actionBar.setIcon(R.drawable.kiilogo);
		_locationRequest = LocationRequest.create();
		// Use high accuracy
		_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		_locationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		_locationRequest.setFastestInterval(FASTEST_INTERVAL);
		_locationClient = new LocationClient(this, this, this);

		// if(KiiUser.getCurrentUser()==null) showLoginDialog();
		if (KiiData.getUser() == null)
			showLoginDialog();
	}

	private void showLoginDialog() {
		android.app.DialogFragment loginFragment = GeoLoginFragment
				.newInstance();
		loginFragment.setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Holo_Light_Dialog);
		((KiiLoginFragment) loginFragment).setType(KiiLoginFragment.Type.EMAIL);
		loginFragment.show(getFragmentManager(), "loginDialog");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		if (KiiData.getUser() != null)
			savedInstanceState.putString(KII_USER_URI, KiiData.getUser()
					.toUri().toString());
		savedInstanceState.putInt(TAB_SELECTED, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String uri = savedInstanceState.getString(KII_USER_URI, null);

		if (uri != null) {
			KiiData.setUser(KiiUser.createByUri(Uri.parse(uri)));

		}
		int tab = savedInstanceState.getInt(TAB_SELECTED, 0);
		getActionBar().setSelectedNavigationItem(tab);
		Log.e(TAG, "kiiData:" + KiiData.getCouponList());
		Log.e(TAG, "rec user:" + KiiData.getUser());
		Log.e(TAG, "rec user:" + KiiUser.isLoggedIn());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_kii_geo_coupon_app);
		_locationClient.connect();
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		_locationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		if (_locationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			_locationClient.removeLocationUpdates(this);
		}
		_locationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kii_geo_coupon_app, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				_locationClient.connect();
				break;
			}
		}
		if (scanResult != null) {
			// handle scan result
			final SharedPreferences settings = getSharedPreferences(
					GeoSampleAndroidApp.PREFS_NAME, 0);
			try {
				KiiData.getUser().refresh(new KiiUserCallBack() {

					@Override
					public void onRefreshCompleted(int token,
							Exception exception) {
						super.onRefreshCompleted(token, exception);
						KiiUser.logIn(new KiiUserCallBack() {

							@Override
							public void onLoginCompleted(int token,
									KiiUser user, Exception exception) {
								super.onLoginCompleted(token, user, exception);
								_couponFragment.setScanResult(scanResult);
							}

						}, KiiData.getUser().getEmail(), settings.getString(
								GeoSampleAndroidApp.USER_PASSWORD, ""));
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "REFRESH!!!");
				e.printStackTrace();
			}
		}
	}

	public Location getCurrentLocation() {
		if (_locationClient.isConnected())
			_cachedLocation = _locationClient.getLastLocation();
		Log.e(TAG, "current location:" + _cachedLocation);
		return _cachedLocation;
	}

	public View createCouponView(Coupon coupon) {
		Bitmap bm = coupon.getCouponImage();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View couponView = inflater.inflate(R.layout.coupon_view, null, false);

		ImageView imageView = (ImageView) couponView
				.findViewById(R.id.couponImage);
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		TextView company = (TextView) couponView.findViewById(R.id.company);
		company.setText(coupon.getCompany());
		TextView product = (TextView) couponView.findViewById(R.id.product);
		product.setText(coupon.getProduct());
		TextView discountCode = (TextView) couponView
				.findViewById(R.id.discountCode);
		discountCode.setText(coupon.getDiscountCode());

		return couponView;
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected to Google Play Services",
				Toast.LENGTH_SHORT).show();
		_locationClient.requestLocationUpdates(_locationRequest, this);

	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this,
				"Disconnected from Google Play Services. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		_cachedLocation=location;
		_mapFragment.notifyLocationChanged(location);
	}

	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}

}
