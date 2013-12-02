package com.example.fstest;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class User 
{
	private static final String name="name";
	private static final String type="user";
	private static final String imagePath="path";
	private static final String nreports="nreports";
	//Preferenze
	private static final String p_gaps="gaps";
	private static final String p_cross="cross";
	private static final String p_obstruction="obstruction";
	private static final String p_parking="parking";
	private static final String p_surface="surface";
	private static final String p_pathway="pathway";
	
	private SharedPreferences pref;
	private Context context;
	
	public User (Context _context)
	{
		context=_context;
		pref=context.getSharedPreferences("user", Context.MODE_PRIVATE);
	}
	
	public void setName(String _name)
	{
		Editor editor = pref.edit();
		editor.putString(name, _name);
		editor.commit();
	}
	
	public void setType(String _type)
	{
		Editor editor = pref.edit();
		editor.putString(type, _type);
		editor.commit();
	}
	
	public void setImagePath(String _imagePath)
	{
		Editor editor = pref.edit();
		editor.putString(imagePath, _imagePath);
		editor.commit();
	}
	
	public void addReport()
	{
		int tempreports=getNReports();
		Editor editor = pref.edit();
		editor.putInt(nreports, tempreports+1);
		editor.commit();
	}
	
	public void setPref(String category, int position, char value)
	{
		Editor editor = pref.edit();
		if (category.equals("Gaps"))
		{
			String pgaps=pref.getString(p_gaps, null);
			pgaps.toCharArray()[position]=value;
			editor.putString(p_gaps, pgaps);
		}
		editor.commit();
	}
	
	
	public String getName()
	{
		return pref.getString(name, null);
	}
	
	public String getType()
	{
		return pref.getString(type, null);
	}
	
	public String getImagePath()
	{
		return pref.getString(imagePath, null);
	}
	
	public int getNReports()
	{
		return pref.getInt(nreports, 0);
	}
	
	public ArrayList<PrefEntry> getPref(String category)
	{
		ArrayList<PrefEntry> listpref=new ArrayList<PrefEntry>();
		if (category.equals("Gaps"))
		{
			String pgaps=pref.getString(p_gaps, null);
			PrefEntry temp;
			for (int i=0;i<pgaps.length();i++)
			{
				temp=new PrefEntry("", null);
				switch (i)
				{
					case 0:temp.setEntry("Stairs");
					 		 break;
					case 1:temp.setEntry("Steps");
					 		 break;
					case 2:temp.setEntry("Ramps");
					 	     break;
					case 3:temp.setEntry("Curbs");
					 		 break;
					default: break;	
				}

				switch (pgaps.charAt(i))
				{
					case '0':temp.setValue("Neutral");
							 break;
					case '1':temp.setValue("Like");
					 		 break;
					case '2':temp.setValue("Dislike");
					 	     break;
					case '3':temp.setValue("Avoid");
					 		 break;
					default: temp.setValue("Neutral");
							 break;
				}
				listpref.add(temp);
			}
		}
		return listpref;
	}
	
	public void eraseUser()
	{
		Editor editor = pref.edit();
		editor.putString(name, null);
		editor.putString(type, null);
		editor.putString(imagePath, null);
		editor.putInt(nreports, 0);
		editor.commit();
		SharedPreferences pref_ac=context.getSharedPreferences("activity", Context.MODE_PRIVATE);
		editor=pref_ac.edit();
		editor.putBoolean("firsttime", true);
		editor.commit();
	}
	
	public void resetPref()
	{
		Editor editor = pref.edit();
		editor.putString(p_gaps, "0000");
		//qui ci vanno tutte le altre categorie
		editor.commit();
	}
}
