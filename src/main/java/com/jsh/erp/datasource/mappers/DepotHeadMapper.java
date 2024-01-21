package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.DepotDetail;
import com.jsh.erp.datasource.entities.DepotHead;
import com.jsh.erp.datasource.entities.DepotHeadExample;
import java.util.List;

import com.jsh.erp.datasource.entities.DepotRecord;
import com.jsh.erp.datasource.vo.DeliveryStatus;
import com.jsh.erp.datasource.vo.DepotHeadDetail;
import org.apache.ibatis.annotations.Param;

public interface DepotHeadMapper {
    long countByExample(DepotHeadExample example);

    int deleteByExample(DepotHeadExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DepotHead record);

    int insertSelective(DepotHead record);

    List<DepotHead> selectByExample(DepotHeadExample example);

    DepotHeadDetail selectHeaderDetailByHeaderId(@Param("headerId") Long headerId,
                                                 @Param("itemId") Long itemId);
    List<DeliveryStatus> selectDetailRecord(@Param("detailId") Long detailId);

    DepotDetail selectDetailByHeaderId(@Param("headerId") Long headerId);
    int insertDetail(DepotDetail record);
    int insertDetailRecord(DepotRecord record);
    int updateDetail(@Param("record") DepotDetail record);
//    int updateDetailRecord(@Param("record") DepotRecord record);

    DepotHead selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DepotHead record, @Param("example") DepotHeadExample example);

    int updateByExample(@Param("record") DepotHead record, @Param("example") DepotHeadExample example);

    int updateByPrimaryKeySelective(DepotHead record);

    int updateByPrimaryKey(DepotHead record);
}