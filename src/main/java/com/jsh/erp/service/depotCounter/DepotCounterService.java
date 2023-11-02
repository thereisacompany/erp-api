package com.jsh.erp.service.depotCounter;

import com.jsh.erp.datasource.entities.DepotCounter;
import com.jsh.erp.datasource.mappers.DepotCounterMapper;
import com.jsh.erp.exception.JshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DepotCounterService {

    private Logger logger = LoggerFactory.getLogger(DepotCounterService.class);

    @Resource
    private DepotCounterMapper depotCounterMapper;

    public List<DepotCounter> getDepotCounter() throws Exception{
        List<DepotCounter> list = null;
        try {
            list = depotCounterMapper.selectAll();
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }
}
