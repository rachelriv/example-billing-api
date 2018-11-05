package com.example.service.plan.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Data
@Entity
public class ServiceBillingPlan {
    private @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @NonNull
    @JoinColumn(name = "service_offering_id")
    private ServiceOffering serviceOffering;

    private String currencyCode;

}
