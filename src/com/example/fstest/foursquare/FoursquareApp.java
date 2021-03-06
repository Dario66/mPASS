package com.example.fstest.foursquare;

import com.example.fstest.Costants;
import com.example.fstest.foursquare.FoursquareDialog.FsqDialogListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

public class FoursquareApp 
{
	private FoursquareSession mSession;
	private FoursquareDialog mDialog;
	private FsqAuthListener mListener;
	private ProgressDialog mProgress;
	private String mTokenUrl;
	private String mAccessToken;
	
	public static final String CALLBACK_URL = "myapp://connect";
	private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";	
	private static final String API_URL = "https://api.foursquare.com/v2";
	private static final String TAG = "FoursquareApi";
	
	public FoursquareApp(Context context) 
	{
		mSession		= new FoursquareSession(context);
		mAccessToken	= mSession.getAccessToken();
		mTokenUrl		= TOKEN_URL+"&client_id="+Costants.CLIENT_ID+"&client_secret="+Costants.CLIENT_SECRET+"&redirect_uri="+CALLBACK_URL;
		String url		= AUTH_URL + "&client_id=" + Costants.CLIENT_ID + "&redirect_uri=" + CALLBACK_URL;
		
		FsqDialogListener listener = new FsqDialogListener() 
		{
			@Override
			public void onComplete(String code) 
			{
				getAccessToken(code);
			}
			
			@Override
			public void onError(String error) 
			{
				mListener.onFail("Authorization failed");
			}
		};
		mDialog			= new FoursquareDialog(context, url, listener);
		mProgress		= new ProgressDialog(context);
		mProgress.setCancelable(false);
	}
	
