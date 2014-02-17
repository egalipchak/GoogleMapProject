package com.example.place_its;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.place_its.MainActivity;
import com.example.place_its.ShowReminderList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		OnMapClickListener, CancelableCallback {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 1;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	// Define an object that holds accuracy and frequency parameters
	LocationRequest mLocationRequest;
	boolean mUpdatesRequested;
	private SharedPreferences.Editor mEditor;
	private SharedPreferences mPrefs;
	private LocationClient mLocationClient;
	private GoogleMap mMap;
	private TextView mLatLong;
	private LatLng pos;

	private List<Marker> mMarkers = new ArrayList<Marker>();
	private Iterator<Marker> marker;
	Intent intentService;
	
	private Marker aMarker;
	private static final String LOGCAT = "MainActivity";
	private double userLat, userLog;
	private final double MIN_RADIUS_CIRCLE = 50;
	private final double MAX_RADIUS_CIRCLE = 5000;
	private int counter = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUpMapIfNeeded();
		mMap.setMyLocationEnabled(true);
		mMap.setOnMapClickListener(this);
		
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				Log.d( LOGCAT, "Here is my marker");
				aMarker = marker;
				Intent intent = new Intent( MainActivity.this, ShowReminderList.class );
				startActivity( intent );
				return false;			
			}
		});

		mLatLong = (TextView) findViewById(R.id.latLong);
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		// Open the shared preferences
		mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		// Start with updates turned on
		mUpdatesRequested = true;
		
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);

		Button btnReTrack = (Button) findViewById(R.id.retrack);

		btnReTrack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				marker = mMarkers.iterator();
				if (marker.hasNext()) {
					Marker current = marker.next();
					mMap.animateCamera(CameraUpdateFactory.newLatLng(current
							.getPosition()), 2000, MainActivity.this);
					current.showInfoWindow();
				}
			}
		});

		Button btnUpdate = (Button) findViewById(R.id.sendLocationBtn);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editText = (EditText) findViewById(R.id.sendLocation);
				String geoData = editText.getText().toString();
				String[] coordinate = geoData.split(",");
				double latitude = Double.valueOf(coordinate[0]).doubleValue();
				double longitude = Double.valueOf(coordinate[1]).doubleValue();
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(latitude, longitude), 12), 2000, null);
			}
		});
	}
	
	@Override
	public void onMapClick(LatLng position) {
		pos = position;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add a new Reminder");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				intentService = new Intent( MainActivity.this, ShowReminderList.class );
				//intentService.putExtra("hellyah", true);
				//setResult( RESULT_OK, intentService);
				startActivity( intentService );
			    Toast.makeText(MainActivity.this, "Tag added!", Toast.LENGTH_SHORT).show();    
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(MainActivity.this, "Nothing added!",
						Toast.LENGTH_SHORT).show();
					}
		});
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Save the current setting for updates
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if( ShowReminderList.isExist() ) {
	    	aMarker = mMap.addMarker(new MarkerOptions()
			.position(pos)
			.title( "Hello" ));
			//.snippet("You can put other info here!"));
	    	mMarkers.add(aMarker);
	    }
	    else {
	    	if( aMarker != null ) {
	    		aMarker.remove();
	    		mMarkers.remove(aMarker);
	    	}
	    }
		/*
		 * Get any previous setting for location updates Gets "false" if an
		 * error occurs
		 */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);
			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
			}
		}
	}

	public void notifyUser() {
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle("Am I Alive?")
			    .setContentText("YOU WOT M8!?!??!");
		
		// Sets an ID for the notification
		int mNotificationId = 001;
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}
	
	
	// Define the callback method that receives location updates
	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		userLat = location.getLatitude();
		userLog = location.getLongitude();
		mLatLong.setText("Lat: " + userLat + " Long: " + userLog);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				location.getLatitude(), location.getLongitude()), 18));
		marker = mMarkers.iterator();
		while (marker.hasNext()) {
			Marker current = marker.next();
			if(calDistance( userLat, userLog, current ) < MIN_RADIUS_CIRCLE ) {
				counter++;
				if( counter == 1 ) {
					Intent i = new Intent( MainActivity.this, StatusBar.class );
					startActivity( i );
				
					notifyUser();
					
					
					
				} else 
					counter = 2;
			} else if( calDistance( userLat, userLog, current ) > MAX_RADIUS_CIRCLE ) {
				counter = 0;
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			Toast.makeText(this, "FAILURE!", Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		// If already requested, start periodic updates
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish() {
		if (marker.hasNext()) {
			Marker current = marker.next();
			mMap.animateCamera(
					CameraUpdateFactory.newLatLng(current.getPosition()), 2000,
					this);
			current.showInfoWindow();
		}
	}

	@Override
	public void onCancel() {
	}
	
	
	public double calDistance( double x, double y, Marker whatMarker ) {
		LatLng position = whatMarker.getPosition();
		double lat = position.latitude;
		double log = position.longitude;
		double x1 = lat - x;
		double y1 = log - y;
		double result = Math.sqrt( x1*x1 + y1*y1); 
		return result;
	}

}
