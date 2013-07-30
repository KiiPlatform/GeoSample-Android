package com.kii.sample.kiigeocoupon;

import java.util.List;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ViewCouponsFragment extends Fragment {
	private static final String TAG = "ViewCouponsFragment";
	LinearLayout couponGallery;
	List<Coupon> couponList;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.view_coupons_fragment, container,
				false);
		couponGallery = (LinearLayout) rootView
				.findViewById(R.id.coupon_gallery);
//		loadCoupons();
		createGallery();
		LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity().getApplicationContext(), R.anim.gallery);

		couponGallery.setLayoutAnimation(controller);
		return rootView;
	}

//	private void loadCoupons() {
//		try {
//			KiiQuery qb = new KiiQuery();
//			KiiGeoCouponApp.kiiData.getUser().bucket(KiiGeoCouponApp.USER_BUCKET)
//					.query(new KiiQueryCallBack<KiiObject>() {
//
//						@Override
//						public void onQueryCompleted(int token,
//								KiiQueryResult<KiiObject> result,
//								Exception exception) {
//							super.onQueryCompleted(token, result, exception);
//							if (exception == null) {
//								List<KiiObject> objects = result.getResult();
//								Log.e(TAG, "objects=" + objects);
//								couponList = new ArrayList<Coupon>();
//								for (KiiObject kiiObject : objects) {
//									couponList.add(new Coupon(kiiObject));
//								}
//								createGallery();
//								//KiiGeoCouponApp.kiiData.setCouponList(couponList);
//							} else
//								exception.printStackTrace();
//						}
//
//					}, qb);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void createGallery() {
		if (KiiData.getCouponList() != null) {
			for (final Coupon coupon : KiiData.getCouponList()) {
				Log.e(TAG,"adding View");
			//	View view=insertCoupon(coupon);
				View view=((KiiGeoCouponApp)getActivity()).createCouponView(coupon);
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						showRedeemDialog(coupon);
					}

				});
				LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				lp.rightMargin=R.dimen.padding;

				couponGallery.addView(view);
			}
		}
	}

	protected void showRedeemDialog(Coupon coupon) {
		android.app.DialogFragment redeemDialog = RedeemDialog
				.newInstance();
		((RedeemDialog)redeemDialog).setCoupon(coupon);
		redeemDialog.setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Holo_Light_Dialog);
	//	redeemDialog.getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);
	//	redeemDialog.getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
	    
		redeemDialog.show(getFragmentManager(), "redeemDialog");
	}

//	private View insertCoupon(Coupon coupon) {
//		Bitmap bm = coupon.getCouponImage();
//		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService
//			      (Context.LAYOUT_INFLATER_SERVICE);
//
//		View couponView =inflater.inflate(R.layout.coupon_view, null,false);
//	//	couponView.setLayoutParams(new LayoutParams(250, 250));
//
//		ImageView imageView =(ImageView)couponView.findViewById(R.id.couponImage);
//		imageView.setLayoutParams(new LayoutParams(220, 220));
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		imageView.setImageBitmap(bm);
//		TextView company=(TextView)couponView.findViewById(R.id.company);
//		company.setText(coupon.getCompany());
//		TextView product=(TextView)couponView.findViewById(R.id.product);
//		product.setText(coupon.getProduct());
//		TextView discountCode=(TextView)couponView.findViewById(R.id.discountCode);
//		discountCode.setText(coupon.getDiscountCode());
//		
//		//layout.addView(couponView);
//		return couponView;
//	}

}
