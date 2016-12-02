package com.unbank.store;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.unbank.mybatis.entity.SQLAdapter;
import com.unbank.mybatis.factory.DynamicConnectionFactory;
import com.unbank.mybatis.mapper.SQLAdapterMapper;

public class RateValueStore {
	private static Log logger = LogFactory.getLog(RateValueStore.class);

	public void saveValues(String tableName, Map<String, Object> colums) {

		String sql = "insert into " + tableName + " ";
		SQLAdapter sqlAdapter = new SQLAdapter();
		sqlAdapter.setSql(sql);
		sqlAdapter.setObj(colums);
		SqlSession sqlSession = DynamicConnectionFactory
				.getInstanceSessionFactory("development").openSession();
		try {
			SQLAdapterMapper sqlAdapterMapper = sqlSession
					.getMapper(SQLAdapterMapper.class);
			int id = saveValue(sqlAdapter, sqlAdapterMapper);
			sqlSession.commit();
		} catch (Exception e) {
			logger.error("保存票据到数据库失败", e);
			sqlSession.rollback();
		} finally {
			sqlSession.close();

		}
	}

	private int saveValue(SQLAdapter sqlAdapter,
			SQLAdapterMapper sqlAdapterMapper) {
		sqlAdapterMapper.insertReturnPriKey(sqlAdapter);
		int id = sqlAdapter.getPrikey();
		return id;
	}

}
