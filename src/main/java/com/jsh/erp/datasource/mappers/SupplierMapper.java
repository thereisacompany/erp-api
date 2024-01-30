package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.Supplier;
import com.jsh.erp.datasource.entities.SupplierExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SupplierMapper {
    long countByExample(SupplierExample example);

    int deleteByExample(SupplierExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Supplier record);

    int insertSelective(Supplier record);

    int insertCarUser(String username, String loginName, String loginPassword, Long supplierId);
    int updateCarUser(String loginName, String loginPassword, Long id);

    long selectLastDriverId();

    int isDriverLoginNameExist(String loginName, Long supplierId);

    String selectCarUser(Long supplierId);

    long selectCarUserId(Long supplierId);

    List<Supplier> selectByExample(SupplierExample example);

    Supplier selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Supplier record, @Param("example") SupplierExample example);

    int updateByExample(@Param("record") Supplier record, @Param("example") SupplierExample example);

    int updateByPrimaryKeySelective(Supplier record);

    int updateByPrimaryKey(Supplier record);
}