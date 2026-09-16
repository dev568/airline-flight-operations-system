package com.airline.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "FLIGHT_SERVICE_URL=http://localhost:8081",
        "CREW_SERVICE_URL=http://localhost:8082",
        "OPERATIONS_SERVICE_URL=http://localhost:8083"
})
class ApiGatewayApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void correlationIdFilterBeanExists() {
        assertNotNull(applicationContext.getBean("correlationIdFilter"));
    }
}
