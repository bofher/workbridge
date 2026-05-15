package com.ccp.WorkBridge.order.controller;

import com.ccp.WorkBridge.order.dto.CreateOrderRequest;
import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.order.dto.OrderResponse;
import com.ccp.WorkBridge.order.dto.OrderSearchCriteria;
import com.ccp.WorkBridge.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public OrderResponse create(@RequestBody CreateOrderRequest createOrderRequest,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return OrderResponse.toResponse(orderService.createOrder(createOrderRequest, userDetails.user()));
    }

    /**
     * Freelancer marks that he completed the order.
     * Sets order status to READY
     *
     * @param orderId the unique identifier of the order to complete
     */
    @PostMapping("/{orderId}/confirm-ready")
    public void confirmOrderReady(@PathVariable Long orderId) {
        orderService.markOrderAsReady(orderId);
    }

    /**
     * Customer confirms that the order is correctly completed
     * Completes the order and processes the payment transfer.
     * Updates the order status to the completed and executes the associated payment.
     *
     * @param orderId the unique identifier of the order to complete
     */

    @PostMapping("/{orderId}/complete-and-transfer")
    public void completeOrderAndTransfer(@PathVariable Long orderId) {
        orderService.completeOrderAndExecutePayment(orderId);
    }

    @DeleteMapping
    public void deleteOrder(@RequestParam Long orderId) {
        orderService.deleteOrder(orderId);
    }

    /**
     * Searches for orders based on the provided criteria with pagination support.
     *
     * <p>Example request with pagination:</p>
     *
     * <pre>
     * POST /orders/search?page=0&size=10&sort=price,desc
     * Content-Type: application/json
     *
     * {
     *   "query": "spring",
     *   "minPrice": 100,
     *   "maxPrice": 500,
     *   "skills": [
     *     {
     *       "skillId": 1,
     *       "minLevel": 3
     *     }
     *   ]
     * }
     * </pre>
     *
     * <p>Pagination behavior:</p>
     * <ul>
     *   <li>page = 0 → first page</li>
     *   <li>page = 1 → second page</li>
     *   <li>size = number of items per page</li>
     *   <li>sort = sorting field and direction (e.g. price,desc)</li>
     * </ul>
     *
     * @param criteria the search criteria for filtering orders
     * @param pageable the pagination information including page number, size, and sorting
     * @return a page of OrderResponse objects matching the search criteria
     */

    @PostMapping("/search")
    public Page<OrderResponse> search(
            @RequestBody OrderSearchCriteria criteria,
            Pageable pageable
    ) {
        return orderService.search(criteria, pageable);
    }
}
