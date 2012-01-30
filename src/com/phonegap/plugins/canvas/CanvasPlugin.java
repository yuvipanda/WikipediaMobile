/**
 * CanvasPlugin - Adds support on Android for toDataURL(type) on the HTML <canvas> tag
 * Usage: window.plugins.canvas.toDataURL(canvasElement, "image/png", success, failure);
 * Valid mime types: image/png, image/jpeg
 * Limitations: JPEG quality level hard-coded in this class
 */
package com.phonegap.plugins.canvas;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.phonegap.api.Plugin;
import com.phonegap.api.PluginResult;
import com.phonegap.api.PluginResult.Status;
import android.webkit.WebView;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;

import android.util.Base64;

/**
 * @author Ryan Gillespie
 * @website http://ryangillespie.com
 * @author Jared Sheets - Screen Density Fix
 *
 */
public class CanvasPlugin extends Plugin {
	
	public static final String ACTION = "toDataURL";
	
	public static final String MIME_PNG = "image/png";
	public static final String MIME_JPEG = "image/jpeg";

	private WebView mAppView;
	private Bitmap bmp;
	private JSONObject imgData = new JSONObject();
	private DisplayMetrics metrics = new DisplayMetrics();

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId) {

		mAppView = this.webView;

		PluginResult result = null;
		String mimeType = "image/png";
		int xpos = 0;
		int ypos = 0;
		int width = 0;
		int height = 0;
		int screenWidth = 0;

		if (action.equals("toDataURL")) {
			try {
				mimeType = data.getString(0);
				xpos = data.getInt(1);
				ypos = data.getInt(2);
				width = data.getInt(3);
				height = data.getInt(4);
				screenWidth = data.getInt(5);

				try {
					imgData.put("data", getImageData(mimeType, xpos, ypos, width, height, screenWidth));
					imgData.put("size", imgData.getString("data").length());
				} catch (Exception e) {
					imgData.put("debug", e);
				}
				result = new PluginResult(PluginResult.Status.OK, imgData);

			} catch (JSONException jsonEx) {
				result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
			}

		}

		return result;

	}

	private String getImageData(String mimeType, int xpos, int ypos, int width, int height, int screenWidth) throws JSONException {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	String encodedImg = "";
    	
    	//get the density of the screen (low = 0.75, medium = 1, high = 1.5)
    	float appScale = mAppView.getScale();
    	// System.out.println("AppScale: " + appScale);
    	
    	/* Draw the entire WebView - then crop the bitmap to the x,y & width,height of the canvas position in the WebView */
    	/* Use Javascript to get the full width of the WebView - no WebView.getContentWidth() */

    	try {    		
    		int webViewHeight = mAppView.getContentHeight();
    		int webViewWidth = (screenWidth == 0) ? mAppView.getWidth() : screenWidth;
    		
    		//scale the webView dimensions by the screen density
    		int scaledViewHeight = (int)(webViewHeight * appScale);
    		int scaledViewWidth = (int)(webViewWidth * appScale);
    		
    		bmp = Bitmap.createBitmap(scaledViewWidth, scaledViewHeight, Bitmap.Config.RGB_565);
    		
    		System.out.println("App Dimensions: " + webViewWidth + " x " + webViewHeight);
    		System.out.println("Scaled Dimensions: " + scaledViewWidth + " x " + scaledViewHeight);
    		
    		//scale the canvas coordinates by the screen density
    		int scaledXpos = (int)(xpos * appScale);
    		int scaledYpos = (int)(ypos * appScale);
    		int scaledWidth = (int)(width * appScale);
    		int scaledHeight = (int)(height * appScale);
    		
    		mAppView.draw(new Canvas(bmp));
    		bmp = Bitmap.createBitmap(bmp, scaledXpos, scaledYpos, scaledWidth, scaledHeight);

	    	if (mimeType.equals(MIME_PNG)) {
	    		System.out.println("!! MimeType = PNG");
	        	bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			} else if (mimeType.equals(MIME_JPEG)) {
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} else {
				throw new Exception("Unsupported mime type: " + mimeType);
			}
	        byte[] b = out.toByteArray();
	        
			byte[] tmp = Base64.encode(b, Base64.DEFAULT);
			encodedImg = new String(tmp);
			encodedImg = "data:" + mimeType + ";base64," + encodedImg;

    	} catch (Exception e) {
    		try {
    			imgData.put("debug", e);
    		} catch (JSONException jx) {
    			; // nothing
    		}
    	}
		
    	return encodedImg;
	}
}