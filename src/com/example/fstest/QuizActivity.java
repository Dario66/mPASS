package com.example.fstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.fstest.foursquare.FoursquareApp;
import com.example.fstest.foursquare.FsqVenue;
import com.example.fstest.foursquare.FoursquareApp.FsqAuthListener;
import com.example.fstest.fusiontables.FTClient;
import com.example.fstest.log.LogDbManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class QuizActivity extends Activity 
{
	private FTClient ftclient;
	private String name;
	private String fsqid;
	private String geo;
	private ProgressDialog spinner;
	private User user;
	private LogDbManager log;
	private String action_for_log;
	private String date_for_log;
	private FoursquareApp fsqapp;
	
	
	
	//DB POSTGRE
			private Socket client;
		    private PrintWriter printwriter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		/*
		user=new User(this);
		spinner=new ProgressDialog(this);
		ftclient=new FTClient(this);
		fsqapp = new FoursquareApp(this);
		FsqAuthListener listener=new FsqAuthListener() 
		{
			
			@Override
			public void onSuccess() 
			{
			}
			
			@Override
			public void onFail(String error) 
			{
				Toast.makeText(QuizActivity.this, "Foursquare Error", Toast.LENGTH_SHORT).show();
			}
		};
		fsqapp.setListener(listener);
		
		Intent i = getIntent();
		FsqVenue venue=(FsqVenue)i.getSerializableExtra("venue");
		
		fsqid=venue.id;
		name=venue.name;
		geo=venue.latitude.toString()+","+venue.longitude.toString();
		*/
		RelativeLayout rl=(RelativeLayout) findViewById(R.id.quiz_layout);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		
		//Crea interfaccia per il questionario
		
		TextView venue_name=new TextView(this);
		//venue_name.setText("Questionnaire "+venue.name);
		venue_name.setId(41);
		venue_name.setPadding(0, 0, 0, 20);
		rl.addView(venue_name);
		
		//Crea i vari radiogroup tramite l'apposita procedura
		createRadioGroup("Questo luogo è accessibile?","Accessibile","Parzialmente accessibile","Non accessibile", 1, 11);
		createRadioGroup("Ci sono delle porte?","Si","No","", 2, 12);
		createRadioGroup("Ci sono degli ascensori?","Si","No","Un piano!", 3, 13);
		createRadioGroup("Ci sono delle scale mobili?","Si","No","", 4, 14);
		createRadioGroup("C'è un parcheggio per disabili?","Si","No","", 5, 15);
		
		/*createRadioGroup("Can rooms be accessed without stairs?","Yes","No","", 1, 11);
		createRadioGroup("Can a wheelchair easily navigate the space?","Yes","No","", 2, 12);
		createRadioGroup("If multiple floors, is an elevator or escalator available?","Yes","No","", 3, 13);
		createRadioGroup("Are tactile navigation maps available??","Yes","No","", 4, 14);
		createRadioGroup("Is accessible parking available??","Yes","No","", 5, 15);*/
		
		TextView head_comment=new TextView(this);
		//head_comment.setText("Inserisci un commento sul luogo!");
		head_comment.setText("Submit a comment!");
		head_comment.setId(31);
		head_comment.setTextSize(20.0f);
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, 15);
		head_comment.setLayoutParams(lp);
		rl.addView(head_comment);
		
		EditText comment=new EditText(this);
		comment.setSingleLine(false);
		comment.setId(51);
		comment.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
		comment.setMinLines(3);
		comment.setMaxLines(10);
		comment.setVerticalScrollBarEnabled(true);
		//comment.setPadding(0, 0, 0, 0);
		//comment.setLines(3);
		lp=new RelativeLayout.LayoutParams(300,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, 31);
		comment.setLayoutParams(lp);
		comment.setBackgroundResource(R.drawable.rounded_edittext);
		rl.addView(comment);
		
		Button btn_submit=new Button(this);
		lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.BELOW, 51);
		btn_submit.setLayoutParams(lp);
		btn_submit.setText("Segnala");
		btn_submit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				spinner.setMessage("Caricamento...");
				spinner.setCancelable(false);
				spinner.show();
				int quiz1 = getRadioGroupResult(11);
				int quiz2 = getRadioGroupResult(12);
				int quiz3 = getRadioGroupResult(13);
				int quiz4 = getRadioGroupResult(14);
				int quiz5 = getRadioGroupResult(15);
				String squiz1="",squiz2="",squiz3="",squiz4="",squiz5="";
				switch (quiz1)
				{
				   case -1:squiz1=" ";
				   		   break;
				   case 0:squiz1="A";
		   		   		   break;
				   case 1:squiz1="P";
		   		   		   break;
				   case 2:squiz1="N";
		   		   	       break;
				   default:break;
				}
				switch (quiz2)
				{
				   case -1:squiz2=" ";
				   		   break;
				   case 0:squiz2="Yes";
		   		   		   break;
				   case 1:squiz2="No";
				   default:break;
				}
				switch (quiz3)
				{
				   case -1:squiz3=" ";
				   		   break;
				   case 0:squiz3="Yes";
		   		   		   break;
				   case 1:squiz3="No";
		   		   		   break;
				   case 2:squiz3="One floor";
		   		   	       break;
				   default:break;
				}
				switch (quiz4)
				{
				   case -1:squiz4=" ";
				   		   break;
				   case 0:squiz4="Yes";
		   		   		   break;
				   case 1:squiz4="No";
				   default:break;
				}
				switch (quiz5)
				{
				   case -1:squiz5=" ";
				   		   break;
				   case 0:squiz5="Yes";
		   		   		   break;
				   case 1:squiz5="No";
				   default:break;
				}
				final String comment_txt=getCommentText(51);
				
				
				//String query_txt="INSERT INTO 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ (name, geo, accessLevel, comment, doorways, elevator, escalator, parking) ";
				//query_txt=query_txt+"VALUES ('"+name+"', '"+geo+"', '"+squiz1+"', '"+comment_txt+"', '"+squiz2+"', '"+squiz3+"', '"+squiz4+"', '"+squiz5+"')";
				//Log.d("Test", query_txt);
				/*Date date=new Date();
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String sdate=dateFormat.format(date);
				date_for_log=sdate;
				action_for_log="Submitted a report about "+name+"."; //Così non vengono sostituiti i caratteri speciali per le richieste http e viene salvato il nome giusto nel log
				name=name.replace("'", "");
				name=name.replace("&","%26");
				String query_txt="INSERT INTO "+Costants.tableId+" (name, fsqid, geo, accessLevel, comment, doorways, elevator, escalator, parking, user, date) ";
				query_txt=query_txt+"VALUES ('"+name+"', '"+fsqid+"', '"+geo+"', '"+squiz1+"', '"+comment_txt+"', '"+squiz2+"', '"+squiz3+"', '"+squiz4+"', '"+squiz5+"', '"+user.getName()+"', '"+sdate+"')";
				//Creo le stringhe per l'azione da mettere nel log, la inserisco dopo solo se l'insert ha avuto successo
				//Log.d("Debug", query_txt);
				ftclient.setQuery(query_txt);
				ftclient.queryOnNewThread("insertvenue");
				*/
				//Quindi se siamo connessi a foursquare e la venue è all'interno del db di foursquare
				/*if (fsqapp.hasAccessToken() && !fsqid.substring(0, 2).equals("NF"))
				{
					Log.d("Debug", "Ok foursquare!");
					new Thread()
					{
						@Override
						public void run()
						{
							try 
							{
								//Servirebbe una conferma o qualcosa del genere
								fsqapp.checkIn(fsqid, comment_txt);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
					}.start();
				}*/
			}
		});
		
		
		
		//fine on click list
		
