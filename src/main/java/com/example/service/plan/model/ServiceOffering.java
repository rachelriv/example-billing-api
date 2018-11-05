package com.example.service.plan.model;

import lombok.Data;

import javax.money.CurrencyUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class ServiceOffering {

    @Column(name = "service_offering_id")
    private @Id @GeneratedValue Long id;

    private int concurrentStreams;

//    CurrencyUnit

}
