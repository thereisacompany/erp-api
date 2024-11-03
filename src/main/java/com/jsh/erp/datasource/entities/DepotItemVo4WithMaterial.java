package com.jsh.erp.datasource.entities;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepotItemVo4WithMaterial {

    private Long MId;

    private String MName;

    private Long organId;
    private Long cid;
    private String organName;

    private String MNumber;

    private String MModel;

    private String MaterialUnit;

    private String MColor;

    private String MStandard;

    private String MMfrs;

    private String MOtherField1;

    private String MOtherField2;

    private String MOtherField3;

    private String enableSerialNumber;

    private String enableBatchNumber;

    private String categoryName;

    private String DepotName;

    private String counterName;
    private String AnotherDepotName;

    private Long UnitId;

    private String unitName;

    private Integer ratio;

    private String otherUnit;

    private BigDecimal presetPriceOne;

    private String priceStrategy;

    private BigDecimal purchaseDecimal;

    private String barCode;

    private Long depotId;
    private Long anotherDepotId;

}