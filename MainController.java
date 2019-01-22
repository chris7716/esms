package com.esms.server.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import com.esms.server.models.BulkRequest;
import com.esms.server.models.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;


@RestController
@CrossOrigin
public class MainController {
	
	static Logger logger = Logger.getLogger(MainController.class.getClass());
	
	@RequestMapping(value="/home")
	public Version getVersion(){
		
		Version version = new Version();
		version.setVersion("1.0");
		version.setTitle("Initial Version");
		
		return version;
	}
	
	@RequestMapping(value="/send/single", method = RequestMethod.GET)
	public String[] sendSingleMessage(@RequestParam(name="src") String src,
			@RequestParam(name="dst") String dst,
			@RequestParam(name="username") String username,
			@RequestParam(name="password") String password,
			@RequestParam(name="msg") String msg,
			@RequestParam(name="dr", required = false) String dr) throws IOException{
		
		
		logger.info("Request Recieved ===========");
		
		String[] responseInfo = new String[2];
		 
		
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 15000);
			HttpConnectionParams.setSoTimeout(params, 1500000);

			HttpClient httpclient = new DefaultHttpClient(params);
			HttpPost httppost = new HttpPost("http://122.255.29.68:5000/sms/send_sms.php?");
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("src", src));
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("dst", dst));
			nameValuePairs.add(new BasicNameValuePair("msg", msg ));
			nameValuePairs.add(new BasicNameValuePair("dr", "1"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			responseInfo[0] = String.valueOf(statusCode);
						
			responseInfo[1] = convertInputStreamToString(response.getEntity().getContent()).toString();
	
			//System.out.println("response-match: " + responseStr);
			System.out.println("status code-match : "
					+ response.getStatusLine().getStatusCode());
			
		} catch (Exception e) {
			responseInfo[0] = "exception";
			responseInfo[1] = e.getLocalizedMessage();
			logger.info(e);
			logger.info("Exception Occured " + e);
		}
		
		
		logger.info("Request Completed ===========");
		return responseInfo;
        //assertEquals("status code incorrect", status, 200);
	}
	
	@RequestMapping(value = "/sms/bulk", method = RequestMethod.POST)
	public void sendBulkMessages(@RequestBody BulkRequest request) throws Exception {
		
		
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        inputStream.close();

        return result;

    }
	
}
