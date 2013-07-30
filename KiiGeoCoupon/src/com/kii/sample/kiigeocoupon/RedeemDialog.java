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
	private View rootView;
	private FrameLayout currentRedeem;
	private Coupon coupon;
	private Button yesButton;
	private Button noButton;
	
	public static RedeemDialog newInstance() {
		RedeemDialog fragment = new RedeemDialog();
        return fragment;
    }


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// link our variables to UI elements
		rootView = inflater.inflate(R.layout.redeem_dialog, container, false);
		currentRedeem=(FrameLayout)rootView.findViewById(R.id.current_redeem);
		yesButton=(Button)rootView.findViewById(R.id.yes);
		noButton=(Button)rootView.findViewById(R.id.no);
		yesButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				redeemCoupon();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		createCouponView(coupon);
		getDialog().setTitle(R.string.redeem_coupon);
		getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);

		return rootView;
	}
	
	protected void redeemCoupon() {
		Location location=((KiiGeoCouponApp)getActivity()).getCurrentLocation();
		coupon.redeem(location);
		KiiData.loadCoupons();
		dismiss();
	}


	public void setCoupon(Coupon coupon){
		this.coupon=coupon;
	}
	
	private void createCouponView(Coupon coupon) {
//		Bitmap bm = coupon.getCouponImage();
//		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
//			      (Context.LAYOUT_INFLATER_SERVICE);
//
//		View couponView =inflater.inflate(R.layout.coupon_view, null,false);
//
//		ImageView imageView =(ImageView)couponView.findViewById(R.id.couponImage);
//		imageView.setLayoutParams(new LayoutParams(220, 220));
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		imageView.setImageBitmap(bm);
//		Log.e(TAG,"company="+coupon.getCompany());
//		
//		TextView company=(TextView)couponView.findViewById(R.id.company);
//		company.setText(coupon.getCompany());
//		TextView product=(TextView)couponView.findViewById(R.id.product);
//		product.setText(coupon.getProduct());
//		TextView discountCode=(TextView)couponView.findViewById(R.id.discountCode);
//		discountCode.setText(coupon.getDiscountCode());
		
		View couponView=((KiiGeoCouponApp)getActivity()).createCouponView(coupon);
		currentRedeem.addView(couponView);
		return;
	}	


}
