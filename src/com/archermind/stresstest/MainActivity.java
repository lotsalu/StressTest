package com.archermind.stresstest;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private ListView lvListView = null;
	// add test list
	List<String> demoDatas = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	/**
	 * this is an Initialization,if a activity is clickable ,must be add in this
	 * Block
	 */
	private void initView() {

		View btnCancle = (ViewGroup) findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(this);
		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);
	}

	/**
	 * this block is used to set test list add test list
	 */
	private void initData() {
		demoDatas.add("WIFI Stress Test");
		demoDatas.add("BT Stress Test");
		demoDatas.add("GPS Stress Test");
		ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, demoDatas);
		lvListView.setAdapter(myArrayAdapter);
	}

	public void onClick(View v) {
		if (v == findViewById(R.id.btnCancle)) {
			finish();
		}
	}

	/**
	 * add test list listener this can jump to the page specified
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		/**
		 * jump to wifi test page,can set the configuration.Like the interval
		 * time,repeat time.
		 */

		if (demoDatas.get(arg2).equals("WIFI Stress Test")) {
			Intent intent = new Intent(this, WIFIActivity.class);
			startActivity(intent);
		}
		/**
		 * jump to the BT test page that can set the configuration.Like the
		 * interval time,repeat time.
		 */

		if (demoDatas.get(arg2).equals("BT Stress Test")) {
			Intent intent = new Intent(this, WIFIActivity.class);
			startActivity(intent);
		}
	}
}
