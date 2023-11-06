package com.jsh.erp.service.depotCounter;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.DepotCounter;
import com.jsh.erp.datasource.mappers.DepotCounterMapper;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.QueryUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class DepotCounterService {

    private Logger logger = LoggerFactory.getLogger(DepotCounterService.class);

    @Resource
    private DepotCounterMapper depotCounterMapper;

    @Resource
    private LogService logService;

    public List<DepotCounter> getAllList(Map<String, String> map) throws Exception{
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        String remark = StringUtil.getInfo(search, "remark");
        String order = QueryUtils.order(map);

        List<DepotCounter> list = null;
        try {
            list = depotCounterMapper.selectByConditionDepot(name, depotId, remark, QueryUtils.offset(map), QueryUtils.rows(map));
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotCounter> getAllList() {
        List<DepotCounter> list = null;
        try {
            list = depotCounterMapper.selectByAll();
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long counts(Map<String, String> map) {
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        String remark = StringUtil.getInfo(search, "remark");

        Long result = null;
        try{
            result = depotCounterMapper.countsByCounter(name, depotId, remark);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
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
