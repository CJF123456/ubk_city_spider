package com.unbank.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class URLBaseFilter implements URLFilter {

	public static Log logger = LogFactory.getLog(URLBaseFilter.class);
	public static SimpleBloomFilter simpleBloomFilter = SimpleBloomFilter
			.getInstance();

	public boolean checkNewsURL(String url) {
		String endWithUrls[] = new String[] { ".mp4", ".pdf", ".mp3", ".flv",
				".swf", ".gif", ".bmp", ".jpg", ".png", ".gif", ".rar", ".zip",
				".7z", ".exe", ".jpeg", ".dll", ".doc" };
		String containsUrls[] = new String[] { "english", "images", "img",
				"blog", "reports", "http://syjhs.askci.com", ".bbs." };
		if (url == null || url.isEmpty()) {
			return false;
		} else {
			for (int i = 0; i < containsUrls.length; i++) {
				if (url.contains(containsUrls[i])) {
					return false;
				}
			}
			for (int i = 0; i < endWithUrls.length; i++) {
				if (url.endsWith(endWithUrls[i])) {
					return false;
				}
			}
			// if (false) {
			if (simpleBloomFilter.contains(url)) {
				return false;
			} else {
				simpleBloomFilter.add(url);
			}
			// } else {
			// if (getDocumentByJsoup(url).body().text().trim().contains("1")) {
			// return false;
			// }
			// }
		}
		return true;
	}

	public static Document getDocumentByJsoup(String url) {
		Document doc = null;
		Connection conn = null;
		conn = Jsoup
				.connect(
						"http://10.0.2.120:8080/ubk_crawl_duplicate/rest/crawlduplicate/duplicateUrls")
				.data("url", url);
		// GET方式获取内容
		try {
			doc = conn.ignoreContentType(true).timeout(3000).post();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("连接去重服务失败====================================", e);
			doc = Jsoup.parse("0");
		}
		// GET方式获取内容失败，尝试POST方式获取
		return doc;
	}

}
