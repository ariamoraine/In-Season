package whats.in.season;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import whats.in.season.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final static String BASE_URL = "http://www.simplesteps.org/eat-local/state/";


	private final static String[] STATES = { "Click Here, Pick A State", "alabama", "alaska", "arizona",
			"arkansas", "california", "colorado", "connecticut", "delaware",
			"florida", "georgia", "hawaii", "idaho", "illinois", "indiana",
			"iowa", "kansas", "kentucky", "louisiana", "maine", "maryland",
			"massachusetts", "michigan", "minnesota", "mississippi", "missouri",
			"montana", "nebraska", "nevada", "new-hampshire", "new-jersey",
			"new-mexico", "new-york", "north-carolina", "north-dakota", "ohio",
			"oklahoma", "oregon", "pennsylvania", "rhode-island",
			"south-carolina", "south-dakota", "tennessee", "texas", "utah",
			"vermont", "virginia", "washington", "west-virginia", "wisconsin",
			"wyoming" };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		isNetworkAvailable();
		spinner();

	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	} 

	public void spinner() {
		//setting up the spinner
		if (isNetworkAvailable() == true){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, STATES);
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long value) {
				String stringOfItem = adapterView.getItemAtPosition(pos).toString();
				
				if (stringOfItem.equals("Click Here, Pick A State")) {
					System.out.print("nothing here yet");
				} else {
					Toast.makeText(getApplicationContext(), "Getting data" , Toast.LENGTH_LONG).show();
					LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); // starting hashmap

					for (int i = 0; i < STATES.length - 1; i++) { // filling hashmap
						map.put(STATES[i], BASE_URL + STATES[i]);
					}

					Iterator<Map.Entry<String, String>> iter = map.entrySet()
							.iterator();  

					String testValue = map.get(stringOfItem);

					try {
						URL urlOfTestValue = new URL(testValue);

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
	}else{ Toast.makeText(getApplicationContext(), "There is no network available", Toast.LENGTH_LONG).show();
	}
	}

	public class MyAsyncTask extends AsyncTask<URL, Void, String> {
		protected String doInBackground(URL... params) {
			//where we do all of our site scraping
			URL urlOfTestValue = params[0];
			BufferedReader reader = null;
			String html = null;
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
					
					StringBuffer builder = new StringBuffer();
					for (String line; (line = reader.readLine()) != null;) {
						builder.append(line.trim()).toString();
						//adding all the lines to our stringBuffer
					}
					String start = "<div class=\"state-produce\">";
					//start is a built in part of string buffer and the end of it is where the html that we what is
					String end = "</div></div>";
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
							"Turkey - Standard Bronze");  // all of these seem like repetitive actions (replace a - with a "")
					html = html.replace("Oysters,", "Oysters -");  // is there a way you can make this into a function?
					//all of this is pulling all text we want out of the html tags
					
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
			
			TextView veggiesOne = (TextView) findViewById(R.id.veggiesOne);
			TextView veggiesTwo = (TextView) findViewById(R.id.veggiesTwo);
			String[] earlyMonthVeggieArray = null;
			String[] lateMonthVeggieArray = null;
			
			String[] monthList = html.split("NEW MONTH");
			//monthList is an array of our scrapped text split on each new month
			String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
			SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
			Calendar calendar = new GregorianCalendar(pdt);
			//finding the current month

			//setting up a hashmap linking the month string to the month int i get back from calendar
			LinkedHashMap<Integer, String> monthMap = new LinkedHashMap<Integer, String>();  // is this going to change?  Probably not.
			monthMap.put(0, "January");  	// putting it here, you're recreating it every single time this function is run.
			monthMap.put(1, "February");	// try moving it to a constant at the top
			monthMap.put(2, "March");		// or look into creating an Enum class
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
			String[] eachMonthList = ourMonth.split(",");
			String currentMonth = eachMonthList[0];
			
			//going over our monthlist and checking if the month is on the website
			for (String veggies : monthList) {
				if (veggies.indexOf("Early " + ourMonth) > -1) {
					//if the early month is on the site it updates the var and we set the text later
					String[] wholeEarlyMonthArray = veggies.split(",");
					earlyMonthVeggieArray = Arrays.copyOfRange(wholeEarlyMonthArray, 1, wholeEarlyMonthArray.length);
//					System.out.print(Arrays.toString(wholeEarlyMonthArray));
				} else if (veggies.indexOf("Late " + ourMonth) > -1) {
					//if the late part of the month is there, do the same
					String[] wholeLateMonthArray = veggies.split(",");
					lateMonthVeggieArray = Arrays.copyOfRange(wholeLateMonthArray, 1, wholeLateMonthArray.length);
					
				} else {
//					Toast.makeText(getApplicationContext(), "Getting Data", Toast.LENGTH_SHORT).show();
//					System.out.println("Looking");
				}
			}
			
			if (earlyMonthVeggieArray == null && lateMonthVeggieArray == null) {
				//if the veggies var hasn't been set then the month we were looking for isn't on the site aka has nothing in season
				veggiesOne.setText("There is nothing in season this month.");
				veggiesTwo.setText(" ");
			} else {
				if (earlyMonthVeggieArray != null) {
					//posting the veggies to the UI
					veggiesOne.setVisibility(View.VISIBLE);
					veggiesOne.setText(Html.fromHtml("<b>" + "Early " + currentMonth + "</b>" + Arrays.toString(earlyMonthVeggieArray).replace("[","").replace("]", "")));
				} else {
					veggiesOne.setText("Early has nothing");
				}

				if (lateMonthVeggieArray != null) {
					//posting the veggies to the UI
					veggiesTwo.setVisibility(View.VISIBLE);
					veggiesTwo.setText(Html.fromHtml("<b>" + "Late " + currentMonth + "</b>" + Arrays.toString(lateMonthVeggieArray).replace("[","").replace("]", "")));
				} else {
					veggiesTwo.setText("Late has nothing");
				}
			}
		}
	}

	}
