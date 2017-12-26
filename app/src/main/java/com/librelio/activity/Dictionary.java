package com.librelio.activity;

import com.adsonik.pdfreaderwithdictionary.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Dictionary extends Activity {
	TextView tv1,tv2,tv3;
	EditText eddd;
	Button btn1;
	private static final String DB_NAME = "EmployeeDatabase.db";
	private SQLiteDatabase database1;
	

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);
		 ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
	      database1 = dbOpenHelper.openDataBase();
	        
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Dictionary");
		// actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(248, 174, 16)));


		String recei=getIntent().getStringExtra("word");
		String recei1=getIntent().getStringExtra("definition");
		
		tv2=(TextView)findViewById(R.id.textView2);
		tv3=(TextView)findViewById(R.id.textView3);
		
		eddd=(EditText)findViewById(R.id.editText12);
		btn1=(Button)findViewById(R.id.button12);
		tv2.setText("");
		String[] tring=recei1.split(";",-1);
		for(int i=0,j=1;i<tring.length;i++,j++){
		tv2.append(j+". "+tring[i]+"\n"+"\n");
		}
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String output=fetchData(eddd.getText().toString().toUpperCase());
				
				if(output!=null){
					tv2.setText("");
					String[] tring1=output.split(";",-1);
					for(int i=0,j=1;i<tring1.length;i++,j++){
					tv2.append(j+". "+tring1[i]+"\n"+"\n");
					}
				}else{
					tv2.setText("Oops!!! Looks like the word is not available.");
				}
				
			}
		});
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                
           
               this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

	private String fetchData(String search) {
		String name=null;
		//Cursor friendCursor = database.query(TABLE_NAME, new String[] {FRIEND_ID, FRIEND_NAME},null, null, null, null, FRIEND_NAME);
		Cursor friendCursor=database1.rawQuery("select * from emp where word='"+search+"'",null);
		friendCursor.moveToFirst();
		if(!friendCursor.isAfterLast()) {
			do {
				name = friendCursor.getString(1);
			} while (friendCursor.moveToNext());
		}
		friendCursor.close();
		return name;
	}



}
