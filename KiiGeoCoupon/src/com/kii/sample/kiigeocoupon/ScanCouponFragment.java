package com.kii.sample.kiigeocoupon;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanCouponFragment extends Fragment {

	private static final String TAG = "ScanCouponFragment";
	private ImageButton _scanButton;
	private FrameLayout _scannedCoupon;
	private Coupon _currentCoupon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView rootView = (ScrollView) inflater.inflate(
				R.layout.scan_coupon_fragment, container, false);
		_scanButton = (ImageButton) rootView.findViewById(R.id.scan_button);
		_scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startScan();
				// capturePicture();
			}
		});
		_scannedCoupon = (FrameLayout) rootView
				.findViewById(R.id.scanned_coupon);
		if (_currentCoupon != null)
			((GeoSampleAndroidApp) getActivity())
					.createCouponView(_currentCoupon);

		return rootView;
	}
	

	public void enableScan() {
		_scanButton.setVisibility(View.VISIBLE);
	}

	protected void startScan() {
		IntentIntegrator integrator = new IntentIntegrator(getActivity());
		integrator.initiateScan();
	}

	public void setScanResult(IntentResult scanResult) {
		Location location = ((GeoSampleAndroidApp) getActivity())
				.getCurrentLocation();
		_currentCoupon = Coupon.create(scanResult, location);
		if (_currentCoupon != null) {
			View couponView = ((GeoSampleAndroidApp) getActivity())
					.createCouponView(_currentCoupon);
			_scannedCoupon.addView(couponView);
		}
	}

}
