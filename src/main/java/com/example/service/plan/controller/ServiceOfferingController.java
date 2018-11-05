package com.example.service.plan.controller;

import com.example.service.plan.model.ServiceOffering;
import com.example.service.plan.repository.ServiceOfferingRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/v1/service-offerings", produces = {APPLICATION_JSON_VALUE})
@Api(value = "/v1/service-offerings", tags = "ServiceOffering Plan Payments", description = "Operations about for service offering payments")
public class ServiceOfferingController {


}
