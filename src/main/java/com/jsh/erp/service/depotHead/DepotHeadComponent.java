package com.jsh.erp.service.depotHead;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.service.ICommonQuery;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.QueryUtils;
import com.jsh.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "depotHead_component")
@DepotHeadResource
public class DepotHeadComponent implements ICommonQuery {

    @Resource
    private UserService userService;

    @Resource
    private DepotHeadService depotHeadService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return depotHeadService.getDepotHead(id);
    }

    @Override
    public List<?> select(Map<String, String> map)throws Exception {
        return getDepotHeadList(map);
    }

    private List<?> getDepotHeadList(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        if(type==null || type.isEmpty()) {
            type = BusinessConstants.DEPOTHEAD_TYPE_OUT;
        }
//        if(type.equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
//            if(subType==null || (subType != null && subType.isEmpty())) {
//                subType = BusinessConstants.DEPOTHEAD_SUBTYPE_OUT;
//            }
//        }
        String roleType = StringUtil.getInfo(search, "roleType");
        String hasDebt = StringUtil.getInfo(search, "hasDebt");
        String status = StringUtil.getInfo(search, "status");
        String purchaseStatus = StringUtil.getInfo(search, "purchaseStatus");
        String number = StringUtil.getInfo(search, "number");
        String linkNumber = StringUtil.getInfo(search, "linkNumber");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        String keyword = StringUtil.getInfo(search, "keyword");
        Long organId = StringUtil.parseStrLong(StringUtil.getInfo(search, "organId"));
        Long creator = StringUtil.parseStrLong(StringUtil.getInfo(search, "creator"));
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        String MNumber = StringUtil.getInfo(search, "MNumber");
        Long counterId = StringUtil.parseStrLong(StringUtil.getInfo(search, "counterId"));
        Long accountId = StringUtil.parseStrLong(StringUtil.getInfo(search, "accountId"));
        String remark = StringUtil.getInfo(search, "remark");
        Long dStatus = StringUtil.parseStrLong(StringUtil.getInfo(search, "dStatus"));
        Long driverId =  StringUtil.parseStrLong(StringUtil.getInfo(search, "driverId"));
        String beginDateTime = StringUtil.getInfo(search, "beginDateTime");
        String endDateTime = StringUtil.getInfo(search, "endDateTime");
        if(creator == null) {
            User user = userService.getCurrentUser();
            if(roleType == null) {
                roleType = userService.getRoleTypeByUserId(user.getId()).getType(); //角色類型
            }
            if(!roleType.equals("全部數據")) {
                creator = user.getId();
            }
        }
        return depotHeadService.select(type, subType, roleType, hasDebt, status, purchaseStatus, number, linkNumber,
                beginTime, endTime, materialParam, keyword, organId, MNumber, creator, depotId, counterId, accountId, remark,
                dStatus, driverId, beginDateTime, endDateTime, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String type = StringUtil.getInfo(search, "type");
        String subType = StringUtil.getInfo(search, "subType");
        if(type==null || type.isEmpty()) {
            type = BusinessConstants.DEPOTHEAD_TYPE_OUT;
        }
//        if(type.equals(BusinessConstants.DEPOTHEAD_TYPE_OUT)) {
//            if(subType==null || (subType != null && subType.isEmpty())) {
//                subType = BusinessConstants.DEPOTHEAD_SUBTYPE_OUT;
//            }
//        }
        String roleType = StringUtil.getInfo(search, "roleType");
        String hasDebt = StringUtil.getInfo(search, "hasDebt");
        String status = StringUtil.getInfo(search, "status");
        String purchaseStatus = StringUtil.getInfo(search, "purchaseStatus");
        String number = StringUtil.getInfo(search, "number");
        String linkNumber = StringUtil.getInfo(search, "linkNumber");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        String materialParam = StringUtil.getInfo(search, "materialParam");
        String keyword = StringUtil.getInfo(search, "keyword");
        Long organId = StringUtil.parseStrLong(StringUtil.getInfo(search, "organId"));
        Long creator = StringUtil.parseStrLong(StringUtil.getInfo(search, "creator"));
        Long depotId = StringUtil.parseStrLong(StringUtil.getInfo(search, "depotId"));
        Long accountId = StringUtil.parseStrLong(StringUtil.getInfo(search, "accountId"));
        Long dStatus = StringUtil.parseStrLong(StringUtil.getInfo(search, "dStatus"));
        Long driverId =  StringUtil.parseStrLong(StringUtil.getInfo(search, "driverId"));
        String beginDateTime = StringUtil.getInfo(search, "beginDateTime");
        String endDateTime = StringUtil.getInfo(search, "endDateTime");
        String remark = StringUtil.getInfo(search, "remark");
        if(creator == null) {
            User user = userService.getCurrentUser();
            if(roleType == null) {
                roleType = userService.getRoleTypeByUserId(user.getId()).getType(); //角色類型
            }
            if(!roleType.equals("全部數據")) {
                creator = user.getId();
            }
        }
        return depotHeadService.countDepotHead(type, subType, roleType, hasDebt, status, purchaseStatus, number, linkNumber,
                beginTime, endTime, materialParam, keyword, organId, creator, depotId, accountId, remark, dStatus, driverId, beginDateTime, endDateTime);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request) throws Exception{
        return depotHeadService.insertDepotHead(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request)throws Exception {
        return depotHeadService.updateDepotHead(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request)throws Exception {
        return depotHeadService.deleteDepotHead(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request)throws Exception {
        return depotHeadService.batchDeleteDepotHead(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name)throws Exception {
        return depotHeadService.checkIsNameExist(id, name);
    }

}
