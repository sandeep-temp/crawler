package com.sk.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.sk.crawler.FactualResponse.Data;

@SuppressWarnings("deprecation")
public class Crawler {

	private static final List<String> cityList = new ArrayList<String>();

	// Map of city and its corresponding attractions.
	private static final Map<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
	static {

		// List of cities to look for. Could change how this input is being taken
		cityList.add("DELHI");
		cityList.add("MUMBAI");
		cityList.add("GOA");
		cityList.add("BANGALORE");
		cityList.add("CHENNAI");
		cityList.add("PUNE");
		cityList.add("SRINAGAR");

		for (String city : cityList) {
			map.put(city.toLowerCase(), new HashSet<String>());
		}
	}

	public static void main(String[] args) {

		String query = "{\"$and\":[{\"country\":{\"$eq\":\"IN\"}},{\"locality\":{\"$in\":"
				+ getCityString()
				+ "}},{\"category_labels\":{\"$includes_any\":[\"Tourist\",\"[\\\"LANDMARKS\\\"]\"]}}]}";

		String filters = URLEncoder.encode(query);

		try {
			int distinctTouristPlaces = 0;
			int totalResponseCount = 1;
			int retrievedResponseCount = 0;
			int offSet = 0;
			while (totalResponseCount > retrievedResponseCount) {

				String response = getResponseForFilterAndOffset(filters, offSet);

				Gson gson = new Gson();
				FactualResponse fr = gson.fromJson(response,
						FactualResponse.class);

				if (fr != null) {
					totalResponseCount = fr.getResponse().getTotal_row_count();
					if (fr.getResponse().getData() != null
							&& fr.getResponse().getData().size() != 0) {
						for (Data data : fr.getResponse().getData()) {
							HashSet<String> tempList = map.get(data
									.getLocality().toLowerCase());
							tempList.add(data.getName());
							retrievedResponseCount++;
						}
					}
				}

				offSet = offSet + 50;
			}

			for (Entry<String, HashSet<String>> entry : map.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
				distinctTouristPlaces = distinctTouristPlaces
						+ entry.getValue().size();
			}
			System.out.println("Extracted tourist places:"
					+ distinctTouristPlaces);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This gets response for a given set of cities in the filter and a given
	 * offset.
	 */
	private static String getResponseForFilterAndOffset(String filters,
			int offset) {
		BufferedReader in = null;
		try {
			URL obj = new URL("http://www.factual.com/api/t/places?"
					+ "include_count=t&limit=50&offset=" + offset + "&filters="
					+ filters);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:37.0) Gecko/20100101 Firefox/37.0");
			con.setRequestProperty("Accept",
					"application/json, text/javascript, */*; q=0.01");
			con.setRequestProperty("Accept-Encoding", "utf-8");
			// This might expire. We'll have to fix this accordingly.
			con.setRequestProperty("X-CSRF-Token",
					"Vwy0BMRuR35xTmzqWBtbSauVzBMaMYw87YJi4dUi9oc=");
			con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			con.setRequestProperty("Referer",
					"http://www.factual.com/data/t/places");
			con.setRequestProperty(
					"Cookie",
					"_www_session=RWs2Y0IrbDVzY0R6YVRBQmU2ZGF2SnhLaWVCWlUvb3ZwK1NjMUsxNUF5bm5QeDVTc3Y1T1FMcjI3dnNTL3FTOC9MMlZYSDRhMkcxUkRlZ0FaMTRrbVRIZUZHbDlCa29sYUtqVEdQVnFRd0RFT3JmcGJnMS9TN2FJL2h3cVJVU0JtaWN0ck1ZQldYR1BZOWFXQmhDWW5veTlldXE4VS9vN0dKdzRIdFZNRW80Q2ZjbnZJb0dOQzM4MlBOb05uTkdHZVpxdU5CdERuSFlTTjZESE52UWhocUtJQWdUS3BaVU9qZnllS3E1SG5OR1ZHQ2JHZTdrWXhTRlB6dFJFN0g0SS0teGE4TmV0K0JKT1ZNYm4rQXVMd0d2Zz09--ebf02c998301661bbccb546e2b6033a4572b81f2; __utma=205848972.1611220917.1430401654.1430401654.1430405097.2; __utmc=205848972; __utmz=205848972.1430401654.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); timezone_offset=-330; __utmb=205848972.7.10.1430405097");

			in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			
			return response.toString();
		} catch (Exception e) {
			// do nothing for now
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// do nothing
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Formats the city list in the required format.
	 */
	private static String getCityString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < cityList.size(); i++) {
			if (i == (cityList.size() - 1)) {
				sb.append("\"" + cityList.get(i) + "\"");
			} else {
				sb.append("\"" + cityList.get(i) + "\",");
			}
		}

		sb.append("]");
		return sb.toString();
	}
}
