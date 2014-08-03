package com.example.pickupv1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.pickupv1.MainActivity.connectTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MapTouchActivity extends FragmentActivity implements OnMyLocationChangeListener {
	 GoogleMap googleMap;
	 private ArrayList<String> arrayList;
	 private TCPClient mTcpClient;
	 String message = null;
	 volatile static List<PickUpDriver> activeDrivers;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_touch);
		
		 arrayList = new ArrayList<String>();
		 activeDrivers = new LinkedList<PickUpDriver>();
		 // connect to the server
	        new connectTask().execute("");

		 try {
	            // Loading map
	            initilizeMap();
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		 
		 String message = getIntent().getExtras().getString("Message");
		 String info[] = message.split(";");
		 String driverName = info[1];
		 double driverLat = Double.parseDouble(info[2]);
		 double driverLong = Double.parseDouble(info[3]);
		 
		 LatLng latLng = new LatLng(driverLat, driverLong);
		 MarkerOptions markerOptions = new MarkerOptions();
		 
         // Setting the position for the marker
         markerOptions.position(latLng);

         // Setting the title for the marker.
         // This will be displayed on taping the marker
         markerOptions.title(latLng.latitude + " : " + latLng.longitude);

         // Clears the previously touched position
         googleMap.clear();

         // Animating to the touched position
         googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

         // Placing a marker on the touched position
         googleMap.addMarker(markerOptions);
		 
		        // Setting a click event handler for the map
		        googleMap.setOnMapClickListener(new OnMapClickListener() {
		 
		            @Override
		            public void onMapClick(LatLng latLng) {
		 
		                // Creating a marker
		                MarkerOptions markerOptions = new MarkerOptions();
		 
		                // Setting the position for the marker
		                markerOptions.position(latLng);
		 
		                // Setting the title for the marker.
		                // This will be displayed on taping the marker
		                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
		 
		                // Clears the previously touched position
		                googleMap.clear();
		 
		                // Animating to the touched position
		                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
		 
		                // Placing a marker on the touched position
		                googleMap.addMarker(markerOptions);
		            }
		        });
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMyLocationEnabled(true);
           
            
            //Setting event handler for location change
            googleMap.setOnMyLocationChangeListener(this);
            
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_touch, menu);
		return true;
	}
	
	//Event handler
	 @Override
	    public void onMyLocationChange(Location location) {
	        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
	 
	        // Getting latitude of the current location
	        double latitude = location.getLatitude();
	 
	        // Getting longitude of the current location
	        double longitude = location.getLongitude();
	 
	        // Creating a LatLng object for the current location
	        LatLng latLng = new LatLng(latitude, longitude);
	 
	        // Showing the current location in Google Map
	        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	 
	        // Zoom in the Google Map
	        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	 
	        // Setting latitude and longitude in the TextView tv_location
	        tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
	        
	        //Sending current location to server
	      //add the text in the arrayList
	        message = "Current Location : Lat => "+latitude+" , Longitude => "+longitude;
            arrayList.add(message);

            //sends the message to the server
            if (mTcpClient != null) {
                mTcpClient.sendMessage(message);
            }
	 
	    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	 public class connectTask extends AsyncTask<String,String,TCPClient> {
		 
	        @Override
	        protected TCPClient doInBackground(String... message) {
	 
	            //we create a TCPClient object and
	            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
	                @Override
	                //here the messageReceived method is implemented
	                public void messageReceived(String message) {
	                    //this method calls the onProgressUpdate
	                    publishProgress(message);
	                }
	            });
	            mTcpClient.run();
	 
	            return null;
	        }
	 
	        @Override
	        protected void onProgressUpdate(String... values) {
	            super.onProgressUpdate(values);
	 
	            System.out.println("\n Server Message"+values[0]);
	            
	            //Splitting server message to create a new driver info
	            String info[] = values[0].split(";");
	            String driverName = info[1];
	            double driverLongitude = Double.parseDouble(info[2]);
	            double driverLatitude = Double.parseDouble(info[3]);
	            
	            
	            PickUpDriver newDriver = new PickUpDriver(driverName, driverLongitude, driverLatitude);
	            activeDrivers.add(newDriver);
	        }
	    }


}
