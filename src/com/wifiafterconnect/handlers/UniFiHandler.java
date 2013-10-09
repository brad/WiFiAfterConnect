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

package com.wifiafterconnect.handlers;

import java.net.URL;

import com.wifiafterconnect.WifiAuthParams;
import com.wifiafterconnect.html.HtmlForm;
import com.wifiafterconnect.html.HtmlPage;

public class UniFiHandler extends CaptivePageHandler {

	/*
	 * http://community.ubnt.com/unifi
	 * Example page : http://community.ubnt.com/t5/UniFi/Payment-Page-iDevices-Still-not-selecting-right-item/td-p/527891
	 * This portal includes options for paying, we will only support the free variations for obvious reasons.
	 * Tricky part is that it has 2 forms. Once button is clicked on first form the second form with TOU becomes visible
	 * which has checkbox for accepting TOU.
	 * 
	 */
	public UniFiHandler(URL url, HtmlPage page) {
		super(url, page);
	}

	@Override
	public boolean checkParamsMissing(WifiAuthParams params) {
		return checkUsernamePasswordMissing (params, page.getForm(0));
	}

	@Override
	public String getPostData(WifiAuthParams params) {
		HtmlForm form = page.getForm(0);
		if (form != null) {
			form.fillInputs(params);
			return form.formatPostData();
		}
		return null;
	}
}