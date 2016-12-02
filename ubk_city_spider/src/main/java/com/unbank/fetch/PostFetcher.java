package com.unbank.fetch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * post 请求解析Fetcher
 * @author Administrator
 *
 */
public class PostFetcher {
	public static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(30000).setConnectTimeout(30000).build();
	public static BasicCookieStore cookieStore = new BasicCookieStore();
	public static CloseableHttpClient httpClient;
	public final String _DEFLAUT_CHARSET = "utf-8";
	public static PostFetcher fetcher = PostFetcher.getInstance();
	public static Log logger = LogFactory.getLog(PostFetcher.class);

	public synchronized static PostFetcher getInstance() {
		if (fetcher == null) {
			fetcher = new PostFetcher();
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
			HttpClientBuilder httpClientBuilder = new HttpClientBuilder(false,
					poolingHttpClientConnectionManager, cookieStore);
			httpClient = httpClientBuilder.getHttpClient();
		}
		return fetcher;
	}

	public String inputStream2String(InputStream is, String charset) {
		String temp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int i = -1;
			while (true) {
				try {
					i = is.read();
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				if (i <= -1) {
					break;
				}
				baos.write(i);

			}
			temp = baos.toString(charset);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;

	}

	public String post(String url, Map<String, String> params,
			Map<String, String> headers, String charset) {
		String result = null;
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		HttpPost httpPost = new HttpPost(url);
		try {
			if (headers != null) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String key : params.keySet()) {
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			}
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpPost,
					context);
			try {
				InputStream inputStream = response.getEntity().getContent();
				result = inputStream2String(inputStream, charset);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
		return result;
	}

	public Document getDoument(String url, Map<String, String> params,
			Map<String, String> headers, String charset) {
		String html = post(url, params, headers, charset);
		if (html == null) {
			return null;
		}
		Document document = Jsoup.parse(html, url);
		return document;
	}

}
