package com.jsh.erp.controller;

import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ji-sheng-hua 752*718*920
 */
@RestController
@RequestMapping(value = "/sequence")
@Api(tags = {"单据编号"})
public class SequenceController {
    private Logger logger = LoggerFactory.getLogger(SequenceController.class);

    @Resource
    private SequenceService sequenceService;

    /**
     * 單據編號生成接口
     * @param request
     * @return
     */
    @GetMapping(value = "/buildNumber")
    @ApiOperation(value = "單據編號生成接口")
    public BaseResponseInfo buildNumber(HttpServletRequest request, @RequestParam(value = "是否產生配送單", required = false) boolean isDO)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
//            String number = sequenceService.buildOnlyNumber(BusinessConstants.DEPOT_NUMBER_SEQ);
            String number = sequenceService.buildNumber(isDO);
            map.put("defaultNumber", number);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

}
