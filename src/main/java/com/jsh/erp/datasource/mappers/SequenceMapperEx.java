package com.jsh.erp.datasource.mappers;

import org.apache.ibatis.annotations.Param;

public interface SequenceMapperEx {

    void updateBuildOnlyNumber(@Param("seq_name") String seq_name);

    /**
     * 获得一个全局唯一的数作为订单号的追加
     * */
    Long getBuildOnlyNumber(@Param("seq_name") String seq_name);
}
