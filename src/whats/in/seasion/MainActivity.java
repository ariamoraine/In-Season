package whats.in.seasion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public TextView veggiesOne;
	public TextView veggiesTwo;
	public static URL stateUrl;
	//public  Spinner spinner;
	public String testValue;
	public String start;
	public String end;
	public String html;
	BufferedReader reader;
	public URL urlOfTestValue;
	public String baseUrl = "http://www.simplesteps.org/eat-local/state/";
	public StringBuffer builder = new StringBuffer();

	String[] states = { "Pick A State", "alabama", "alaska", "arizona",
			"arkansas", "california", "colorado", "connecticut", "delaware",
			"florida", "gorgia", "hawaii", "idaho", "illinois", "indiana",
			"iowa", "kansas", "kentucky", "louisiana", "maine", "maryland",
			"massachustts", "michigan", "minnesota", "mississippi", "missouri",
			"montana", "nebraska", "nevada", "new-hampshire", "new-jersry",
			"new-mexico", "new-york", "north-carolina", "north-dakota", "ohio",
			"oklahoma", "oregon", "pennsylvania", "rhode-island",
			"south-carolina", "south-dakota", "tennessee", "texas", "utah",
			"vermont", "virginia", "washington", "west-virginia", "wisconsin",
			"wyoming" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinner();

	}

	public void spinner() {
		//setting up my spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, states);
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long value) {
				String stringOfItem = adapterView.getItemAtPosition(pos).toString();
				
				if (stringOfItem == "Pick A State") {
					System.out.print("nothing here yet");
				} else {
					Toast.makeText(getApplicationContext(), "Getting data" , Toast.LENGTH_SHORT).show();
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); // starting hashmap

					for (int i = 0; i < states.length - 1; i++) { // filling hashmap
						map.put(states[i], baseUrl + states[i]);
					}

					Iterator<Map.Entry<String, String>> iter = map.entrySet()
							.iterator();

					testValue = map.get(stringOfItem);

					try {
						urlOfTestValue = new URL(testValue);

						MyAsyncTask aTask = new MyAsyncTask();

						aTask.execute(urlOfTestValue);
					} catch (MalformedURLException e) {
						System.out.println("pullVeggies failed ");
						e.printStackTrace();
					}

				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(getApplicationContext(), "There is nothing selected", Toast.LENGTH_SHORT).show();
			}

		});
	}

	public class MyAsyncTask extends AsyncTask<URL, Void, String> {
		//the AsyncTask for our scraping
		
		protected String doInBackground(URL... params) {
			//where we do all of our site scraping

			try {
				try {
					//opening up the stream to our chosen URL
					reader = new BufferedReader(new InputStreamReader(
							urlOfTestValue.openStream(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					Log.i("showTime", e.getMessage());
				} catch (IOException e) {
					Log.i("showTime", e.getMessage());
				}
				try {
					
					for (String line; (line = reader.readLine()) != null;) {
						builder.append(line.trim()).toString();
						//adding all the lines to our stringBuffer
					}
					start = "<div class=\"state-produce\">";
					//start is a built in part of string buffer and the end of it is where the html that we what is
					end = "</div></div>";
					//end is also built in and the end of the html that we want.

					String part = builder.substring(builder.indexOf(start)
							+ start.length());
					//putting all the pieces together.
					
					html = part.substring(0, part.indexOf(end));
					html = html.replaceAll("<h3>", " NEW MONTH");
					html = html.replaceAll("</h3>", ", ");
					html = html.replace("- ", "");
					html = html.replaceAll("\\<.*?>", "");
					html = html.replace("Wreathes", "Wreathes.");
					html = html.replace("Halibut, Pacific,", "Halibut - Pacific,");
					html = html.replace("Shrimp, Pink,", "Shrimp - Pink,");
					html = html.replace("Turkey Bourbon Red", "Turkey - Bourbon Red");
					html = html.replace("Turkey Standard Bronze",
							"Turkey - Standard Bronze");
					html = html.replace("Oysters,", "Oysters -");
					//all of this is pulling all text we want out of the html tags
					builder = new StringBuffer(); //html is not the var we want and we need to restart builder for next time this code is run

				} catch (IOException e) {
					Log.i("showTime", e.getMessage());
				}
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException logOrIgnnore) {
					}
			}

			return html;
		}

		protected void onPostExecute(String html) {
			//onPostExecute is a built in part of AsyncTask and auto starts when doInBackground is done
			
			veggiesOne = (TextView) findViewById(R.id.veggiesOne);
			veggiesTwo = (TextView) findViewById(R.id.veggiesTwo);
			String veggiesTextOne = null;
			String veggiesTextTwo = null;

			String[] monthList = html.split("NEW MONTH");
			//monthList is an array of our scrapped text split on each new month
			String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
			SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
			Calendar calendar = new GregorianCalendar(pdt);
			//finding the current month

			//setting up a hashmap linking the month string to the month int i get back from calendar
			LinkedHashMap<Integer, String> monthMap = new LinkedHashMap<Integer, String>();
			monthMap.put(0, "January");
			monthMap.put(1, "February");
			monthMap.put(2, "March");
			monthMap.put(3, "April");
			monthMap.put(4, "May");
			monthMap.put(5, "June");
			monthMap.put(6, "July");
			monthMap.put(7, "August");
			monthMap.put(8, "September");
			monthMap.put(9, "October");
			monthMap.put(10, "Novemver");
			monthMap.put(11, "December");

			String ourMonth = (monthMap.get(calendar.get(Calendar.MONTH)));

			//going over our monthlist and checking if the month is on the website
			for (String veggies : monthList) {
				if (veggies.indexOf("Early " + ourMonth) > -1) {
					//if the early month is on the site it updates the var and we set the text later
					veggiesTextOne = veggies;
				} else if (veggies.indexOf("Late " + ourMonth) > -1) {
					//if the late part of the month is there, do the same
					veggiesTextTwo = veggies;
				} else {
					System.out.println("looking");
				}
			}
			
			if (veggiesTextOne == null && veggiesTextTwo == null) {
				//if the veggies var hasn't been set then the month we were looking for isn't on the site aka has nothing in season
				veggiesOne.setText("This month has nothing in season");
				veggiesTwo.setText(" ");
			} else {
				if (veggiesTextOne != null) {
					//posting the veggies to the UI
					veggiesOne.setText(veggiesTextOne);
				} else {
					veggiesOne.setText("Early has nothing");
				}

				if (veggiesTextTwo != null) {
					//posting the veggies to the UI
					veggiesTwo.setText(veggiesTextTwo);
				} else {
					veggiesTwo.setText("Late has nothing");
				}
			}
		}
	}
}
