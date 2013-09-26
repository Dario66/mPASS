package com.example.fstest;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class MapActivity extends Activity implements Runnable
{
	private GoogleMap mMap;
	private FTClient ftclient;
	private ProgressDialog spinner;
	private Thread thread;  
    private Handler handler;
    private int counter=0;
    private Context context;
    private HashMap<Marker, String> markerIdMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        context=this;
        setContentView(R.layout.activity_map);
        
        //Test spinner , in teoria dovrebbe vedersi prima del caricamento della mappa ma non funziona
        spinner=new ProgressDialog(this);
        spinner.setMessage("Caricamento mappa...");
        spinner.setCancelable(false);
        spinner.setMax(100); 
        spinner.setProgress(0); 
        spinner.show();
        
        handler=new Handler();
        thread=new Thread(this);
        thread.run();
    }
    
    //Resetta la mappa se c'� bisogno
    private Boolean setUpMapIfNeeded()
    {
    	Boolean needed=false;
    	if (mMap == null) 
    	{
    		needed=true;
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            ftclient.setQuery("SELECT ROWID, fsqid, name, geo FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ");
            ftclient.query("setmarkers");
            if (mMap != null) {}
                // The Map is verified. It is now safe to manipulate the map.

    	} 
    	return needed;
    }
    
    //Procedura per settare i marker dei luoghi nella mappa
    public void setMarkers(JSONArray venues)
    {
    	String name, ll, fsqid;
    	double lat = 0, lng = 0;
    	markerIdMap=new HashMap<Marker, String>(); //associa ad ogni marker un foursquare id, utilizzato per fare query per i singoli luoghi
    	//Manca la gestione di marker doppi
    	for(int i=0;i<venues.length();i++)
    	{
    		try 
    		{
				JSONArray row=venues.getJSONArray(i);
				fsqid=row.get(1).toString();
				name=row.get(2).toString();
				ll=row.get(3).toString();
				String[] lls=ll.split("\\,");
				lat=Double.parseDouble(lls[0]);
				lng=Double.parseDouble(lls[1]);
				Log.d("Test",name);
				Marker marker=mMap.addMarker(new MarkerOptions()
	    		.position(new LatLng(lat, lng))
	    		.title(name)
	    		.snippet("Commento...")
	    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
	    		.draggable(false)
	    		);
				markerIdMap.put(marker, fsqid);
			} 
    		catch (JSONException e) 
			{
				e.printStackTrace();
			}
    	}
    	
    	mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() 
    	{
			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				String fsqid=markerIdMap.get(marker);
				ftclient.setQuery("SELECT ROWID, name, accessLevel, comment, doorways, elevator, escalator, parking FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ WHERE fsqid='"+fsqid+"'");
				spinner.setMessage("Caricamento dati...");
				spinner.show();
				new Thread()
				{
					@Override
					public void run()
					{
						ftclient.query("loadvenue");
					}
				}.start();
			}
		});
    }

    //Callback eseguito dopo il completamento della query, mostra le info sul luogo
    public void showInfoDialog (JSONArray venues)
    {
    	spinner.dismiss();
    	InfoDialog idialog=new InfoDialog((Activity)context, venues);
		idialog.show();
    }
    
    //Test thread per visualizzare lo spinner prima del caricamento della mappa
	@Override
	public void run() 
	{
		try  
        {  
            synchronized (thread)  
            {  
                while(counter <= 4)  
                {  
                    thread.wait(500);  
                    counter++;  
                    handler.post(new Runnable()  
                    {  
                        @Override  
                        public void run()  
                        {   
                            spinner.setProgress(counter*25);  
                        }  
                    });  
                }  
            }  
        }  
		catch (InterruptedException e)  
        {  
            e.printStackTrace();  
        } 
		//Dopo lo spinner viene caricata la mappa
		handler.post(new Runnable()  
        {  
            @Override  
            public void run()  
            {  
                spinner.dismiss();  
                //setContentView(R.layout.activity_map);
                mMap=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                ftclient=new FTClient(context);
                setUpMapIfNeeded();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.277205,12.191162) , 6.0f));
                mMap.setMyLocationEnabled(true);
                ftclient.setQuery("SELECT ROWID, fsqid, name, geo FROM 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ");
                ftclient.query("setmarkers");
            }  
        });
		synchronized (thread)  
        {  
            thread.interrupt();  
        }
	}
}
