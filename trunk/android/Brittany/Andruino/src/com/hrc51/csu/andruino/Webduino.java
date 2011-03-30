package com.hrc51.csu.andruino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.content.SharedPreferences;

public class Webduino {
	SharedPreferences serverSettings;
	JsonParser jp;

	public Webduino(SharedPreferences settings) {
		serverSettings = settings;
	}
	
	public String index() {
	       URL url;
	       HttpURLConnection urlConnection;
	       String response;

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "n/a")+":"+serverSettings.getString("serverport", "n/a")+"/");
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "n/a")+":"+serverSettings.getString("serverport", "n/a")+"/");
	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   //BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   response = "Command: " + jp.getCommand() + "\n";
	    	   response += "Response: " + jp.getResponse() + "\n";
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   return response;
	       }
	       catch (MalformedURLException e) {
	    	   return("url Error");
	       }
	       catch (IOException e) {
	    	   return("IO Error: "+e);
	       }
	}
	public ArrayList<AndruinoObj> read() {
	       URL url;
	       HttpURLConnection urlConnection;
	       //ArrayList<String> alError = new ArrayList<String>();

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   //url = new URL("https://"+serverSettings.getString("serverurl", "n/a")+":"+serverSettings.getString("serverport", "n/a")+"/read");
	    		   url = new URL("https://csu.hrc51.com:8080/read");

	    	   else
	    		   //url = new URL("http://"+serverSettings.getString("serverurl", "n/a")+":"+serverSettings.getString("serverport", "n/a")+"/read");
	    		   url = new URL("http://csu.hrc51.com:8080/read");

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   //BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   return jp.getDetails();
	       }
	       catch (MalformedURLException e) {
//	    	   alError.add("url Error" +e);
//	    	   return(alError);
	    	   return null;
	       }
	       catch (IOException e) {
//	    	   alError.add("IO Error: "+e);
//	    	   return(alError);
	    	   return null;
	       }
	}
}