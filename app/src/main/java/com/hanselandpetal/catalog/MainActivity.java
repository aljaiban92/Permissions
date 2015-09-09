package com.hanselandpetal.catalog;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.hanselandpetal.catalog.model.Flower;
import com.hanselandpetal.catalog.parsers.FlowerJSONParser;
import com.hanselandpetal.catalog.parsers.FlowerXMLParser;

public class MainActivity extends Activity {

	TextView output;
	ProgressBar pb;
	List<MyTask> tasks;

	List<Flower> flowerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		Initialize the TextView for vertical scrolling
		output = (TextView) findViewById(R.id.textView);
		output.setMovementMethod(new ScrollingMovementMethod());

		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);
		tasks = new ArrayList<>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_do_task) {
			if(isOnline())
				requestData("http://services.hanselandpetal.com/secure/flowers.json");
			else
			{
				Toast.makeText(this, "Network not avaible", Toast.LENGTH_LONG).show();
			}
		}
		return false;
	}

	private void requestData(String uri) {
		MyTask task = new MyTask();
		task.execute(uri);
	}

	protected void updateDisplay() {
		if (flowerList != null){
			for (Flower f: flowerList) {
				output.append(f.getName() + "\n");
			}
		}
	}

	protected boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	private class MyTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			if(tasks.size() == 0)
				pb.setVisibility(View.VISIBLE);
			tasks.add(this);
		}

		@Override
		protected String doInBackground(String... params) {
			String content = HttpManager.getData(params[0], "feeduser", "feedpassword");
			return content;
		}

		@Override
		protected void onPostExecute(String result) {

			pb.setVisibility(View.INVISIBLE);

			if(result == null){
				Toast.makeText(MainActivity.this, "Cannnot connect to the service", Toast.LENGTH_LONG).show();
			}

			flowerList = FlowerJSONParser.parseFeed(result);
			updateDisplay();

		}

		@Override
		protected void onProgressUpdate(String... values) {
			updateDisplay();
		}

	}

}