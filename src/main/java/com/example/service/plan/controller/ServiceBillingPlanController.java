package com.example.service.plan.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/v1/billing-plans", produces = {APPLICATION_JSON_VALUE})
@Api(value = "/v1/billing-plans", tags = "ServiceOffering Plan Payments", description = "Operations about for service offering payments")
public class ServiceBillingPlanController {
}
