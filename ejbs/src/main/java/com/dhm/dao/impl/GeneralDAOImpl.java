package com.dhm.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.dhm.dao.GeneralDAO;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;

@Repository(GeneralDAO.SERVICE_ID)
public class GeneralDAOImpl extends SqlMapClientDaoSupport implements GeneralDAO{

	@Resource(name="sqlMapClient")
	private void injectSqlMapClient(SqlMapClient sqlMapClient) {
		super.setSqlMapClient(sqlMapClient);
	}
	
	@Override
	public Object insert(String sqlId, Object param) {
		return getSqlMapClientTemplate().insert(sqlId, param);
	}

	@Override
	public int update(String sqlId, Object param) {
		return getSqlMapClientTemplate().update(sqlId, param);
	}
	
	@Override
	public Object queryForObject(String sqlId, Object param) {
		return getSqlMapClientTemplate().queryForObject(sqlId, param);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryForListMap(String sqlId, Object param) {
		return getSqlMapClientTemplate().queryForList(sqlId, param);
	}

	@Override
	public List<?> queryForList(String sqlId, Object param) {
		return getSqlMapClientTemplate().queryForList(sqlId, param);
	}
	
	@Override
	public <T> Integer batchInsert(final String sqlId, final List<T> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return getSqlMapClientTemplate().execute(new SqlMapClientCallback<Integer>() {
            @Override
            public Integer doInSqlMapClient(SqlMapExecutor sqlmapexecutor) throws SQLException {
                // 开始批处理
                sqlmapexecutor.startBatch();
                int count = 0;
                for (T t : list) {
                    // 批量插入
                    Object obj = sqlmapexecutor.insert(sqlId, t);
                    count += (null == obj) ? 0 : 1;
                }
                // 执行批处理
                sqlmapexecutor.executeBatch();
                return count;
            }
        });
    }
	
	@Override
	public <T> Integer batchUpdate(final String sqlId, final List<T> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return getSqlMapClientTemplate().execute(new SqlMapClientCallback<Integer>() {
            @Override
            public Integer doInSqlMapClient(SqlMapExecutor sqlmapexecutor) throws SQLException {
                // 开始批处理
                sqlmapexecutor.startBatch();
                int count = 0;
                for (T t : list) {
                    // 批量更新
                    count += sqlmapexecutor.update(sqlId, t);
                }
                // 执行批处理
                sqlmapexecutor.executeBatch();
                return count;
            }
        });
    }
}
