/*
 * Copyright (C) 2013 Sasha Vasko <sasha at aftercode dot net> 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wifiafterconnect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import com.wifiafterconnect.util.Logger;

import android.content.Context;

public class URLRedirectChecker {
	private URL urlToCheckHttp;
	@SuppressWarnings("unused")
	private URL urlToCheckHttps;

	public enum AuthorizationType {
		None, IfNeeded, Force;
	}
	
	public AuthorizationType defaultType = AuthorizationType.IfNeeded;
	
	private Context context = null;
	private Logger logger;
	
	private void InitUrls () {
		if (context == null) {
			try {
				urlToCheckHttp = new URL(Constants.URL_TO_CHECK_HTTP);
				urlToCheckHttps = new URL(Constants.URL_TO_CHECK_HTTPS);
			} catch (MalformedURLException e) {
				logger.exception (e);
			}
		}else {
			urlToCheckHttp = SettingsActivity.getUrlToCheckHttp(context);
			urlToCheckHttps = SettingsActivity.getUrlToCheckHttps(context);
		}
			
	}
	
	public URLRedirectChecker(Logger logger, Context context) {
		this.logger = logger;
		this.context = context;
		
		InitUrls ();
	}
	
	public URLRedirectChecker(String tag, Context context) {
		this.logger = new Logger (tag == null ? "URLRedirectChecker" : tag);
		this.context = context;
		InitUrls ();
	}
	
	public boolean attemptAuthorization (URL url, ParsedHttpInput parsedPage) {
		WifiAuthenticator auth = new WifiAuthenticator (context, logger);
		return auth.attemptAuthorization (url, parsedPage, null);
	}
	

	public void setSaveLogFile (URL url) {
		if (SettingsActivity.getSaveLogToFile(context)) {
			File saveDir = SettingsActivity.getSaveLogLocation(context);
			try {
				logger.setLogFile(new File (saveDir, (url == null ? "probing" : url.getHost()) + ".log"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* ###################################################### 
	 * The Captive Portal check code from android. Unlike them,
	 * we actually need the portal page, so that we can post a response.
	 * 
	 * Copyright (C) 2012 The Android Open Source Project
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 */
	private static final int SOCKET_TIMEOUT_MS = 10000;
	private static final String DEFAULT_SERVER = "clients3.google.com";

	public boolean isCaptivePortal(InetAddress server) {
        HttpURLConnection urlConnection = null;
        //if (!mIsCaptivePortalCheckEnabled) return false;

        String url_string = "http://" + server.getHostAddress() + "/generate_204";
        //if (DBG) log("Checking " + url_string);
        try {
            URL url = new URL(url_string);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(SOCKET_TIMEOUT_MS);
            urlConnection.setReadTimeout(SOCKET_TIMEOUT_MS);
            urlConnection.setUseCaches(false);
            urlConnection.getInputStream();
            // we got a valid response, but not from the real google
            return urlConnection.getResponseCode() != 204;
        } catch (IOException e) {
            //if (DBG) log("Probably not a portal: exception " + e);
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

	public InetAddress lookupHost(String hostname) {
        InetAddress inetAddress[];
        try {
            inetAddress = InetAddress.getAllByName(hostname);
        } catch (UnknownHostException e) {
            return null;
        }

        for (InetAddress a : inetAddress) {
            if (a instanceof Inet4Address) return a;
        }
        return null;
    }
	
	/* ======================================================
	 * End of the The Captive Portal check code from android
	 */

	public boolean isCaptivePortal(String server_hostname) {
		InetAddress server = lookupHost (server_hostname == null ? DEFAULT_SERVER : server_hostname);
		return isCaptivePortal (server);
	}
	
	public boolean checkHttpConnection (URL url, AuthorizationType doAuthorize) {
		//if (proto < 0 || proto >= protocols.length)			return false;
		//String protocol = protocols[proto];
		boolean success = false;
		if (url == null)
			url = urlToCheckHttp;
		
		HttpURLConnection conn = null;
		try {
			
			//URL url = new URL(protocol + "://www.google.com");
			logger.debug("Trying [" + url + "]");
			ParsedHttpInput parsed = null;
			
			// due to switching to wifi, name resolution can fail if the timing in just right,
			// give it another chance
			for ( int i = 0; i < 2 && parsed == null ; ++i ) {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(Constants.SOCKET_TIMEOUT_MS);
				conn.setReadTimeout(Constants.SOCKET_TIMEOUT_MS);
				conn.setUseCaches(false);

				if ((parsed = ParsedHttpInput.receive (logger, conn)) == null) {
					try {	Thread.sleep(100); } catch (InterruptedException e) {} // don't care
				}
			}
			if (parsed == null)
				return false;
			
		    String field = null;
		    URL redirectUrl = null;
		    
		    if (doAuthorize == AuthorizationType.Force) {
		    	success = attemptAuthorization (conn.getURL(), parsed);
		    }else if (!url.getHost().equals(conn.getURL().getHost())) {
		        // we were redirected! Kick the user out to the browser to sign on?
		    	logger.debug("Redirected to  [" + conn.getURL() + "]");
		    	if (doAuthorize != AuthorizationType.None) {
		    		setSaveLogFile (conn.getURL());
		    		success = attemptAuthorization (conn.getURL(), parsed);
		    	}
		    }else if ((field = conn.getHeaderField("Location")) != null){
		    	redirectUrl = new URL (field);
		    }else if (parsed.hasMetaRefresh()) {
		    	redirectUrl = parsed.getMetaRefreshURL();
		    }else
		    	success = true;

		    if (redirectUrl != null) {
		    	if (!redirectUrl.getHost().equals(conn.getURL().getHost())) {
		    		logger.debug("Redirected to  [" + redirectUrl + "]. Explicit handling needed.");
		    		setSaveLogFile (redirectUrl);
		    		if (!redirectUrl.getProtocol().equals(url.getProtocol())) {
		    			logger.debug("protocol has changed!");
		    		}
		    		if (doAuthorize != AuthorizationType.None)
		    			success = checkHttpConnection (redirectUrl, AuthorizationType.Force);
		    	} else {
			    	// something wicked happened otherwise
		    		logger.error("Unexpected redirect URL  [" + redirectUrl + "] - giving up.");
		    	}
		    }
		} catch (MalformedURLException e){
    		logger.error("Redirected to a malformed url ");
    		logger.exception (e);
		} catch (IOException e) {
			logger.exception (e);
    	} finally {
			if (conn != null)
				conn.disconnect();
		}
		return success;
	}

	
	public void setDefaultType (AuthorizationType type) {
		defaultType = type;
	}
	
	public boolean checkHttpConnection () {
		boolean success = checkHttpConnection (urlToCheckHttp, defaultType);
		logger.debug("Internet connection is " + (success ? "Available" : "Blocked by Captive portal"));
		return success;
	}

	public boolean checkHttpConnection (AuthorizationType authType) {
		boolean success = checkHttpConnection (urlToCheckHttp, authType);
		logger.debug("Internet connection is " + (success ? "Available" : "Blocked by Captive portal"));
		return success;
	}
	
}