//INIZIO INVIO DATI POSIZIONE AL SERVER DATABASE
		
		final String message="PROVA";
		final int  port=4444;
		final String ip="192.168.1.102";
		new Thread(new Runnable(){
			
			@Override
			public void run(){
				try{
					
					client=new Socket(ip,port);
					printwriter =new PrintWriter(client.getOutputStream());
					printwriter.write(message);
					
					
					String hostName = ip;
					int portNumber = port;

					printwriter.flush();
					printwriter.close();
					client.close();
				}
				
				catch(UnknownHostException e){
					
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		}).start();
		
		
//FINE INVIO DATI AL SERVER DB
		
		
		
		
		rl.addView(btn_submit);
	}
	
	  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}
	
	public void loadResponse(String response, Boolean success)
	{
		spinner.dismiss();
		//Se la segnalazione è stata inviata correttamente alla fusion table, scriviamo il nuovo record nel log
		if (success)
		{
			log=new LogDbManager(this);
			log.openToWrite();
			log.insertEntry(action_for_log, date_for_log);
			log.close();
			user.addReport();
			Toast.makeText(QuizActivity.this, "Segnalazione avvenuta con successo!", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(QuizActivity.this, "Errore nell'inviare la segnalazione al server. Riprovare più tardi.", Toast.LENGTH_LONG).show();
		this.finish();
	}
	
	private String getCommentText(int id)
	{
		EditText comment=(EditText)findViewById(id);
		return comment.getText().toString();
	}
	
	private int getRadioGroupResult(int rgid)
	{
		RadioGroup rg=(RadioGroup)findViewById(rgid);
		int id=rg.getCheckedRadioButtonId();
		View rb = rg.findViewById(id);
		return(rg.indexOfChild(rb));
	}
	
	private void createRadioGroup(String question, String first, String second, String third, int id, int rgid)
	{
		RelativeLayout rl=(RelativeLayout) findViewById(R.id.quiz_layout);
		TextView tv_question=new TextView(this);
		tv_question.setText(question);
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		if (rgid!=11)
		{
			lp.addRule(RelativeLayout.BELOW , rgid-1);
		}
		else
		{
			lp.addRule(RelativeLayout.BELOW , 41);
		}
		tv_question.setLayoutParams(lp);
		tv_question.setId(id);
		tv_question.setTextSize(20.0f);
		rl.addView(tv_question);
		if (third!="")
		{
			RadioGroup rg=new RadioGroup(this);
			RadioButton rb[]=new RadioButton[3];
			rg.setOrientation(RadioGroup.VERTICAL);
			lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			lp.addRule(RelativeLayout.BELOW, tv_question.getId());
			rg.setPadding(0, 10, 0, 20);
			rg.setLayoutParams(lp);
		    rb[0]  = new RadioButton(this);
		    rg.addView(rb[0]); 
		    rb[0].setText(first);
		    rb[1]  = new RadioButton(this);
		    rg.addView(rb[1]); 
		    rb[1].setText(second);
		    rb[2]  = new RadioButton(this);
		    rg.addView(rb[2]); 
		    rb[2].setText(third);
		    rg.setId(rgid);
			rl.addView(rg);
		}
		else
		{
			RadioGroup rg=new RadioGroup(this);
			RadioButton rb[]=new RadioButton[2];
			rg.setOrientation(RadioGroup.VERTICAL);
			lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			lp.addRule(RelativeLayout.BELOW, tv_question.getId());
			rg.setPadding(0, 10, 0, 20);
			rg.setLayoutParams(lp);
		    rb[0]  = new RadioButton(this);
		    rg.addView(rb[0]); 
		    rb[0].setText(first);
		    rb[1]  = new RadioButton(this);
		    rg.addView(rb[1]); 
		    rb[1].setText(second);
		    rg.setId(rgid);
			rl.addView(rg);
		}
	}
}
