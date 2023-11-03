package com.jsh.erp.datasource.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(description = "儲位")
public class DepotCounter {

    private Long id;

    @ApiModelProperty("倉庫id")
    private Long depotId;

    @ApiModelProperty(value = "名稱", required = true)
    private String name;

    @ApiModelProperty("空間")
    private BigDecimal space;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("註解")
    private String remark;

    @ApiModelProperty("負責人")
    private Long principal;

    @ApiModelProperty("是否開啟")
    private Boolean enabled;

    @ApiModelProperty("是否預設")
    private Boolean isDefault;

    private Long tenantId;
}
