package com.jsh.erp.service.transferwarehouse;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.DepotHead;
import com.jsh.erp.datasource.entities.DepotHeadExample;
import com.jsh.erp.datasource.entities.DepotItem;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.datasource.mappers.DepotItemMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferWarehouseService {

    private Logger logger = LoggerFactory.getLogger(TransferWarehouseService.class);

    @Resource
    private DepotHeadService depotHeadService;

    @Resource
    private DepotItemService depotItemService;

    @Resource
    private DepotHeadMapper depotHeadMapper;

//    @Resource
//    private DepotItemMapper depotItemMapper;

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotItemIDs)throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotItemIDs);
        for(Long id: ids) {

            DepotItem depotItem = depotItemService.getDepotItem(id);

            DepotHead depotHead = depotHeadService.getDepotHead(depotItem.getHeaderId());
            if("4".equals(status)) {
                if("5".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_MSG));
                }

                //更新當前庫存
                if(depotItem.getAnotherDepotId()!=null){
                    depotItemService.updateCurrentStockFun(depotItem.getHeaderId(), depotItem.getMaterialId(), depotItem.getAnotherDepotId(), null);
                }
            }
        }
        if(dhIds.size()>=ids.size()) {
            DepotHead depotHead = new DepotHead();
            depotHead.setStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int confirmSingleStatus(Long id, Integer amount) throws Exception {
        int result = 0;
        Long dhId;
        DepotItem depotItem = depotItemService.getDepotItem(id);
        DepotHead depotHead = depotHeadService.getDepotHead(depotItem.getHeaderId());
        if(BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING.equals(depotHead.getStatus())) {
            dhId = depotHead.getId();
        } else {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_MSG));
        }
        int size = updateTransferDepotHeadStock(id, depotHead.getId(), amount);

        if(dhId > 0) {
            List<Long> dhIds = new ArrayList<>();
            dhIds.add(dhId);
            depotHead = new DepotHead();

            JSONObject jsonObject = new JSONObject();
            // TODO remark是否有記錄已完成移倉的item id
            String remark = depotHead.getRemark();
            if(remark == null || remark.isEmpty()) {
                if(size == 1) {
                    depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                }
                jsonObject.put("move", String.valueOf(id));
            } else {
                jsonObject = JSONObject.parseObject(remark);
                if(jsonObject.containsKey("move")) {
                    String move = jsonObject.getString("move");
                    String[] mIds = move.split(",");
                    if (size == mIds.length + 1) {
                        depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                    }
                    move = move.concat(",").concat(String.valueOf(id));
                    jsonObject.put("move", move);
                } else {
                    if(size == 1) {
                        depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                    }
                    jsonObject.put("move", String.valueOf(id));
                }
            }
            depotHead.setRemark(jsonObject.toJSONString());
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    /**
     * 移倉完成後，更新數量
     * @param id
     * @param amount 確認數量
     * @throws Exception
     */
    private int updateTransferDepotHeadStock(Long id, Long headerId, Integer amount) throws Exception {
        List<DepotItem> list = depotItemService.getListByHeaderId(headerId);
        //更新當前庫存
        DepotItem depotItem = depotItemService.getDepotItem(id);

        // TODO 判斷確認數量是否有帶值，若與原移倉數量不相同，需額外調整
        if(amount != null) {
            if(depotItem.getOperNumber().longValue() != amount) {
                // TODO 2023-11-30 是否要判斷確認的數量，是否有超過商品的庫存
                BigDecimal decimalAmount = new BigDecimal(amount);
                depotItem.setOperNumber(decimalAmount);
                depotItem.setBasicNumber(decimalAmount);

                String transferChange = "移倉數量: %d, 確認數量: %d";
                String remark = depotItem.getRemark();
                if(remark != null || !remark.isEmpty()) {
                    remark = remark.concat(",").concat(String.format(transferChange, depotItem.getOperNumber(), amount));
                } else {
                    remark = String.format(transferChange, depotItem.getOperNumber(), amount);
                }
                depotItem.setRemark(remark);
            }
        }
        if(depotItem.getAnotherDepotId()!=null){
            depotItemService.updateCurrentStockFun(depotItem.getHeaderId(), depotItem.getMaterialId(), depotItem.getAnotherDepotId(), null);
        }
        return list.size();
    }
}
