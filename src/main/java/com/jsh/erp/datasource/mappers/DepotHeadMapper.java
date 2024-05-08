package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.*;

import java.util.List;

import com.jsh.erp.datasource.vo.DeliveryStatus;
import com.jsh.erp.datasource.vo.DepotHeadDetail;
import com.jsh.erp.datasource.vo.DriverDelivery;
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
    List<DepotReport> selectDetailReport(@Param("detailId") Long detailId);
    DepotReport selectDetailReportByPrimaryKey(@Param("id") Long id);
    int insertDetail(DepotDetail record);
    int insertDetailRecord(DepotRecord record);
    int updateDetail(@Param("record") DepotDetail record);
//    int updateDetailRecord(@Param("record") DepotRecord record);
    int updateDetailReport(@Param("report") DepotReport report);

    DepotHead selectByPrimaryKey(Long id);

    Long selectIdByNumber(String number);

    int updateByExampleSelective(@Param("record") DepotHead record, @Param("example") DepotHeadExample example);

    int updateByExample(@Param("record") DepotHead record, @Param("example") DepotHeadExample example);

    int updateByPrimaryKeySelective(DepotHead record);

    int updateByPrimaryKey(DepotHead record);

    List<DriverDelivery> findAllDriver(@Param("driverName") String driverName,
                                       @Param("licensePlateNumber") String licensePlateNumber,
                                       @Param("beginTime") String beginTime,
                                       @Param("endTime") String endTime,
                                       @Param("keyword") String keyword,
                                       @Param("offset") Integer offset,
                                       @Param("rows") Integer rows);
    Long countAllDriver(@Param("driverName") String driverName,
                       @Param("licensePlateNumber") String licensePlateNumber,
                        @Param("beginTime") String beginTime,
                        @Param("endTime") String endTime,
                       @Param("keyword") String keyword);
}