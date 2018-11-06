package com.example.service.plan.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@ApiModel
public class BillingPlan {

    @Id
    @GeneratedValue
    @ApiModelProperty(hidden = true)
    private Long id;

    @ManyToOne
    @NonNull
    private ServiceOffering serviceOffering;

    // country code
    // currency code? with default
    // name? description?
    // payment type: finite/infinite/frequency/frequency_interval WEEK, DAY, YEAR, MONTH

    private String currencyCode;//three-character ISO-4217 currency code.
    // value

}
