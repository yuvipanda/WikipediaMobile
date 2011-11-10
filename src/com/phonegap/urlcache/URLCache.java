package com.phonegap.urlcache;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;

public final class URLCache extends Plugin {

	public static String TAG = "URLCache";

	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {

		PluginResult.Status status = PluginResult.Status.OK;
		JSONObject result = new JSONObject();

		String uri = null;

		try {
			uri = args.getString(0);

			if(uri != null && action.equals("getCachedPage")) {
				try {
					String filePath = updateDocumentResource(uri);
					result.put("file", filePath);
					result.put("status", 0);

				} catch(MalformedURLException e) {
					status = PluginResult.Status.MALFORMED_URL_EXCEPTION;
					result.put("message", "MalformedURLException "+uri);
					result.put("status", status.ordinal());
				} catch(IOException e) {
					e.printStackTrace();
					status = PluginResult.Status.IO_EXCEPTION;
					result.put("message", "IOException "+uri);
					result.put("status", status.ordinal());
				} catch(SAXException e) {
					status = PluginResult.Status.ERROR;
					result.put("message", "SAXException "+uri);
					result.put("status", status.ordinal());
				} catch(ParserConfigurationException e) {
					status = PluginResult.Status.ERROR;
					result.put("message", "SAXException "+uri);
					result.put("status", status.ordinal());
				}
			}
			else {
				status = PluginResult.Status.INVALID_ACTION;
				result.put("message", "InvalidAction");
				result.put("status", status.ordinal());
			}
		} catch (JSONException e1) {
			status = PluginResult.Status.JSON_EXCEPTION;
			try {
				result.put("message", "JSONException "+uri);
				result.put("status", status.ordinal());
			} catch(JSONException e2) {
				// very bad if this happens
				e2.printStackTrace();
			}
		}
		//Log.d(TAG, result.toString());
		return new PluginResult(status, result);

	}

	private String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String getFileName(String url) {
		// First check if the file exists already
		String fileName = md5(url);
		String fileDir = ctx.getFilesDir().getAbsolutePath();
		String filePath = fileDir + "/" + fileName;

		//Log.d(TAG, "URI: " + uri + " filePath: " + filePath);
		return filePath;
	}
	
	public String getCachedPathForURI(String url) throws MalformedURLException, IOException {
		String filePath = getFileName(url);
		File f = new File(filePath);
		if (f.exists()) {
			return filePath;
		}
		//Log.d(TAG, "Fetching from server " + uri);
		URL u;
		DataInputStream dis = null;
		FileOutputStream out = null;
		byte[] buffer = new byte[1024];
		int length = -1;
		u = new URL(url);
		URLConnection urlConnection = u.openConnection();
		urlConnection.setRequestProperty("Application_Version", "Wikipedia Mobile/1.0.0");
		dis = new DataInputStream(new BufferedInputStream(urlConnection.getInputStream(), 8192));
		out = ctx.openFileOutput(f.getName(), Context.MODE_PRIVATE);
		while ((length = dis.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		out.flush();
		dis.close();
		out.close();
		return filePath;
	}
	
	public String updateDocumentResource(String url) throws IOException,
			ParserConfigurationException, SAXException {
		// check if page already exists
		// TODO check how long ago as well
		String filePath = getFileName(url);
		File f = new File(filePath);
		if (f.exists()) {
			return filePath;
		}
		URL uri = new URL(url);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(uri.openStream());
		
		doc.getDocumentElement().normalize();
		
		NodeList nodeList = doc.getElementsByTagName("img");
		
		// images
		for(int i = 0, j = nodeList.getLength() ; i < j ; i++) {
			Element em = (Element) nodeList.item(i);
			String oldSrc = em.getAttribute("src");
			if(!oldSrc.equals("")) {
				em.setAttribute("src", getCachedPathForURI("http:"+oldSrc));
			}
			Log.d(TAG, "image source "+em.getAttribute("src"));
		}
		
		// script
		nodeList = doc.getElementsByTagName("script");
		
		for(int i = 0, j = nodeList.getLength() ; i < j ; i++) {
			Element em = (Element) nodeList.item(i);
			String oldSrc = em.getAttribute("src");
			if(!oldSrc.equals("")) {
				em.setAttribute("src", getCachedPathForURI("http:"+oldSrc));
			}
			Log.d(TAG, "script source "+em.getAttribute("src"));
		}
		
		// links
		nodeList = doc.getElementsByTagName("link");
		
		for(int i = 0, j = nodeList.getLength() ; i < j ; i++) {
			Element em = (Element) nodeList.item(i);
			String oldHref = em.getAttribute("href");
			if(!oldHref.equals("")) {
				em.setAttribute("href", getCachedPathForURI("http:"+oldHref));
			}
			Log.d(TAG, "link source "+em.getAttribute("href"));
		}
		
		// anchor links
		nodeList = doc.getElementsByTagName("a");
		
		for(int i = 0, j = nodeList.getLength() ; i < j ; i++) {
			Element em = (Element) nodeList.item(i);
			String oldHref = em.getAttribute("href");
			// FIXME this should change to a user-set variable
			if(!oldHref.equals("") && !oldHref.startsWith("//") && oldHref.startsWith("/")) {
				em.setAttribute("href", "http://en.m.wikipedia.org"+oldHref);
			}
			Log.d(TAG, "a href source "+em.getAttribute("href"));
		}
		
		writeHtmlFile(doc, filePath);
		
		return filePath;
	}
	
	// This method writes a DOM document to a file
	public void writeHtmlFile(Document doc, String filename) {
	    try {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource(doc);

	        // Prepare the output file
	        File file = new File(filename);
	        Result result = new StreamResult(file);

	        // Write the DOM document to the file
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	        Log.d(TAG, filename);
	    } catch (TransformerConfigurationException e) {
	    	e.printStackTrace();
	    } catch (TransformerException e) {
	    	e.printStackTrace();
	    }
	}
}
