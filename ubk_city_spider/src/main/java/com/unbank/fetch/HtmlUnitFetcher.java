package com.unbank.fetch;

import java.io.IOException;
import java.net.MalformedURLException;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/***
 * JS加载界面解析Fetcher
 * 
 * @author CJF
 * 
 */
public class HtmlUnitFetcher {

	public static WebClient webClient;
	public static HtmlUnitFetcher htmlUnitFetcher;

	public static HtmlUnitFetcher getinstence() {
		if (htmlUnitFetcher == null) {
			webClient = new WebClient(BrowserVersion.FIREFOX_17);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setTimeout(10 * 1000);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.waitForBackgroundJavaScript(10 * 1000);
			webClient
					.setAjaxController(new NicelyResynchronizingAjaxController());
			htmlUnitFetcher = new HtmlUnitFetcher();
		}
		return htmlUnitFetcher;
	}

	public String get(String url) {
		HtmlPage page = null;
		try {
			page = webClient.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = page.asXml();
		webClient.closeAllWindows();
		return html;
	}

	public String get(String url, String charset) {
		return get(url);
	}

	public void setProxy(String proxyIp, String proxyPort) {
		ProxyConfig proxyConfig = new ProxyConfig(proxyIp,
				Integer.parseInt(proxyPort));
		webClient.getOptions().setProxyConfig(proxyConfig);
	}

}
