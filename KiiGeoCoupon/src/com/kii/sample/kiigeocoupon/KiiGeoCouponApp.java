package com.kii.sample.kiigeocoupon;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.tool.login.KiiLoginFragment;

public class KiiGeoCouponApp extends Activity {

	private static final String APP_ID = "YOUR KII APP ID";
	private static final String APP_KEY = "YOUR KII APP KEY";
	public static final String PREFS_NAME = "KiiGeoCouponPrefs";
	public static final String USER_EMAIL = "email";
	public static final String USER_PASSWORD = "password";
	public static final String USER_BUCKET = "Coupons";
	private static final String TAG = "KiiGeoCouponApp";
	private static final String TAB_SELECTED = "selected_tab";
	private static final String KII_USER_URI = "kiiuseruri";
	private ScanCouponFragment couponFragment;
	private ViewCouponsFragment redeemFragment;
	private CouponMapFragment mapFragment;

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
		couponFragment = new ScanCouponFragment();
		redeemFragment = new ViewCouponsFragment();
		mapFragment = new CouponMapFragment();

		tab1.setTabListener(new MyTabsListener(couponFragment));
		tab2.setTabListener(new MyTabsListener(redeemFragment));
		tab3.setTabListener(new MyTabsListener(mapFragment));
		actionBar.addTab(tab1);
		actionBar.addTab(tab2);
		actionBar.addTab(tab3);
		actionBar.setIcon(R.drawable.kiilogo);
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
		savedInstanceState.putString(KII_USER_URI, KiiData.getUser().toUri()
				.toString());
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
		if (scanResult != null) {
			// handle scan result
			final SharedPreferences settings = getSharedPreferences(
					KiiGeoCouponApp.PREFS_NAME, 0);
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
								couponFragment.setScanResult(scanResult);
							}

						}, KiiData.getUser().getEmail(), settings.getString(
								KiiGeoCouponApp.USER_PASSWORD, ""));
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "REFRESH!!!");
				e.printStackTrace();
			}
		}
	}

	public Location getCurrentLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);
		return location;
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
