package com.unbank.spider;


import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.unbank.fetch.Fetcher;
import com.unbank.filter.URLBaseFilter;
import com.unbank.filter.URLFilter;
import com.unbank.store.RateValueStore;
import com.unbank.util.MD5;



/**
 * 
 * @author Administrator
 * @url源  http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/
 * @detail 数据来源：2015年统计用区划代码和城乡划分代码(截止2015年09月30日)
 * @date  2016-10-24
 *
 */
@Component
public class CityDataSpider {
	
	
	private static String detailUrl;
	private static MD5 md=new MD5();
	private static URLFilter urlFilter = new URLBaseFilter();
	private static Fetcher fetcher;
	private static String charsetName;
	private static String oldUrl;
	/*public  static void main(String[] args) {
		getCityData();
	}*/
	/**
	 * 第一级 省 直辖市 共计31个 除去我国台湾省、香港特别行政区、澳门特别行政区
	 */
	public static void getCityData() {
		 charsetName="utf-8";
		 oldUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/";
		 fetcher = Fetcher.getInstance();
		 String html= fetcher.getHtmlWithCookie(oldUrl);
		 Document document= Jsoup.parse(html, charsetName);
		 Elements elements=document.select("tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr > td>a");
		 for (Element element : elements) {
			 String el=element.attr("href");
			 String code=el.replace(".html", "").trim();
			 String url= oldUrl+el;
			 String province=element.text().trim();
			 detailUrl = md.GetMD5Code(url);
				if (urlFilter.checkNewsURL(detailUrl)) {
					try {
						Map<String, Object> colums = new HashMap<String, Object>();
						colums.put("province", province);
						colums.put("provincecode", code);
						colums.put("url", url);
						colums.put("detailUrl", detailUrl);
						new RateValueStore().saveValues("province", colums);
						colums.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				getCityByProvince(url,code);
				
		}
	}
	/**
	 * 第二级 市 、
	 * @param url
	 * @param code
	 */
	private static void getCityByProvince(String url, String code) {
		 String html= fetcher.getHtmlWithCookie(url);
		 Document document= Jsoup.parse(html, charsetName);
		 Elements elements=document.select("body > table:nth-child(3) > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr.citytr>td:nth-child(2)>a");
		for (Element element : elements) {
			String el=element.attr("href");
			url= oldUrl+el;
			String citycode=el.replace(".html", "").split("/")[1].trim();
			String city=element.text();
			System.out.println(url);
			 detailUrl = md.GetMD5Code(url);
				if (urlFilter.checkNewsURL(detailUrl)) {
					try {
						Map<String, Object> colums = new HashMap<String, Object>();
						colums.put("city", city);
						colums.put("citycode", citycode);
						colums.put("url", url);
						colums.put("provincecode", code);
						colums.put("detailUrl", detailUrl);
						new RateValueStore().saveValues("city", colums);
						colums.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				getCountyByCity(url,citycode,code);
				try {
					Thread.sleep(1000*60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		 }
		}
		 
		/**
		 * 区 县
		 * @param url
		 * @param citycode
		 */
	private static void getCountyByCity(String url,String citycode,String provincecode) {
		 String html= fetcher.getHtmlWithCookie(url);
		 Document document= Jsoup.parse(html, charsetName);
		 Elements elements=document.select("body > table:nth-child(3) > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr.countytr > td:nth-child(2) > a");
		 for (Element element : elements) {
			 String el= element.select("a").attr("href");
			 System.out.println(el);
			 url=oldUrl+provincecode+"/"+el;
			 String countycode=el.replace(".html", "").split("/")[1].trim();
			 System.out.println();
			 String county=element.select("a").text().trim();
			 System.out.println(county+countycode);
			 detailUrl = md.GetMD5Code(url);
				if (urlFilter.checkNewsURL(detailUrl)) {
					try {
						Map<String, Object> colums = new HashMap<String, Object>();
						colums.put("county", county);
						colums.put("citycode", citycode);
						colums.put("url", url);
						colums.put("countycode", countycode);
						colums.put("detailUrl", detailUrl);
						new RateValueStore().saveValues("county", colums);
						colums.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				getCountyByTown(url,countycode,provincecode);
		}
	}
	private static void getCountyByTown(String url, String countycode,String provincecode) {
		 String html= fetcher.getHtmlWithCookie(url);
		 Document document= Jsoup.parse(html, charsetName);
		 Elements elements=document.select("table:nth-child(3) > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr.towntr > td:nth-child(2) > a");
		 for (Element element : elements) {
			 String el= element.attr("href");
			 String no1=el.split("/")[0];
			 System.out.println(el);
			 url=oldUrl+provincecode+"/"+no1+"/"+el;
			 String towncode=el.replace(".html", "").split("/")[1].trim();
			 System.out.println();
			 String town=element.select("a").text().trim();
			 System.out.println(town+towncode);
			 detailUrl = md.GetMD5Code(url);
				if (urlFilter.checkNewsURL(detailUrl)) {
					try {
						Map<String, Object> colums = new HashMap<String, Object>();
						colums.put("town", town);
						colums.put("towncode", towncode);
						colums.put("url", url);
						colums.put("countycode", countycode);
						colums.put("detailUrl", detailUrl);
						new RateValueStore().saveValues("town", colums);
						colums.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//getCommunityByCounty(url,towncode);
		}
		
	}
	private static void getCommunityByCounty(String url, String towncode) {
		 String html= fetcher.getHtmlWithCookie(url);
		 Document document= Jsoup.parse(html, charsetName);
		 Elements elements=document.select("body > table:nth-child(3) > tbody > tr:nth-child(1) > td > table > tbody > tr:nth-child(2) > td > table > tbody > tr > td > table > tbody > tr.villagetr");
		 for (Element element : elements) {
			 String community= element.select("td:nth-child(3)").text().trim();
			 String type= element.select("td:nth-child(2)").text().trim();
			 detailUrl = md.GetMD5Code(community+towncode);
				if (urlFilter.checkNewsURL(detailUrl)) {
					try {
						Map<String, Object> colums = new HashMap<String, Object>();
						colums.put("community", community);
						colums.put("type",type);
						colums.put("towncode", towncode);
						colums.put("detailUrl", detailUrl);
						new RateValueStore().saveValues("community", colums);
						colums.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					
		 }
	 }
}
