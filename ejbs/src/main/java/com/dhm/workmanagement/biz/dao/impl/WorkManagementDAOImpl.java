package com.dhm.workmanagement.biz.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dhm.common.SQLID;
import com.dhm.dao.GeneralDAO;
import com.dhm.workmanagement.biz.dao.WorkManagementDAO;
import com.dhm.workmanagement.dto.WorkExecutorDTO;

/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
@Component("workManagementDAO")
public class WorkManagementDAOImpl implements WorkManagementDAO {

	@Resource(name=GeneralDAO.SERVICE_ID)
	private GeneralDAO generalDAO;
	
    @SuppressWarnings("unchecked")
	@Override
    public List<WorkExecutorDTO> getWorkManagementProfile() {
    	return (List<WorkExecutorDTO>)generalDAO.queryForList(SQLID.SEARCH_WORK_EXECUTOR_DEFINE, "YXD_CBP");
    }

}
