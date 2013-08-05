package com.kii.sample.kiigeocoupon;

import android.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class RedeemDialog extends DialogFragment{
	
	private static final String TAG = "DialogFragment";
	private View _rootView;
	private FrameLayout _currentRedeem;
	private Coupon _coupon;
	private Button _yesButton;
	private Button _noButton;
	
	public static RedeemDialog newInstance() {
		RedeemDialog fragment = new RedeemDialog();
        return fragment;
    }


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// link our variables to UI elements
		_rootView = inflater.inflate(R.layout.redeem_dialog, container, false);
		_currentRedeem=(FrameLayout)_rootView.findViewById(R.id.current_redeem);
		_yesButton=(Button)_rootView.findViewById(R.id.yes);
		_noButton=(Button)_rootView.findViewById(R.id.no);
		_yesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				redeemCoupon();
			}
		});
		_noButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		createCouponView(_coupon);
		getDialog().setTitle(R.string.redeem_coupon);
		getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);

		return _rootView;
	}
	
	protected void redeemCoupon() {
		Location location=((GeoSampleAndroidApp)getActivity()).getCurrentLocation();
		_coupon.redeem(location);
		KiiData.loadCoupons();
		dismiss();
	}


	public void setCoupon(Coupon coupon){
		this._coupon=coupon;
	}
	
	private void createCouponView(Coupon coupon) {
		View couponView=((GeoSampleAndroidApp)getActivity()).createCouponView(coupon);
		_currentRedeem.addView(couponView);
		return;
	}	


}
