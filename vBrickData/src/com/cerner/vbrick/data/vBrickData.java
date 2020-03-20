package com.cerner.vbrick.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class vBrickData 
{
	public static String accessToken = null;
	public static String token = null;
	public static String eventID;

	public static void main(String[] args) throws IOException,ParseException //generateToken() throws Exception 
	{
		vBrickData data = new vBrickData();
		token = data.generateAccessToken();
		System.out.println("Token:"+token);
		//getVideo(accessToken);
	//	getEventslist(accessToken);
		getAttenddeDetails(accessToken);
		
		/*
		HashMap<String, String> m = getEventslist(accessToken);
		if(!m.isEmpty())
		{	
			List<Events> events = getAttendeeList(accessToken,m);
			getAttendeeListforeachEvent(events);
		}
		*/
	}

	public String generateAccessToken() throws IOException, ParseException
	{
		String postEndpoint = "https://cerner.rev.vbrick.com/api/v1/user/login";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(postEndpoint); 
		httpPost.setHeader("Accept","application/json"); 
		httpPost.setHeader("Content-Type", "application/json");

		String inputJson = "{\n" + "  \"username\": \"SA050728\",\n" +
				"  \"password\": \"Manjula24\",\n" + "}";

		StringEntity stringEntity = new StringEntity(inputJson);
		httpPost.setEntity(stringEntity);

		System.out.println("Executing request " + httpPost.getRequestLine());

		HttpResponse response = httpclient.execute(httpPost);

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

		//Throw runtime exception if status code isn't 200 
		if(response.getStatusLine().getStatusCode() != 200)
		{ 
			throw new RuntimeException("Failed : HTTP error code : "+response.getStatusLine().getStatusCode()); }

		//Create the StringBuffer object and store the response into it. 
		StringBuffer result = new StringBuffer(); 
		String line = ""; 
		while ((line = br.readLine())!= null) 
		{
			System.out.println("Response : \n"+result.append(line)); 
		} 

		JSONObject obj = new JSONObject(result.toString());
		accessToken = obj.get("token").toString();

		return accessToken;
	}

	public static String getVideo(String token)
	{
		try
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();

			HttpGet request=new HttpGet("http://cerner.rev.vbrick.com/api/v1/videos/search");	//calling PowerBI API-GetGroups. Lists all the groups in the workspace
			//HttpGet request=new HttpGet("http://cerner.rev.vbrick.com/api/v1/videos/report");
			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");
			HttpResponse response=client.execute(request);
			System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity());
			if(response.getStatusLine().getStatusCode()!=200)
			{
				System.out.println("Failed HTTP response"+response.getStatusLine().getStatusCode()+" "+json);
				return null;	
			}

			JSONObject jsonObject = new JSONObject(json);
			JSONArray values = jsonObject.getJSONArray("videos");
			System.out.println(values.toString());

			File file = new File("VideoList.csv"); 
			String csv = CDL.toString(values);
			FileUtils.writeStringToFile(file, csv); 
			//System.out.println(csv);	 

		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());

		}
		return accessToken;
	}

	public static HashMap<String, String> getEventslist(String token)
	{
		HashMap<String,String> map = new HashMap<>();
		try
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();

			HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/?after=2018-01-01&before=2018-03-31");
			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");

			HttpResponse response=client.execute(request);
			System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity());
			if(response.getStatusLine().getStatusCode()!=200)
			{
				System.out.println("Failed HTTP response"+response.getStatusLine().getStatusCode()+" "+json);
				return null;	
			}

			System.out.println("2018-Q1-EventsList:"+json);	

			JSONArray arr = new JSONArray(json);
			JSONObject obj;

			for(int i=0;i<arr.length();i++)
			{
				obj = arr.getJSONObject(i);
				//System.out.println(obj.get("id"));

				//String key = "EventID";
				String key = obj.get("id").toString();
				String value = null;
				map.put(key, value);
				//System.out.println(map);
			}
			System.out.println(map+"\n");

			/*
			File file = new File("EventsList.csv"); 
			String csv = CDL.toString(arr);
			FileUtils.writeStringToFile(file, csv); 	
			*/
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return map;

		}
		return map;
	}

	/*
	public static List<Events> getAttendeeList(String token,HashMap<String,String> map)
	{
		List<Events> events = new ArrayList<Events>();

		try
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();

			for(Map.Entry<String, String> entry:map.entrySet())
			{
				HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/" +entry.getKey()+ "/report");
				System.out.println("request:"+request.getURI());

				request.addHeader("Authorization",bearerToken);		//Adding the bearer token
				request.addHeader("Content-Type", "application/json");
				request.addHeader("cache-control", "no-cache");

				HttpResponse response=client.execute(request);
				System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

				String json = EntityUtils.toString(response.getEntity());
				if(response.getStatusLine().getStatusCode()!=200)
				{
					System.out.println("Failed HTTP response"+response.getStatusLine().getStatusCode()+" "+json);
					return null;	
				}

				JSONObject obj;    // = new JSONObject(json);
				JSONArray arr = new JSONArray(json);

				Events e = new Events();
				for(int k=0;k<arr.length();k++)
				{
					//Events e = new Events();
					obj=(JSONObject) arr.get(k);
					//e1.setEvent_ID(obj.get("id").toString());
					e.setUserType(obj.getString("userType").toString());
					e.setName(obj.get("name").toString());
					e.setUsername(obj.get("username").toString());
					e.setEmail(obj.get("email").toString());
					e.setipAddress(obj.get("ipAddress").toString());
					e.setBrowser(obj.get("browser").toString());
					e.setDeviceType(obj.get("deviceType").toString());
					e.setZone(obj.get("zone").toString());
					e.setDeviceAccessed(obj.get("deviceAccessed").toString());
					e.setStreamAccessed(obj.get("streamAccessed").toString());
					e.setEnteredDate(obj.get("enteredDate").toString());
					e.setExitedDate(obj.get("exitedDate").toString());
					e.setViewingTime(obj.get("viewingTime").toString());
					events.add(e);
					//System.out.println(e);
				}
				System.out.println(e);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return events;
	}
	*/

	/*
	public static void getAttendeeListforeachEvent(List<Events> events)
	{
		for(Events e : events)
		{
			String userType = e.getUserType();
			System.out.println(userType);
			System.out.println(e.getName());
			System.out.println(e.getUsername());
			System.out.println(e.getEmail());
			System.out.println(e.getipAddress());
			System.out.println(e.getBrowser());
			System.out.println(e.getDeviceType());
			System.out.println(e.getZone());
			System.out.println(e.getDeviceAccessed());
			System.out.println(e.getStreamAccessed());
			System.out.println(e.getEnteredDate());
			System.out.println(e.getExitedDate());
			System.out.println(e.getViewingTime());
			
		}
	}
	*/
	
	//getting event attendee list for a single event.. Passed event ID manually
	public static String getAttenddeDetails(String token)
	{
		try
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();

			HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/a1959367-8493-4f27-98a0-9c4a38526289/report");
			System.out.println("Req:"+request.getURI());
			
			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");
			request.addHeader("cache-control", "no-cache");
			HttpResponse response=client.execute(request);
			
			System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity());
			if(response.getStatusLine().getStatusCode()!=200)
			{
				System.out.println("Failed HTTP response"+response.getStatusLine().getStatusCode()+" "+json);
				return null;	
			}

			System.out.println("Attendee:"+json);

			/*
			JSONArray values = new JSONArray(json);
			JSONObject obj = null;

			for(int i=0;i<values.length();i++)
			{
				obj = values.getJSONObject(i);
			}

			File file = new File("2018-Q1-AttendeeList.csv"); 
			String csv = CDL.toString(values);
			FileUtils.writeStringToFile(file, csv); 
			//System.out.println(csv); 
			 * */
			 
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());

		}
		return accessToken;
	}
}