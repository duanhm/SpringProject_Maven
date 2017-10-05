package com.dhm.workmanagement.biz.dao;

import java.util.List;

import com.dhm.workmanagement.dto.WorkExecutorDTO;

/**
 * 
 * @author EX-DUANHONGMEI001
 *
 */
public interface WorkManagementDAO {

    public List<WorkExecutorDTO> getWorkManagementProfile();
}
