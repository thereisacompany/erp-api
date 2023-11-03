package com.jsh.erp.service.depotCounter;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.DepotCounter;
import com.jsh.erp.datasource.mappers.DepotCounterMapper;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class DepotCounterService {

    private Logger logger = LoggerFactory.getLogger(DepotCounterService.class);

    @Resource
    private DepotCounterMapper depotCounterMapper;

    @Resource
    private LogService logService;

    public List<DepotCounter> getAllList(Long depotId) throws Exception{
        List<DepotCounter> list = null;
        try {
            list = depotCounterMapper.selectAll(null);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public DepotCounter getCounter(Long id) {
        return depotCounterMapper.selectByPrimaryKey(id);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertCounter(DepotCounter counter, HttpServletRequest request) {
//        DepotCounter counter = JSONObject.parseObject(obj.toJSONString(), DepotCounter.class);
        int result=0;

        try {
            counter.setIsDefault(false);
            counter.setEnabled(true);
            result = depotCounterMapper.insertSelective(counter);

            logService.insertLog("倉庫-儲位",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(counter.getName()).toString(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }

        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateCounter(DepotCounter counter, HttpServletRequest request) {
//        DepotCounter counter = JSONObject.parseObject(obj.toJSONString(), DepotCounter.class);
        int result=0;
        try{
            result= depotCounterMapper.updateByPrimaryKeySelective(counter);
            logService.insertLog("倉庫-儲位",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(counter.getName()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