	private void getAccessToken(final String code) 
	{
		mProgress.setMessage("Getting access token ...");
		mProgress.show();
		new Thread() 
		{
			@Override
			public void run() 
			{
				Log.i(TAG, "Getting access token");
				int what = 0;
				try 
				{
					URL url = new URL(mTokenUrl + "&code=" + code);
					
					Log.i(TAG, "Opening URL " + url.toString());
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					//urlConnection.setDoOutput(true);
					
					urlConnection.connect();
					
					JSONObject jsonObj  = (JSONObject) new JSONTokener(streamToString(urlConnection.getInputStream())).nextValue();
		        	mAccessToken 		= jsonObj.getString("access_token");
		        	
		        	Log.i(TAG, "Got access token: " + mAccessToken);
				} 
				catch (Exception ex) 
				{
					what = 1;
					ex.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}
	
	private void fetchUserName() 
	{
		mProgress.setMessage("Finalizing ...");
		
		new Thread() 
		{
			@Override
			public void run() 
			{
				Log.i(TAG, "Fetching user name");
				int what = 0;
		
				try 
				{
					String v	= timeMilisToString(System.currentTimeMillis()); 
					URL url 	= new URL(API_URL + "/users/self?oauth_token=" + mAccessToken + "&v=" + v);
					
					Log.d(TAG, "Opening URL " + url.toString());
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					//urlConnection.setDoOutput(true);
					
					urlConnection.connect();
					
					String response		= streamToString(urlConnection.getInputStream());
					Log.i(TAG, response);
					JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
		       
					JSONObject resp		= (JSONObject) jsonObj.get("response");
					JSONObject user		= (JSONObject) resp.get("user");
					
					//Ottengo l'url dell'immagine
					String firstName 	= user.getString("firstName");
		        	String lastName		= user.getString("lastName");
		        	JSONObject photo=(JSONObject)user.get("photo");
		        	String url_temp=photo.getString("prefix");
		        	url_temp=url_temp+"100x100";
		        	String url_photo=photo.getString("suffix");
		        	url_photo=url_temp+url_photo;
		        	
		        	Log.i(TAG, "Got user name: " + firstName + " " + lastName);
		        	
		        	mSession.storeAccessToken(mAccessToken, firstName + " " + lastName, url_photo);
				} 
				catch (Exception ex) 
				{
					what = 1;
					
					ex.printStackTrace();
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			if (msg.arg1 == 1) 
			{
				if (msg.what == 0) 
				{
					fetchUserName();
				} 
				else 
				{
					mProgress.dismiss();
					mListener.onFail("Failed to get access token");
				}
			} 
			else 
			{
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};
	
	public boolean hasAccessToken() 
	{
		return (mAccessToken == null) ? false : true;
	}
	
	public void setListener(FsqAuthListener listener) 
	{
		mListener = listener;
	}
	
	public String getUserName() 
	{
		return mSession.getUsername();
	}
	
	public String getPhoto()
	{
		return mSession.getPhotoUrl();
	}
	
	public void authorize() 
	{
		mDialog.show();
	}
	
	public boolean checkIn(String venueid, String comment) throws Exception
	{
		boolean success=false;
		try 
		{
			String v	= timeMilisToString(System.currentTimeMillis()); 
			URL url 	= new URL(API_URL + "/checkins/add?venueId=" + venueid + "&oauth_token=" + mAccessToken + "&v=" + v);
			Log.d(TAG, "Opening URL " + url.toString());
			
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			
			String response=streamToString(urlConnection.getInputStream());
			Log.d(TAG, response);
			JSONObject jsonObj=(JSONObject) new JSONTokener(response).nextValue();
			JSONObject meta=(JSONObject) jsonObj.getJSONObject("meta");
			String code=meta.getString("code");
			Log.d(TAG,code);
			if (!comment.isEmpty())
			{
				addTip(venueid, comment);
			}
			if (code.equals("200")) success=true;
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		return success;
	}
	
	private void addTip(String venueid, String comment) throws Exception
	{
		try 
		{
			if (comment.length()>150)
			{
				comment=comment.substring(0, 150);
				comment=comment.concat("...");
			}
			comment=comment.concat(" - By mPASS Application");
			comment=comment.replaceAll(" ", "%20");
			String v	= timeMilisToString(System.currentTimeMillis()); 
			URL url 	= new URL(API_URL + "/tips/add?venueId=" + venueid + "&text=" + comment + "&oauth_token=" + mAccessToken + "&v=" + v);
			Log.d(TAG, "Opening URL " + url.toString());
			
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			urlConnection.connect();
			
			String response=streamToString(urlConnection.getInputStream());
			Log.d(TAG, response);
			JSONObject jsonObj=(JSONObject) new JSONTokener(response).nextValue();
			JSONObject meta=(JSONObject) jsonObj.getJSONObject("meta");
			String code=meta.getString("code");
			Log.d(TAG,code);
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<FsqVenue> getNearby(double latitude, double longitude) throws Exception {
		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
		
		try 
		{
			String v	= timeMilisToString(System.currentTimeMillis()); 
			String ll 	= String.valueOf(latitude) + "," + String.valueOf(longitude);
			URL url 	= new URL(API_URL + "/venues/search?ll=" + ll + "&oauth_token=" + mAccessToken + "&v=" + v);
			//URL url 	= new URL(API_URL + "/venues/search?query=mirabilandia&ll=44,12"+ "&oauth_token=" + mAccessToken + "&v=" + v);
			
			Log.d(TAG, "Opening URL " + url.toString());
			
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			//urlConnection.setDoOutput(true);
			
			urlConnection.connect();
			
			String response		= streamToString(urlConnection.getInputStream());
			JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
			
			JSONArray groups	= (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");
			
			int length			= groups.length();
			//Log.d(TAG, jsonObj.toString());
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					//JSONObject group 	= (JSONObject) groups.get(i);
					//JSONArray items 	= (JSONArray) group.getJSONArray("items");
					
					//int ilength 		= items.length();
					//int ilength 		= groups.length();
					
					//for (int j = 0; j < ilength; j++) {
						//JSONObject item = (JSONObject) items.get(j);
						JSONObject item = (JSONObject) groups.get(i);
						
						FsqVenue venue 	= new FsqVenue();
						
						venue.id 		= item.getString("id");
						venue.name		= item.getString("name");
						
						JSONObject location = (JSONObject) item.getJSONObject("location");
						
						Location loc 	= new Location(LocationManager.GPS_PROVIDER);
						
						loc.setLatitude(Double.valueOf(location.getString("lat")));
						loc.setLongitude(Double.valueOf(location.getString("lng")));
						
						venue.latitude=Double.valueOf(location.getString("lat"));
						venue.longitude=Double.valueOf(location.getString("lng"));
						//venue.location	= loc;
						try
						{
							venue.address=location.getString("address");
						}
						catch (Exception ex)
						{
							venue.address=" ";
						}
						/*if (location.getString("address").isEmpty())
						{
							venue.address=" ";
						}
						else venue.address=location.getString("address");*/
						//venue.address	= location.getString("address");
						venue.distance	= (String.valueOf( location.getInt("distance")));
						venue.distance=venue.distance+" m";
						//venue.distance	= location.getString("distance");
						//venue.herenow	= item.getJSONObject("hereNow").getInt("count");
						//venue.type		= group.getString("type");
						try
						{
							JSONArray categories=(JSONArray) item.getJSONArray("categories");
							JSONObject category=(JSONObject) categories.get(0);
							venue.type=category.getString("name");
						}
						catch (Exception exc)
						{
							venue.type="Undefined";
						}
						
						venueList.add(venue);
					//}
				}
			}
		} 
		catch (Exception ex) 
		{
			throw ex;
		}
		
		return venueList;
	}
	
	public ArrayList<FsqVenue> getNearbyWithQuery(double latitude, double longitude, String query) throws Exception {
		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
		
		try 
		{
			String v	= timeMilisToString(System.currentTimeMillis()); 
			String ll 	= String.valueOf(latitude) + "," + String.valueOf(longitude);
			URL url 	= new URL(API_URL + "/venues/search?ll=" + ll +"&query="+ query +"&oauth_token=" + mAccessToken + "&v=" + v);
			//URL url 	= new URL(API_URL + "/venues/search?query=mirabilandia&ll=44,12"+ "&oauth_token=" + mAccessToken + "&v=" + v);
			
			Log.d(TAG, "Opening URL " + url.toString());
			
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			//urlConnection.setDoOutput(true);
			
			urlConnection.connect();
			
			String response		= streamToString(urlConnection.getInputStream());
			JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
			
			JSONArray groups	= (JSONArray) jsonObj.getJSONObject("response").getJSONArray("venues");
			
			int length			= groups.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) 
				{
						JSONObject item = (JSONObject) groups.get(i);
						
						FsqVenue venue 	= new FsqVenue();
						
						venue.id 		= item.getString("id");
						venue.name		= item.getString("name");
						
						JSONObject location = (JSONObject) item.getJSONObject("location");
						
						Location loc 	= new Location(LocationManager.GPS_PROVIDER);
						try
						{
							venue.address=location.getString("address");
						}
						catch (Exception ex)
						{
							venue.address=" ";
						}
						loc.setLatitude(Double.valueOf(location.getString("lat")));
						loc.setLongitude(Double.valueOf(location.getString("lng")));
						
						venue.latitude=Double.valueOf(location.getString("lat"));
						venue.longitude=Double.valueOf(location.getString("lng"));
						venue.distance	= (String.valueOf( location.getInt("distance")));
						venue.distance=venue.distance+" m";
						try
						{
							JSONArray categories=(JSONArray) item.getJSONArray("categories");
							JSONObject category=(JSONObject) categories.get(0);
							venue.type=category.getString("name");
						}
						catch (Exception exc)
						{
							venue.type="Undefined";
						}
						
						venueList.add(venue);
					//}
				}
			}
		} 
		catch (Exception ex) 
		{
			throw ex;
		}
		
		return venueList;
	}
	
	private String streamToString(InputStream is) throws IOException 
	{
		String str  = "";
		if (is != null) 
		{
			StringBuilder sb = new StringBuilder();
			String line;
			try 
			{
				BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));
				while ((line = reader.readLine()) != null) 
				{
					sb.append(line);
				}
				reader.close();
			} 
			finally 
			{
				is.close();
			}
			str = sb.toString();
		}
		return str;
	}
	
	private String timeMilisToString(long milis) 
	{
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Calendar calendar   = Calendar.getInstance();
		calendar.setTimeInMillis(milis);
		return sd.format(calendar.getTime());
	}
	
	public interface FsqAuthListener 
	{
		public abstract void onSuccess();
		public abstract void onFail(String error);
	}
}