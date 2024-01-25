package com.jsh.erp.datasource.entities;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepotReportVo4Body {
    @ApiModelProperty(required = true)
    private String feedback;
}
