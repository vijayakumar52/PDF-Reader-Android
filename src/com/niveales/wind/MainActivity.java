package com.niveales.wind;

import java.util.ArrayList;
import java.util.Arrays;

import com.adsonik.pdfreaderwithdictionary.R;
import com.librelio.activity.DictNew;
import com.librelio.activity.Dictionary;
import com.librelio.activity.MuPDFActivity;
import com.librelio.activity.RateUs;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@SuppressLint("NewApi") @Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		switch(position){
		case 0:
		
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		break;
		case 1:
			Intent i=new Intent(MainActivity.this,DictNew.class);
			startActivity(i);
			
			
			break;
		case 2:
			Intent i1=new Intent(MainActivity.this,RateUs.class);
			startActivity(i1);
			
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}

	@SuppressLint("NewApi") public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	@SuppressLint("NewApi") public static class PlaceholderFragment extends Fragment implements OnItemClickListener {
		
		 Button enter;
		 TextView t1,t2,t3;
		 ListView lv;
		
		 ArrayList<String> list=null;
		 ArrayAdapter<String> adapter;
		 SharedPreferences myPrefs;

		/* The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		@SuppressLint("NewApi") public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			enter=(Button)rootView.findViewById(R.id.button1);
			t1=(TextView)rootView.findViewById(R.id.textView23);
			t3=(TextView)rootView.findViewById(R.id.txtViewHistory);
			//t3.setTypeface(tf);
			
			lv=(ListView)rootView.findViewById(R.id.listView1);
			
			list=new ArrayList<String>();
			myPrefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
			String check=myPrefs.getString("key",null);
			if(check!=null){
				t3.setVisibility(View.VISIBLE);
			String[] valuee=check.split(";",-1);
			for(int i=0;i<valuee.length;i++){
			list.add(valuee[i]);
			}
			}
			adapter=new ArrayAdapter<String>(getActivity(),R.layout.grouprow,R.id.txtView1,list);
			adapter.notifyDataSetChanged();

			lv.setAdapter(adapter);
			t3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myPrefs.edit().clear().commit();
					adapter.clear();
					adapter.notifyDataSetChanged();
					
					
				}
			});
			lv.setOnItemClickListener(this);
			enter.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					 Intent in=new Intent();
					 in.setAction(Intent.ACTION_GET_CONTENT);
					 in.setType("file/*");
					 startActivityForResult(in,21);
				}
			});
			

			return rootView;
		}

		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
			if(requestCode==21){
				if(resultCode==RESULT_OK){
			
			String filep=data.getData().getPath();
			String name=data.getData().getLastPathSegment();
			Intent iii=new Intent(getActivity(),MuPDFActivity.class);
			iii.putExtra("returnvalue", filep);
			iii.putExtra("name", name);
			startActivity(iii);
			getActivity().finish();
			}
			}
			
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String abc=(String)parent.getItemAtPosition(position);
			String output=null;
			String ab=myPrefs.getString("path", null);
			String a[]=ab.split(";",-1);
			for(int i=0;i<a.length;i++){
				if(a[i].contains(abc)){
					output=a[i];
				}
			}
			Intent iii=new Intent(getActivity(),MuPDFActivity.class);
			iii.putExtra("returnvalue", output);
			iii.putExtra("name", abc);
			startActivity(iii);
			getActivity().finish();
			}

	}

}
