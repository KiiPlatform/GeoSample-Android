package com.kii.sample.kiigeocoupon;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kii.cloud.storage.GeoPoint;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiObjectCallBack;

public class Coupon {

	/**
	 * 
	 */
	private static final String COMPANY = "company";
	private static final String PRODUCT = "product";
	private static final String DISCOUNT_CODE = "discount_code";
	private static final String VIEW_AT = "viewAt";
	private static final String REDEEM_AT = "redeemedAt";
	private static final String RAW_DATA = "rawData"; // cached to increased
														// performance

	private static final String TAG = "Coupon";

	private KiiObject kiiObject;

	public Coupon(KiiObject object) {
		kiiObject = object;
	}

	public static Coupon create(IntentResult scanResult, Location location) {
		KiiObject kiiObject = null;
		try {
			Log.e(TAG, "login:" + KiiUser.isLoggedIn());
			kiiObject = KiiData.getUser().bucket(KiiGeoCouponApp.USER_BUCKET)
					.object();
		} catch (Exception e) {
			Log.e(TAG, "Can not create bucket");
			e.printStackTrace();
		}
		kiiObject.set(RAW_DATA, scanResult.getRawBytes());
		StringTokenizer st = new StringTokenizer(scanResult.getContents(), ",");
		if (st.countTokens() >= 3) {
			kiiObject.set(COMPANY, st.nextToken());
			kiiObject.set(PRODUCT, st.nextToken());
			kiiObject.set(DISCOUNT_CODE, st.nextToken());
		}
		kiiObject.set(VIEW_AT,
				new GeoPoint(location.getLatitude(), location.getLongitude()));
		kiiObject.save(new KiiObjectCallBack() {

			@Override
			public void onSaveCompleted(int token, KiiObject object,
					Exception exception) {
				super.onSaveCompleted(token, object, exception);
				if (exception == null)
					Log.e(TAG, "Coupon saved");
				else
					exception.printStackTrace();
			}

		});
		Coupon coupon = new Coupon(kiiObject);
		return coupon;
	}

	public String getCompany() {
		return kiiObject.getString(COMPANY, "");
	}

	public String getProduct() {
		return kiiObject.getString(PRODUCT, "");
	}

	public String getDiscountCode() {
		return kiiObject.getString(DISCOUNT_CODE, "");
	}

	public GeoPoint getViewAt() {
		return kiiObject.getGeoPoint(VIEW_AT, null);
	}

	public GeoPoint getRedeemAt() {
		return kiiObject.getGeoPoint(REDEEM_AT, null);
	}

	public void redeem(Location location) {
		kiiObject.set(REDEEM_AT,
				new GeoPoint(location.getLatitude(), location.getLongitude()));
		kiiObject.save(new KiiObjectCallBack() {

			@Override
			public void onSaveCompleted(int token, KiiObject object,
					Exception exception) {
				super.onSaveCompleted(token, object, exception);
				if (exception == null)
					Log.e(TAG, "Coupon redeemed");
				else
					exception.printStackTrace();
			}

		});
	}

	public Bitmap getCouponImage() {
		// get a byte matrix for the data
		BitMatrix matrix;
		com.google.zxing.Writer writer = new QRCodeWriter();
		String data;
		try {
			data = new String(kiiObject.getByteArray(RAW_DATA), "UTF8");
		} catch (UnsupportedEncodingException e) {
			// the program shouldn't be able to get here
			return null;
		}
		try {

			matrix = writer.encode(data,
					com.google.zxing.BarcodeFormat.QR_CODE, 230, 230);
		} catch (com.google.zxing.WriterException e) {
			return null;
		}

		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				// pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000
				// : 0xFFFFFFFF;
				pixels[offset + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
