package com.kii.sample.kiigeocoupon;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

public class KiiData  {

	protected static final String TAG = "KiiData";
	/**
	 * 
	 */
	private static KiiUser user;
	private static List<Coupon> couponList;

	public static KiiUser getUser() {
		return user;
	}

	public static void setUser(KiiUser user) {
		KiiData.user = user;
		loadCoupons();
	}

	public static List<Coupon> getCouponList() {
		return couponList;
	}

	public static void setCouponList(List<Coupon> couponList) {
		KiiData.couponList = couponList;
	}

	public static void loadCoupons() {
		try {
			KiiQuery qb = new KiiQuery(KiiClause.equals(Coupon.IS_REDEEMED, false)); 
			qb.sortByAsc(Coupon.COMPANY);
			qb.sortByAsc(Coupon.PRODUCT);
			user.bucket(KiiGeoCouponApp.USER_BUCKET)
					.query(new KiiQueryCallBack<KiiObject>() {

						@Override
						public void onQueryCompleted(int token,
								KiiQueryResult<KiiObject> result,
								Exception exception) {
							super.onQueryCompleted(token, result, exception);
							if (exception == null) {
								List<KiiObject> objects = result.getResult();
								Log.e(TAG, "objects=" + objects);
								couponList = new ArrayList<Coupon>();
								for (KiiObject kiiObject : objects) {
									if(!kiiObject.has(Coupon.REDEEM_AT))
									couponList.add(new Coupon(kiiObject));
								}
								
							} else
								exception.printStackTrace();
						}

					}, qb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
