package com.kemboishoppingcartapp.orderservice.service;

import com.kemboishoppingcartapp.orderservice.dto.InventoryResponse;
import com.kemboishoppingcartapp.orderservice.dto.OrderLineItemsDto;
import com.kemboishoppingcartapp.orderservice.dto.OrderRequest;
import com.kemboishoppingcartapp.orderservice.model.Order;
import com.kemboishoppingcartapp.orderservice.model.OrderLineItems;
import com.kemboishoppingcartapp.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .build();
        List<OrderLineItems> orderLineItemsList =
                orderRequest.getOrderLineItemsDtoList()
                        .stream()
                        .map(orderLineItemsDto -> mapToDto(orderLineItemsDto))
                        .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        // check whether the product exists before placing an order
       InventoryResponse[] inventoryResponsesArray = webClient.get()
                .uri("http://localhost:8081/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).anyMatch(InventoryResponse::isInStock);
        if(allProductsInStock) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = OrderLineItems.builder()
                .skuCode(orderLineItemsDto.getSkuCode())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
        return orderLineItems;
    }
}
