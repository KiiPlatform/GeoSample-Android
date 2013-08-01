package com.kii.sample.kiigeocoupon;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ViewCouponsFragment extends Fragment {
	private static final String TAG = "ViewCouponsFragment";
	GridView couponGallery;
	List<Coupon> couponList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.view_coupons_fragment,
				container, false);
		couponGallery = (GridView) rootView.findViewById(R.id.coupon_gallery);
		couponGallery.setAdapter(new CouponImageAdapter(getActivity()));
		// createGallery();
		LayoutAnimationController controller = AnimationUtils
				.loadLayoutAnimation(getActivity().getApplicationContext(),
						R.anim.gallery);

		couponGallery.setLayoutAnimation(controller);
		couponGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				showRedeemDialog(KiiData.getCouponList().get(position));
			}
		});
		return rootView;
	}

	private void createGallery() {
		if (KiiData.getCouponList() != null) {
			for (final Coupon coupon : KiiData.getCouponList()) {
				Log.e(TAG, "adding View");
				// View view=insertCoupon(coupon);
				View view = ((GeoSampleAndroidApp) getActivity())
						.createCouponView(coupon);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showRedeemDialog(coupon);
					}

				});
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				lp.rightMargin = R.dimen.padding;

				couponGallery.addView(view);
			}
		}
	}

	protected void showRedeemDialog(Coupon coupon) {
		android.app.DialogFragment redeemDialog = RedeemDialog.newInstance();
		((RedeemDialog) redeemDialog).setCoupon(coupon);
		redeemDialog.setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Holo_Light_Dialog);
		// redeemDialog.getDialog().getWindow().setWindowAnimations(R.style.MyAnimation_Window);
		// redeemDialog.getDialog().getWindow().getAttributes().windowAnimations
		// = R.style.MyAnimation_Window;

		redeemDialog.show(getFragmentManager(), "redeemDialog");
	}

	public class CouponImageAdapter extends BaseAdapter {
		Context context;

		public CouponImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			/* Set the number of element we want on the grid */
			return KiiData.getCouponList().size();
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View myView = convertView;
			Coupon coupon = KiiData.getCouponList().get(position);
			if (convertView == null) {
				/* we define the view that will display on the grid */

				// Inflate the layout
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				myView = inflater.inflate(R.layout.grid_item, null);

				// Add The Text!!!
				TextView company = (TextView) myView.findViewById(R.id.company);
				company.setText(coupon.getCompany());
				TextView product = (TextView) myView.findViewById(R.id.product);
				product.setText(coupon.getProduct());
				TextView discountCode = (TextView) myView
						.findViewById(R.id.discountCode);
				discountCode.setText(coupon.getDiscountCode());

				// Add The Image!!!
				BitmapDrawable background=new BitmapDrawable(coupon.getCouponImage());
				background.setAlpha(64);
				myView.setBackground(background);
//				ImageView iv = (ImageView) myView
//						.findViewById(R.id.grid_item_image);
//				iv.setImageBitmap(coupon.getCouponImage());
			}

			return myView;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
