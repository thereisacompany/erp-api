package com.jsh.erp.datasource.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "車輛管理資料")
public class Vehicle {

    private Long id;

    @ApiModelProperty(value = "車牌號碼")
    @JsonProperty("license_plate_number")
    private String licensePlateNumber;
    
    @ApiModelProperty(value = "品牌型號")
    @JsonProperty("brand_model")
    private String brandModel;
    
    @ApiModelProperty(value = "駕駛")
    private String driver;
    
    @ApiModelProperty(value = "車身顏色")
    private String color;
    
    @ApiModelProperty(value = "里程數(公里)")
    private Integer mileage;

    @ApiModelProperty(value = "引擎號碼")
    @JsonProperty("engine_number")
    private String engineNumber;

    @ApiModelProperty(value = "出廠日期")
    private String manufacture;

    @ApiModelProperty(value = "檢測日期")
    @JsonProperty("test_date")
    private String testDate;

    @ApiModelProperty(value = "保險日期")
    @JsonProperty("insurance_date")
    private String insuranceDate;

    @ApiModelProperty(value = "排放量(c.c.)")
    private Integer emissions;

    @ApiModelProperty(value = "車價")
    private Integer price;

    @ApiModelProperty(value = "車輛狀態(1:正常 2:出租中 3:出售中")
    private Integer status;

    @ApiModelProperty(value = "歸屬(1:公司 2:私人 3:租用")
    private Integer ownership;
}
