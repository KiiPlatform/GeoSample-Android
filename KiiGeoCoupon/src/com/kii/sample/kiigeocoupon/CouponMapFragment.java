package com.kii.sample.kiigeocoupon;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.kii.cloud.storage.GeoPoint;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.query.KiiClause;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

public class CouponMapFragment extends Fragment {

	private static final String TAG = "MapFragment";
	private GoogleMap _map;
	private View _rootView;
	private MapView _mapView;
	private List<Coupon> _couponList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		_rootView = inflater.inflate(R.layout.coupon_map_fragment, container,
				false);
		_mapView = (MapView) _rootView.findViewById(R.id.mapview);
		_mapView.onCreate(savedInstanceState);
		checkIfGooglePlayAvailable();
		setUpMapIfNeeded();
		if (_couponList != null)
			drawCouponMarks();
		return _rootView;
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (_map == null) {
			_map = _mapView.getMap();
			// Check if we were successful in obtaining the map.
			if (_map != null) {
				_map.clear();
				// The Map is verified. It is now safe to manipulate the map.
				_map.setMyLocationEnabled(true);
				_map.getUiSettings().setMyLocationButtonEnabled(false);

				try {
					MapsInitializer.initialize(this.getActivity());
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
				_map.setOnCameraChangeListener(new OnCameraChangeListener() {

					@Override
					public void onCameraChange(CameraPosition position) {
						loadGeoCoupons(position.target.latitude,
								position.target.longitude);
					}
				});

				// LocationManager locationManager = (LocationManager)
				// getActivity()
				// .getSystemService(Activity.LOCATION_SERVICE);
				// // Creating a criteria object to retrieve provider
				// Criteria criteria = new Criteria();
				//
				// // Getting the name of the best provider
				// String provider = locationManager.getBestProvider(criteria,
				// false);
				//
				// // Getting Current Location
				// Location location = locationManager
				// .getLastKnownLocation(provider);
				Location location = ((GeoSampleAndroidApp) getActivity())
						.getCurrentLocation();
				moveCamera(location);

				if (location != null) {
					// PLACE THE INITIAL MARKER
					drawMarker(
							new LatLng(location.getLatitude(),
									location.getLongitude()),
							BitmapDescriptorFactory.HUE_AZURE);
				}

			}
		}
	}

	protected void moveCamera(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				15);
		CameraPosition camPosition = _map.getCameraPosition();
		if (!((Math.floor(camPosition.target.latitude * 100) / 100) == (Math
				.floor(latLng.latitude * 100) / 100) && (Math
				.floor(camPosition.target.longitude * 100) / 100) == (Math
				.floor(latLng.longitude * 100) / 100))) {
			_map.getUiSettings().setScrollGesturesEnabled(false);
			_map.animateCamera(cameraUpdate, new CancelableCallback() {

				@Override
				public void onFinish() {
					_map.getUiSettings().setScrollGesturesEnabled(true);

				}

				@Override
				public void onCancel() {
					_map.getUiSettings().setAllGesturesEnabled(true);

				}
			});
		}
	}

	private void drawMarker(LatLng currentPosition, float hue) {
		_map.addMarker(
				new MarkerOptions().position(currentPosition).snippet(
						"Lat:" + currentPosition.latitude + "Lng:"
								+ currentPosition.longitude)).setIcon(
				BitmapDescriptorFactory.defaultMarker(hue));
	}

	private void checkIfGooglePlayAvailable() {
		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity().getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
					getActivity(), requestCode);
			dialog.show();

		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		setUpMapIfNeeded();
		_mapView.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onResume() {
		setUpMapIfNeeded();
		_mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		_mapView.onLowMemory();
	}

	public void loadGeoCoupons(double latitude, double longitude) {
		if (KiiData.getUser() == null)
			return;
		try {
			VisibleRegion vr = _map.getProjection().getVisibleRegion();
			GeoPoint sw = new GeoPoint(vr.latLngBounds.southwest.latitude,
					vr.latLngBounds.southwest.longitude);
			GeoPoint ne = new GeoPoint(vr.latLngBounds.northeast.latitude,
					vr.latLngBounds.northeast.longitude);
			GeoPoint currentLocation = new GeoPoint(latitude, longitude);
			KiiClause clause1 = KiiClause.geoBox("viewAt", ne, sw);
			String calculatedDistance = "distanceFromCurrentLoc";
			KiiClause clause2 = KiiClause.geoDistance("redeemedAt",
					currentLocation, 100, calculatedDistance);
			KiiQuery query = new KiiQuery(KiiClause.or(clause1, clause2));
			;

			KiiData.getUser().bucket(GeoSampleAndroidApp.USER_BUCKET)
					.query(new KiiQueryCallBack<KiiObject>() {

						@Override
						public void onQueryCompleted(int token,
								KiiQueryResult<KiiObject> result,
								Exception exception) {
							super.onQueryCompleted(token, result, exception);
							if (exception == null) {
								List<KiiObject> objects = result.getResult();
								Log.e(TAG, "objects=" + objects);
								_couponList = new ArrayList<Coupon>();
								for (KiiObject kiiObject : objects) {
									_couponList.add(new Coupon(kiiObject));
								}
								drawCouponMarks();
							} else
								exception.printStackTrace();
						}

					}, query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void drawCouponMarks() {
		LatLng location;
		float hue;
		GeoPoint point;
		if (_couponList != null)
			for (Coupon coupon : _couponList) {
				if (coupon.getRedeemAt() == null) {
					point = coupon.getViewAt();
					hue = BitmapDescriptorFactory.HUE_GREEN;
				} else {
					point = coupon.getRedeemAt();
					hue = BitmapDescriptorFactory.HUE_RED;
				}
				location = new LatLng(point.getLatitude(), point.getLongitude());
				Log.e(TAG, "point:" + point);
				drawMarker(location, hue);
			}

	}

	public void notifyLocationChanged(Location location) {
		// redraw the marker when get location update.
		if (_map != null) {
			drawMarker(
					new LatLng(location.getLatitude(), location.getLongitude()),
					BitmapDescriptorFactory.HUE_AZURE);
			drawCouponMarks();
			moveCamera(location);
		}
	}
}
