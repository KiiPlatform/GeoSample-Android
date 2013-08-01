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
import android.widget.LinearLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanCouponFragment extends Fragment {

	private static final String TAG = "ScanCouponFragment";
	private ImageButton scanButton;
	private FrameLayout scannedCoupon;
	private Coupon currentCoupon;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.scan_coupon_fragment, container, false);
		scanButton = (ImageButton) rootView.findViewById(R.id.scan_button);
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 startScan();
				//capturePicture();
			}
		});
		scannedCoupon = (FrameLayout) rootView
				.findViewById(R.id.scanned_coupon);
		if (currentCoupon != null)
			((GeoSampleAndroidApp)getActivity()).createCouponView(currentCoupon);


		return rootView;
	}


	public void enableScan() {
		scanButton.setVisibility(View.VISIBLE);
	}

	protected void startScan() {
		IntentIntegrator integrator = new IntentIntegrator(getActivity());
		integrator.initiateScan();
	}

	public void setScanResult(IntentResult scanResult) {
		Location location=((GeoSampleAndroidApp)getActivity()).getCurrentLocation();
		currentCoupon = Coupon.create(scanResult, location);
		View couponView=((GeoSampleAndroidApp)getActivity()).createCouponView(currentCoupon);
		scannedCoupon.addView(couponView);
	}



}
