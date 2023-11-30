package com.jsh.erp.service.transferwarehouse;

import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.DepotHead;
import com.jsh.erp.datasource.entities.DepotHeadExample;
import com.jsh.erp.datasource.entities.DepotItem;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.depotItem.DepotItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public int confirmSingleStatus(Long id, Long mid, Integer amount) throws Exception {
        int result = 0;
        Long dhId = 0L;
        DepotHead depotHead = depotHeadService.getDepotHead(id);
        if(BusinessConstants.PURCHASE_STATUS_TRANSFER_SKIPING.equals(depotHead.getStatus())) {
            dhId = depotHead.getId();
        } else {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_UN_TRANSFER_TO_TRANSFER_FAILED_MSG));
        }
        int size = updateTransferDepotHeadStock(id, mid, amount);

        if(dhId > 0) {
            List<Long> dhIds = new ArrayList<>();
            dhIds.add(dhId);
            depotHead = new DepotHead();

            // TODO remark是否有記錄已完成移倉的item id
            String remark = depotHead.getRemark();
            if(remark == null) {
                if(size == 1) {
                    depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                }
                remark = String.valueOf(mid);
            } else {
                String[] mIds = remark.split(",");
                if(size == mIds.length+1) {
                    depotHead.setStatus(BusinessConstants.PURCHASE_STATUS_TRANSER_SKIPED);
                }
                remark = remark.concat(",").concat(String.valueOf(mid));
            }
            depotHead.setRemark(remark);
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
    public int updateTransferDepotHeadStock(Long id, Long mid, Integer amount) throws Exception {
        List<DepotItem> list = depotItemService.getListByHeaderId(id);
        //更新當前庫存 header id 及 material id相同
        DepotItem depotItem = list.get(0);
        for(int i=0;i<list.size();i++) {
            if(list.get(i).getMaterialId() == mid) {
                depotItem = list.get(i);
                break;
            }
        }

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

        depotItemService.updateCurrentStock(depotItem);
        return list.size();
    }
}
