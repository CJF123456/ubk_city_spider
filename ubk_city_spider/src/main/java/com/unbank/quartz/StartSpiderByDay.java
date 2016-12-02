package com.unbank.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.unbank.spider.CityDataSpider;

@Component
public class StartSpiderByDay {
	private static Log logger = LogFactory.getLog(StartSpiderByDay.class);
	@Autowired
	CityDataSpider cityDataSpider;
	

	/**
	 * 定时启动任务
	 */
	public void executeInternal() {
		try {
			logger.info("城市代码任务");
			cityDataSpider.getCityData();

		} catch (Exception e) {
			logger.error("启动采集定时任务出错", e);
		}
	}

}
