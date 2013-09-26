package com.example.fstest;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.graphics.drawable.Drawable;
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

public class QuizActivity extends Activity {

	private FTClient ftclient;
	private String name;
	private String fsqid;
	private String geo;
	private ProgressDialog spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		
		spinner=new ProgressDialog(this);
		ftclient=new FTClient(this);
		
		Intent i = getIntent();
		FsqVenue venue=(FsqVenue)i.getSerializableExtra("venue");
		
		fsqid=venue.id;
		name=venue.name;
		geo=venue.latitude.toString()+","+venue.longitude.toString();
		
		RelativeLayout rl=(RelativeLayout) findViewById(R.id.quiz_layout);
		rl.setGravity(Gravity.CENTER_HORIZONTAL);
		
		//Crea interfaccia per il questionario
		
		TextView venue_name=new TextView(this);
		venue_name.setText("Questionario "+venue.name);
		venue_name.setId(41);
		venue_name.setPadding(0, 0, 0, 20);
		rl.addView(venue_name);
		
		//Crea i vari radiogroup tramite l'apposita procedura
		createRadioGroup("Questo luogo � accessibile?","Accessibile","Parzialmente accessibile","Non accessibile", 1, 11);
		createRadioGroup("Ci sono delle porte?","Si","No","", 2, 12);
		createRadioGroup("Ci sono degli ascensori?","Si","No","Un piano!", 3, 13);
		createRadioGroup("Ci sono delle scale mobili?","Si","No","", 4, 14);
		createRadioGroup("C'� un parcheggio per disabili?","Si","No","", 5, 15);
		
		TextView head_comment=new TextView(this);
		head_comment.setText("Inserisci un commento sul luogo!");
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
				String comment_txt=getCommentText(51);
				//String query_txt="INSERT INTO 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ (name, geo, accessLevel, comment, doorways, elevator, escalator, parking) ";
				//query_txt=query_txt+"VALUES ('"+name+"', '"+geo+"', '"+squiz1+"', '"+comment_txt+"', '"+squiz2+"', '"+squiz3+"', '"+squiz4+"', '"+squiz5+"')";
				//Log.d("Test", query_txt);
				String query_txt="INSERT INTO 1JvwJIV2DOSiQSXeSCj8PA8uKuSmTXODy3QgikiQ (name, fsqid, geo, accessLevel, comment, doorways, elevator, escalator, parking) ";
				query_txt=query_txt+"VALUES ('"+name+"', '"+fsqid+"', '"+geo+"', '"+squiz1+"', '"+comment_txt+"', '"+squiz2+"', '"+squiz3+"', '"+squiz4+"', '"+squiz5+"')";
				ftclient.setQuery(query_txt);
				new Thread()
				{
					@Override
					public void run()
					{
						//ftclient.query(query_txt, "insertvenue");
						ftclient.query("insertvenue");
					}
				}.start();
			}
		});
		rl.addView(btn_submit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz, menu);
		return true;
	}
	
	public void loadResponse(String response)
	{
		spinner.dismiss();
		Toast.makeText(QuizActivity.this, "Segnalazione avvenuta con successo!", Toast.LENGTH_LONG).show();
		this.finish();
		//Serve controllare se la segnalazione ha veramente avuto successo
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
