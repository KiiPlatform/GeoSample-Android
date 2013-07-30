package com.kii.sample.kiigeocoupon;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
	private GoogleMap mMap;
	private View rootView;
	private MapView mapView;
	private List<Coupon> couponList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.coupon_map_fragment, container,
				false);
		mapView = (MapView) rootView.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);
		checkIfGooglePlayAvailable();
		setUpMapIfNeeded();
		if (couponList != null)
			drawCouponMarks();
		return rootView;
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = mapView.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.clear();
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setMyLocationEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(false);

				try {
					MapsInitializer.initialize(this.getActivity());
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
				mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

					@Override
					public void onCameraChange(CameraPosition position) {
						loadGeoCoupons(position.target.latitude,
								position.target.longitude);
					}
				});

				LocationManager locationManager = (LocationManager) getActivity()
						.getSystemService(Activity.LOCATION_SERVICE);
				// Creating a criteria object to retrieve provider
				Criteria criteria = new Criteria();

				// Getting the name of the best provider
				String provider = locationManager.getBestProvider(criteria,
						true);

				// Getting Current Location
				Location location = locationManager
						.getLastKnownLocation(provider);
				moveCamera(location);
				LocationListener locationListener = new LocationListener() {
					public void onLocationChanged(Location location) {
						// redraw the marker when get location update.
						drawMarker(
								new LatLng(location.getLatitude(),
										location.getLongitude()),
								BitmapDescriptorFactory.HUE_AZURE);
						drawCouponMarks();
						moveCamera(location);
					}

					@Override
					public void onProviderDisabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProviderEnabled(String arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						// TODO Auto-generated method stub

					}
				};
				if (location != null) {
					// PLACE THE INITIAL MARKER
					drawMarker(
							new LatLng(location.getLatitude(),
									location.getLongitude()),
							BitmapDescriptorFactory.HUE_AZURE);
				}
				locationManager.requestLocationUpdates(provider, 20000, 0,
						locationListener);

			}
		}
	}

	protected void moveCamera(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				15);
		mMap.animateCamera(cameraUpdate);
	}

	private void drawMarker(LatLng currentPosition, float hue) {
		mMap.addMarker(
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
		mapView.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onResume() {
		setUpMapIfNeeded();
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	public void loadGeoCoupons(double latitude, double longitude) {
		try {
			VisibleRegion vr = mMap.getProjection().getVisibleRegion();
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

			KiiData.getUser().bucket(KiiGeoCouponApp.USER_BUCKET)
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
									couponList.add(new Coupon(kiiObject));
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
		for (Coupon coupon : couponList) {
			if (coupon.getRedeemAt() == null) {
				point = coupon.getViewAt();
				hue = BitmapDescriptorFactory.HUE_GREEN;
			} else {
				point = coupon.getRedeemAt();
				hue = BitmapDescriptorFactory.HUE_RED;
			}
			location = new LatLng(point.getLatitude(), point.getLongitude());
			drawMarker(location, hue);
		}

	}
}
