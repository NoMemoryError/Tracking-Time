package com.lab.helper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import com.google.gson.JsonObject;

import android.util.Log;

public class TTClient {
	public static class Param {


		private static String TAG = "TTClient";
		public static List<Param> make(Object... strings) {
			List<Param> result = new ArrayList<TTClient.Param>();

			if (strings.length % 2 != 0) {
				throw new InvalidParameterException();
			}

			for (int i = 0; i < strings.length; i += 2) {
				result.add(new Param(strings[i].toString(), strings[i + 1]));
			}

			return result;
		}

		private String name;
		private Object value;

		public Param(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}
		public static String sendPostRequest(String serverUrl, JsonObject jsonObject)
		{
			ArrayList<NameValuePair> nvp = getPostData(jsonObject.toString());
			System.out.println(nvp.toString());
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(serverUrl);
				System.out.println(new UrlEncodedFormEntity(nvp));
				httppost.setEntity(new UrlEncodedFormEntity(nvp));
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					BufferedInputStream bis = new BufferedInputStream(instream);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					String html = new String(baf.toByteArray());
					Log.d(TAG, html);

					return html;
				}

			} catch (ClientProtocolException e) {
				Log.e(TAG, "There was a protocol based error");
			} catch (IOException e) {
				Log.e(TAG, "There was an IO Stream related error");
			}
			return "";
		}
	}
	private static ArrayList<NameValuePair> getPostData(String data) {
		ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();
		nvp.add(new BasicNameValuePair("data", convertToUTF8(data)));
		return nvp;
	}
	private static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}
}
