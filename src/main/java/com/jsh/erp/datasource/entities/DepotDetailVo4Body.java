package com.jsh.erp.datasource.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepotDetailVo4Body {
    @ApiModelProperty(required = true)
    private Long headerId;
    @ApiModelProperty(required = true)
    private Integer driverId;
    @ApiModelProperty(required = true)
    private String assignDate;
    @ApiModelProperty(required = true)
    private String assignUser;
}
