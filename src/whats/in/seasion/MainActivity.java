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

public class MainActivity extends Activity {

	public TextView veggiesOne;
	public TextView veggiesTwo;
	public static URL stateUrl;
	public static Spinner spinner;
	public String testValue;
	public String start;
	public String end;
	public String html;
	BufferedReader reader;
	public URL urlOfTestValue;
	public String baseUrl = "http://www.simplesteps.org/eat-local/state/";
	public StringBuffer builder = new StringBuffer();

	String[] states = { "alabama", "alaska", "arizona", "arkansas",
			"california", "colorado", "connecticut", "delaware", "florida",
			"gorgia", "hawaii", "idaho", "illinois", "indiana", "iowa",
			"kansas", "kentucky", "louisiana", "maine", "maryland",
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, states);
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Object item = spinner.getSelectedItem(); // selected state
				String stringOfItem = "";
				stringOfItem = item.toString(); // convert
				System.out.println(stringOfItem);

				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); // started

				for (int i = 0; i < states.length - 1; i++) { // filling hashmap
					map.put(states[i], baseUrl + states[i]);
				}

				Iterator<Map.Entry<String, String>> iter = map.entrySet()
						.iterator();

				testValue = map.get(stringOfItem);
				System.out.println(testValue);
				System.out.println(stringOfItem);

				try {
					urlOfTestValue = new URL(testValue);
					System.out.println("inside try for pullVeggies");
					MyAsyncTask aTask = new MyAsyncTask();
					System.out.println(testValue);
					System.out.println(urlOfTestValue);
					aTask.execute(urlOfTestValue);
					// pullVeggies(new URL(testValue));
					System.out.println("under pullVeggies");
				} catch (MalformedURLException e) {
					System.out.println("pullVeggies failed ");
					e.printStackTrace();
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				System.out.println("Nothing selected");
			}

		});
	}

	public class MyAsyncTask extends AsyncTask<URL, Void, String> {

		protected String doInBackground(URL... params) {

			try {
				try {
					System.out.println(urlOfTestValue);
					reader = new BufferedReader(new InputStreamReader(
							urlOfTestValue.openStream(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					Log.i("showTime", e.getMessage());
				} catch (IOException e) {
					Log.i("showTime", e.getMessage());
				}
				try {
					for (String line; (line = reader.readLine()) != null;) {
						// System.out.println("checking where disconnect is");
						// System.out.println(line);
						builder.append(line.trim()).toString();
						// System.out.println(line);
						// System.out.println("above builder");
						// System.out.println(builder);
					}
					System.out.println("Inside run");
					System.out.println(builder);
					System.out.println(urlOfTestValue);
					// the next two lines are setting the beginning and end of
					// what we want
					start = "<div class=\"state-produce\">";
					end = "</div></div>";
					System.out.println("under div div");
					System.out.println(start.length());
					System.out.println(end.length());
					System.out.println(builder);
					// This next line is where it breaks :(
					String part = builder.substring(builder.indexOf(start)
							+ start.length());
					System.out.println(part);
					System.out.println("under string part");
					html = part.substring(0, part.indexOf(end));
					System.out.println(html);
					System.out.println("testing just made html var");
					// html = start.substring(0, start.indexOf(end));
					html = html.replaceAll("<h3>", " NEW MONTH");
					html = html.replaceAll("</h3>", ", ");
					html = html.replace("- ", "");
					html = html.replaceAll("\\<.*?>", "");
					html = html.replace("Christmas Trees,", "");
					html = html.replace("Wreathes", "");
					html = html.replace(
							"Turkey Bourbon Red, Turkey Standard Bronze",
							"Turkey");
					html = html.replace("Oysters,", "Oysters -");
					System.out.println(html);
					System.out.println("Under all the html shit");
					builder = new StringBuffer();

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

			veggiesOne = (TextView) findViewById(R.id.veggiesOne);
			veggiesTwo = (TextView) findViewById(R.id.veggiesTwo);
			String veggiesTextOne = null;
			String veggiesTextTwo = null;

			System.out.println("inside onpostexecute");
			String[] monthList = html.split("NEW MONTH");

			System.out.println(monthList.length);
			String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
			SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
			Calendar calendar = new GregorianCalendar(pdt);
			System.out.println("under calendarsetting");

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

			for (String veggies : monthList) {
				System.out.println(veggies);
				if (veggies.indexOf("Early " + ourMonth) > -1) {
					veggiesTextOne = veggies;
				} else if (veggies.indexOf("Late " + ourMonth) > -1) {
					veggiesTextTwo = veggies;
				} else {
					System.out.print("still looking");
				}
			}
			if (veggiesTextOne == null && veggiesTextTwo == null) {
				veggiesOne.setText("This month has nothing in season");
				veggiesTwo.setText(" ");
			} else {
				if (veggiesTextOne != null) {
					veggiesOne.setText(veggiesTextOne);
				} else {
					veggiesOne.setText("Early has nothing");
				}

				if (veggiesTextTwo != null) {
					veggiesTwo.setText(veggiesTextTwo);
				} else {
					veggiesTwo.setText("Late has nothing");
				}
			}
		}
	}
}
