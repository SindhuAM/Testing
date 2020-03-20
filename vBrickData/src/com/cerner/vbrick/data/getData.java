package com.cerner.vbrick.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

public class getData 
{
	public static String accessToken = null;
	public static String token = null;
	public static List<String> str = new ArrayList<String>();

	public static void main(String[] args) throws IOException,ParseException //generateToken() throws Exception 
	{
		getData data = new getData();
		token = data.generateAccessToken();
		System.out.println("Token:"+token);

		getEventslist(accessToken);
		getAttendeeList(accessToken);
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

	public static Object getEventslist(String token) throws ClientProtocolException, IOException
	{
		String bearerToken="VBrick "+ token;
		HttpClient client = HttpClientBuilder.create().build();

		HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/?after=2018-01-01&before=2020-03-18");
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

		JSONArray arr = new JSONArray(json);
		JSONObject obj = null;
		for(int i=0;i<arr.length();i++)
		{
			obj = (JSONObject) arr.get(i);
			if(obj.get("id") != null)
			{
				str.add(obj.get("id").toString());
			}
		}

		System.out.println(str.size());
		return obj;
	}

	public static void getAttendeeList(String token) throws ClientProtocolException, IOException
	{

		Iterator i = str.iterator();
		while(i.hasNext())
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/" +i.next()+ "/report");
			
			URI httpReq = request.getURI();
			System.out.println(httpReq);
			
			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");
			
			
		}
		
			/*
			HttpResponse response=client.execute(request);
			System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity());

			if(response.getStatusLine().getStatusCode()!=200)
			{
				System.out.println("Failed HTTP response"+response.getStatusLine().getStatusCode()+" "+json);
				//return null;	
			}

			System.out.println(json);
			*/
		//}
	}
}