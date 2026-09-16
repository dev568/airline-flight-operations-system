package com.airline.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private CorrelationIdFilter correlationIdFilter;
    private GatewayFilterChain filterChain;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        correlationIdFilter = new CorrelationIdFilter();
        filterChain = mock(GatewayFilterChain.class);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void filter_WhenCorrelationIdPresent_ShouldPreserveIt() {
        String existingCorrelationId = "550e8400-e29b-41d4-a716-446655440000";
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Correlation-ID", existingCorrelationId)
                .build();
        exchange = MockServerWebExchange.from(request);

        correlationIdFilter.filter(exchange, filterChain).block();

        assertEquals(existingCorrelationId, exchange.getResponse().getHeaders().getFirst("X-Correlation-ID"));
        assertEquals(existingCorrelationId, exchange.getRequest().getHeaders().getFirst("X-Correlation-ID"));
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void filter_WhenCorrelationIdAbsent_ShouldGenerateNew() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        exchange = MockServerWebExchange.from(request);

        correlationIdFilter.filter(exchange, filterChain).block();

        String generatedCorrelationId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        assertNotNull(generatedCorrelationId);
        assertFalse(generatedCorrelationId.isBlank());
        assertEquals(generatedCorrelationId, exchange.getRequest().getHeaders().getFirst("X-Correlation-ID"));
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void filter_WhenCorrelationIdBlank_ShouldGenerateNew() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("X-Correlation-ID", "")
                .build();
        exchange = MockServerWebExchange.from(request);

        correlationIdFilter.filter(exchange, filterChain).block();

        String generatedCorrelationId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        assertNotNull(generatedCorrelationId);
        assertFalse(generatedCorrelationId.isBlank());
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    void getOrder_ShouldReturnNegativeValue() {
        int order = correlationIdFilter.getOrder();
        assertTrue(order < 0);
        assertEquals(-100, order);
    }
}
