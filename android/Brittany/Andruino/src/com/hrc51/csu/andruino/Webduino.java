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
	
	public boolean login() {
	       URL url;
	       HttpURLConnection urlConnection;
	       String username = serverSettings.getString("username", "matt");
	       String password = serverSettings.getString("password", "password");
	       
	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/" 
	        			   +username+"/"+password);
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/" 
	        			   +username+"/"+password);

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   
	    	   // setting cookies
	    	   urlConnection.setRequestProperty("cookie", "username="+username+"; password="+password);
	    	   urlConnection.connect();
	    	   
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   in.close();
	    	   urlConnection.disconnect();

	    	   if (jp.getResponse().equals("pass"))
	    		   return true;
	    	   else
	    		   return false;
	       }
	       catch (MalformedURLException e) {
	    	   return false;
	       }
	       catch (IOException e) {
	    	   return false;
	       }
	}
	
	public boolean logout() {
	       URL url;
	       HttpURLConnection urlConnection;

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/logout");
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/logout");

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   in.close();
	    	   urlConnection.disconnect();

	    	   if (jp.getResponse().equals("pass"))
	    		   return true;
	    	   else
	    		   return false;
	       }
	       catch (MalformedURLException e) {
	    	   return false;
	       }
	       catch (IOException e) {
	    	   return false;
	       }
	}
	
	public String index() {
	       URL url;
	       HttpURLConnection urlConnection;
	       String response;

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/");
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/");

	    	   urlConnection = (HttpURLConnection) url.openConnection();
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
	       ArrayList<AndruinoObj> alError = new ArrayList<AndruinoObj>();
	       String username;
	       String password;
	       
	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/read");	    		   
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/read");

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   
	    	   //getting cookies from server
	    	    for (int i=0; ; i++) {
	    	        String headerName = urlConnection.getHeaderFieldKey(i);
	    	        String headerValue = urlConnection.getHeaderField(i);

	    	        if (headerName == null && headerValue == null) {
	    	            // No more headers
	    	            break;
	    	        }
	    	        if ("Set-Cookie".equalsIgnoreCase(headerName)) {
	    	            // Parse cookies
	    	            String[] fields = headerValue.split(";\\s*");

	    	            // Parse each field
	    	            for (int j=1; j<fields.length; j++) {
	    	            	if(fields[j].contains("username"))
	    	            	{
	    	            		username = fields[j].substring(fields[j].indexOf("=") + 1, fields[j].length());
	    	            	}
	    	            	else if(fields[j].contains("password"))
	    	            	{
	    	            		password = fields[j].substring(fields[j].indexOf("=") + 1, fields[j].length());
	    	            	}
	    	            }

	    	            // Save the cookie...
	    	        }
	    	    }
	    	   
	    	   
	    	   
	    	   urlConnection.connect();

	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   	    	   
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   return jp.getDetails();
	       }
	       catch (MalformedURLException e) {
	    	   AndruinoObj errObj = new AndruinoObj(0,0,"URL Error",e.toString(),0,0,0,0,"");
	    	   alError.add(errObj);
	    	   return(alError);
	       }
	       catch (IOException e) {
	    	   AndruinoObj errObj = new AndruinoObj(0,0,"IO Error",e.toString(),0,0,0,0,"");
	    	   alError.add(errObj);
	    	   return(alError);
	       }
	}
	
	public boolean write(int did, int value) {
	       URL url;
	       HttpURLConnection urlConnection;

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/write/"+did+"/"+value);
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/write/"+did+"/"+value);

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   
	    	   if (jp.getResponse().equals("pass"))
	    		   return true;
	    	   else
	    		   return false;
	       }
	       catch (MalformedURLException e) {
	    	   return false;
	       }
	       catch (IOException e) {
	    	   return false;
	       }
	}

	public boolean enable(boolean isEnabled, int did) {
	       URL url;
	       HttpURLConnection urlConnection;
    	   String action = isEnabled ? "enable" : "disable";

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/"+action+"/pin/"+did);
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/"+action+"/pin/"+did);

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   
	    	   if (jp.getResponse().equals("pass"))
	    		   return true;
	    	   else
	    			   return false;
	       }
	       catch (MalformedURLException e) {
	    	   return false;
	       }
	       catch (IOException e) {
	    	   return false;
	       }
	}
	
	public boolean setLabel(int did, String newLabel) {
	       URL url;
	       HttpURLConnection urlConnection;

	       try {
	    	   if (serverSettings.getBoolean("usessl", false))
	        	   url = new URL("https://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	        			   +":"+serverSettings.getString("serverport", "8080")+"/setlabel/pin/"+did+"/"
	        			   +newLabel);
	    	   else
	    		   url = new URL("http://"+serverSettings.getString("serverurl", "csu.hrc51.com")
	    				   +":"+serverSettings.getString("serverport", "8080")+"/setlabel/pin/"+did+"/"
	    				   +newLabel);

	    	   urlConnection = (HttpURLConnection) url.openConnection();
	    	   BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    	   String inputLine;
	    	   inputLine = in.readLine();
	    	   jp = new JsonParser(inputLine);
	    	   in.close();
	    	   urlConnection.disconnect();
	    	   
	    	   if (jp.getResponse().equals("pass"))
	    		   return true;
	    	   else
	    			   return false;
	       }
	       catch (MalformedURLException e) {
	    	   return false;
	       }
	       catch (IOException e) {
	    	   return false;
	       }
	}
}
