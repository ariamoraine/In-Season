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
	public String baseUrl = "http://www.simplesteps.org/eat-local/state/";
	StringBuilder builder = new StringBuilder();

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
		veggiesOne = (TextView) findViewById(R.id.veggiesOne);
		veggiesTwo = (TextView) findViewById(R.id.veggiesTwo);
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

				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); // started

				for (int i = 0; i < states.length - 1; i++) { // filling hashmap
					map.put(states[i], baseUrl + states[i]);
					System.out.println("just filled hashmap");
				}

				Iterator<Map.Entry<String, String>> iter = map.entrySet()
						.iterator();

				String testValue = map.get(stringOfItem);

				try {
					System.out.println("inside try for pullVeggies");
					pullVeggies(new URL(testValue));
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

	public void pullVeggies(final URL url) {
		
		System.out.println("inside pullveggies");
		Thread veggieThread = new Thread(new Runnable() {
		
			public void run() {
			
				System.out.println("Inside onPostExecute");
				String start = "<div class=\"state-produce\">";
				String end = "</div></div>";
				String part = builder.substring(builder.indexOf(start)
						+ start.length());
				System.out.println("inside the pull");
				BufferedReader reader = null;

				try {
					try {
						reader = new BufferedReader(new InputStreamReader(url
								.openStream(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						Log.i("showTime", e.getMessage());
					} catch (IOException e) {
						Log.i("showTime", e.getMessage());
					}
					try {
						for (String line; (line = reader.readLine()) != null;) {
							builder.append(line.trim());
						}
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

				String html = part.substring(0, part.indexOf(end));
				html = html.replaceAll("<h3>", " NEW MONTH");
				html = html.replaceAll("</h3>", ", ");
				html = html.replace("- ", "");
				html = html.replaceAll("\\<.*?>", "");
				html = html.replace("Christmas Trees,", "");
				html = html.replace("Wreathes", "");
				html = html.replace(
						"Turkey Bourbon Red, Turkey Standard Bronze", "Turkey");
				html = html.replace("Oysters,", "Oysters -");
				String[] monthList = html.split("NEW MONTH");
				String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
				SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000,
						ids[0]);
				Calendar calendar = new GregorianCalendar(pdt);
				if (calendar.get(Calendar.MONTH) == 0) {
					veggiesOne.setText(monthList[1]);
					veggiesTwo.setText(monthList[2]);
				} else if (calendar.get(Calendar.MONTH) == 1) {
					veggiesOne.setText(monthList[3]);
					veggiesTwo.setText(monthList[4]);
				} else if (calendar.get(Calendar.MONTH) == 2) {
					veggiesOne.setText(monthList[5]);
					veggiesTwo.setText(monthList[6]);
				} else if (calendar.get(Calendar.MONTH) == 3) {
					veggiesOne.setText(monthList[7]);
					veggiesTwo.setText(monthList[8]);
				} else if (calendar.get(Calendar.MONTH) == 4) {
					veggiesOne.setText(monthList[9]);
					veggiesTwo.setText(monthList[10]);
				} else if (calendar.get(Calendar.MONTH) == 5) {
					veggiesOne.setText(monthList[11]);
					veggiesTwo.setText(monthList[12]);
				} else if (calendar.get(Calendar.MONTH) == 6) {
					veggiesOne.setText(monthList[13]);
					veggiesTwo.setText(monthList[14]);
				} else if (calendar.get(Calendar.MONTH) == 7) {
					veggiesOne.setText(monthList[15]);
					veggiesTwo.setText(monthList[16]);
				} else if (calendar.get(Calendar.MONTH) == 8) {
					veggiesOne.setText(monthList[17]);
					veggiesTwo.setText(monthList[18]);
				} else if (calendar.get(Calendar.MONTH) == 9) {
					veggiesOne.setText(monthList[19]);
					veggiesTwo.setText(monthList[20]);
				} else if (calendar.get(Calendar.MONTH) == 10) {
					System.out.println("inside november");
					veggiesOne.setText(monthList[21]);
					veggiesTwo.setText(monthList[22]);
				} else if (calendar.get(Calendar.MONTH) == 11) {
					veggiesOne.setText(monthList[23]);
					veggiesTwo.setText(monthList[24]);
				}
			}
		
		});
		veggieThread.start();
	}
}
