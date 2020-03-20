package com.cerner.vbrick.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
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

import com.opencsv.CSVWriter;

public class list 
{
	public static String accessToken = null;
	public static String token = null;

	public static void main(String[] args) throws IOException,ParseException //generateToken() throws Exception 
	{
		list data = new list();
		token = data.generateAccessToken();
		System.out.println("Token:"+token);
		HashMap<String, String> at = getEventslist(accessToken);
		getAttendeeList(accessToken,at);
	}

	public String generateAccessToken() throws IOException, ParseException
	{
		String postEndpoint = "https://cerner.rev.vbrick.com/api/v2/user/login";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(postEndpoint); 
		httpPost.setHeader("Accept","application/json"); 
		httpPost.setHeader("Content-Type", "application/json");

		String inputJson = "{\n" + "  \"username\": \"SA050728\",\n" +
				"  \"password\": \"Enter your password\",\n" + "}";

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

		httpclient.close();
		return accessToken;

	}

	public static HashMap<String, String> getEventslist(String token) throws ClientProtocolException, IOException
	{
		HashMap<String,String> map = new HashMap<>();
		try
		{
			String bearerToken="VBrick "+ token;
			HttpClient client = HttpClientBuilder.create().build();

			HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/?after=2018-01-01&before=2020-03-21");
			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");

			HttpResponse response=client.execute(request);
			System.out.println("Response Code:" + response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity());
			System.out.println(json);
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
				if(obj.get("id") != null && obj.get("title") != null)
				{
					map.put(obj.get("id").toString(), obj.get("title").toString());
				}
			}

			File file = new File("2018-2020-EventsList.csv"); 
			String csv = CDL.toString(arr);
			FileUtils.writeStringToFile(file, csv);
			System.out.println(map.size());
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			return map;
		}
		return map;
	}

	public static List<String> getAttendeeList(String token, HashMap<String,String> map) throws ClientProtocolException, IOException
	{
		List<String> e = new ArrayList<String>();
		String bearerToken="VBrick "+ token;
		//System.out.println(map);

		//try 
		//{
		for(Map.Entry<String, String> entry : map.entrySet())
		{
			CloseableHttpClient client = HttpClients.createDefault();
			HttpGet request=new HttpGet("https://cerner.rev.vbrick.com/api/v2/scheduled-events/" +entry.getKey()+ "/report");

			System.out.println("request:"+request.getURI());
			String list = request.getURI().toString();

			//FileWriter writer = new FileWriter("2018-Q1-EventList.csv");
			//writer.append(list);

			request.addHeader("Authorization",bearerToken);		//Adding the bearer token
			request.addHeader("Content-Type", "application/json");

			CloseableHttpResponse response=client.execute(request);

			try
			{
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);

				
				
				String json = EntityUtils.toString(entity);
				System.out.println(json);
				
				
				/*
					JSONArray arr = new JSONArray(EntityUtils.toString(response.getEntity()));
					JSONObject obj = null;

					File f = new File("AttendeeList.csv");
					String csv = CDL.toString(arr);
					FileUtils.writeStringToFile(f, csv);

				 */
				
			}
			/*
			finally
			{
				response.close();
			}
			*/

			catch(Exception ex)
			{
				//System.out.println(ex.getMessage());
				response.close();
			}
			
		}
		return e;
	}
}