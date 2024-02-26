package com.jsh.erp.datasource.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VehicleLicenseNumberListVo {

    @ApiModelProperty(value = "車牌號碼")
    @JsonProperty("license_plate_number")
    private String licensePlateNumber;

    @ApiModelProperty(value = "是否已綁定司機(0:未綁定 1:已綁定)")
    @JsonProperty("is_bind")
    private Integer isBind;
}
