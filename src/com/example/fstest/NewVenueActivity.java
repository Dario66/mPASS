package com.example.fstest;

import java.util.ArrayList;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;

import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NewVenueActivity extends Activity 
{
	private String maxid;
	private GoogleMap mMap;
	private GPSTracker gps;
	private LatLng selectedLocation;
	
	
	
	//OSM
	 private MapView myOpenMapView;
		private MapController myMapController;
		ArrayList<OverlayItem> anotherOverlayItemArray;
		
		MyLocationOverlay myLocationOverlay = null;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_venue);
		
		
		myOpenMapView = (MapView)findViewById(R.id.openmapview2);
        myOpenMapView.setBuiltInZoomControls(true);
        myMapController = myOpenMapView.getController();
        myMapController.setZoom(2);
        
        myOpenMapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		
        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
        myOpenMapView.getOverlays().add(myScaleBarOverlay);
        
        //aggiungi la mia locazione con MyLocationOverlay
        myLocationOverlay = new MyLocationOverlay(this, myOpenMapView);
        myOpenMapView.getOverlays().add(myLocationOverlay);
        myOpenMapView.postInvalidate();
		/*gps=new GPSTracker(this);
		selectedLocation=new LatLng(gps.getLatitude(), gps.getLongitude());
		mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map2)).getMap();
		mMap.setMyLocationEnabled(true);
		setUpMapIfNeeded();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(),gps.getLongitude()) , 15.0f));
		*/
		/*mMap.setOnMapClickListener(new OnMapClickListener() 
		{
			@Override
			public void onMapClick(LatLng point) 
			{
				mMap.clear();
				mMap.addMarker(new MarkerOptions()
				.position(point)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				selectedLocation=point;
			}
		});*/
		//maxid=getIntent().getExtras().getString("maxid");
		//Log.d("Debug", maxid);
	}
//GESTIRE IL TAP CON LA MAPPA OSM
	 OnItemGestureListener<OverlayItem> myOnItemGestureListener
	    = new OnItemGestureListener<OverlayItem>(){

		 
		 
			@Override
			public boolean onItemLongPress(int arg0, OverlayItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onItemSingleTapUp(int index, OverlayItem item) {
				
				anotherOverlayItemArray = new ArrayList<OverlayItem>();
				anotherOverlayItemArray.add(item);
				
				
		        		
		        		
			    myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);
		        
				Toast.makeText(NewVenueActivity.this, 
						item.mDescription + "\n"
						+ item.mTitle + "\n"
						+ item.mGeoPoint.getLatitudeE6() + " : " + item.mGeoPoint.getLongitudeE6(), 
						Toast.LENGTH_LONG).show();
				return true;
			}
			
	    	
	    };
	
	    ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>
		(this, anotherOverlayItemArray, myOnItemGestureListener);
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_venue, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        	case R.id.item_verify:EditText input=(EditText)findViewById(R.id.et_newvenue);
        						  if (input.getText().toString().isEmpty())
        							  Toast.makeText(NewVenueActivity.this, "Nome del luogo mancante!", Toast.LENGTH_SHORT).show();
        						  else
        						  {
	        						  //maxid=maxid.substring(2);
						    		  maxid=maxid.substring(2, maxid.length()-2);
						    		  Log.d("Debug", maxid);
						    		  int newid=Integer.parseInt(maxid)+1;
						    		  final String snewid="NF"+Integer.toString(newid);
	        						  FsqVenue newvenue=new FsqVenue();
									  newvenue.name=input.getText().toString();
									  newvenue.latitude=selectedLocation.latitude;
									  newvenue.longitude=selectedLocation.longitude;
									  newvenue.direction=0;
									  newvenue.address=" ";
									  newvenue.id=snewid;
									  newvenue.distance="";
									  newvenue.type="";
									  Intent quiz_intent=new Intent(NewVenueActivity.this, QuizActivity.class);
									  quiz_intent.putExtra("venue", newvenue);
									  NewVenueActivity.this.startActivity(quiz_intent);
									  finish();
        						  }
        					      break;
        	default:break;
        }
        return true;
    }
	
	private Boolean setUpMapIfNeeded()
    {
    	Boolean needed=false;
    	if (mMap == null) 
    	{
    		//needed=true;
           // mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2))
             //                   .getMap();
            if (mMap != null) {}
                // The Map is verified. It is now safe to manipulate the map.

    	} 
    	return needed;
    }
}