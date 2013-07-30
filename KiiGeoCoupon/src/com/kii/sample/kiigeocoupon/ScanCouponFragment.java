package com.kii.sample.kiigeocoupon;

import android.app.Fragment;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
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
	private CameraPreview cameraPreview;
	private PictureCallback rawCallback;
	private ShutterCallback shutterCallback;
	private PictureCallback jpegCallback;

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
			((KiiGeoCouponApp)getActivity()).createCouponView(currentCoupon);
//		cameraPreview = new CameraPreview(
//				getActivity().getApplicationContext(), getCameraInstance());
	//	rootView.addView(cameraPreview);
//		rawCallback = new PictureCallback() {
//			public void onPictureTaken(byte[] data, Camera camera) {
//				Log.d("Log", "onPictureTaken - raw");
//			}
//		};
//
//		/** Handles data for jpeg picture */
//		shutterCallback = new ShutterCallback() {
//			public void onShutter() {
//				Log.i("Log", "onShutter'd");
//			}
//		};
//		jpegCallback = new PictureCallback() {
//			public void onPictureTaken(byte[] data, Camera camera) {
//				FileOutputStream outStream = null;
//				try {
//					outStream = new FileOutputStream(String.format(
//							"/sdcard/%d.jpg", System.currentTimeMillis()));
//					outStream.write(data);
//					outStream.close();
//					Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//				}
//				Log.d("Log", "onPictureTaken - jpeg");
//			}
//		};

		return rootView;
	}

//	protected void capturePicture() {
//		getCameraInstance().takePicture(shutterCallback, rawCallback,
//				jpegCallback);
//	}
//
//	private void decodeQRCode(byte[] data) {
//		Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
//		int[] pixels=null;
//		bMap.getPixels(pixels, 0, 640,0, 0, 640, 480);
//		LuminanceSource source = new RGBLuminanceSource(640,480,pixels);
//		
//		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//		Reader reader = new MultiFormatReader();
//		try {
//			Result result = reader.decode(bitmap);
//			// result.getText();
//			byte[] rawBytes = result.getRawBytes();
//			BarcodeFormat format = result.getBarcodeFormat();
//			ResultPoint[] points = result.getResultPoints();
//		} catch (NotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ChecksumException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		}
//	}

	public void enableScan() {
		scanButton.setVisibility(View.VISIBLE);
	}

	protected void startScan() {
		IntentIntegrator integrator = new IntentIntegrator(getActivity());
		integrator.initiateScan();
	}

	public void setScanResult(IntentResult scanResult) {
		Location location=((KiiGeoCouponApp)getActivity()).getCurrentLocation();
		currentCoupon = Coupon.create(scanResult, location);
		View couponView=((KiiGeoCouponApp)getActivity()).createCouponView(currentCoupon);
		scannedCoupon.addView(couponView);

//		createCouponView(currentCoupon);
	}

//	private void createCouponView(Coupon coupon) {
//		Bitmap bm = coupon.getCouponImage();
//		LayoutInflater inflater = (LayoutInflater) getActivity()
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//		View couponView = inflater.inflate(R.layout.coupon_view, null, false);
//
//		ImageView imageView = (ImageView) couponView
//				.findViewById(R.id.couponImage);
//		imageView.setLayoutParams(new LayoutParams(220, 220));
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		imageView.setImageBitmap(bm);
//
//		TextView company = (TextView) couponView.findViewById(R.id.company);
//		company.setText(coupon.getCompany());
//		TextView product = (TextView) couponView.findViewById(R.id.product);
//		product.setText(coupon.getProduct());
//		TextView discountCode = (TextView) couponView
//				.findViewById(R.id.discountCode);
//		discountCode.setText(coupon.getDiscountCode());
//
//		// layout.addView(couponView);
//		scannedCoupon.addView(couponView);
//		return;
//	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

}
