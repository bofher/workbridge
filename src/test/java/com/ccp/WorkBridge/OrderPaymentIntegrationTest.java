package com.ccp.WorkBridge;

import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.order.repo.OrderRepository;
import com.ccp.WorkBridge.payment.dto.PaymentIntentResult;
import com.ccp.WorkBridge.payment.model.OrderPayment;
import com.ccp.WorkBridge.payment.model.enums.PaymentStatus;
import com.ccp.WorkBridge.payment.repo.OrderPaymentRepository;
import com.ccp.WorkBridge.payment.service.OrderPaymentService;
import com.ccp.WorkBridge.payment.service.interfaces.PaymentProviderService;
import com.ccp.WorkBridge.user.StripeConnectAccount;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.repo.UserRepository;
import com.ccp.WorkBridge.user.repo.StripeConnectAccountRepository;
import com.ccp.WorkBridge.user.service.connect.StripeConnectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderPaymentIntegrationTest {

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderPaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StripeConnectAccountRepository stripeConnectAccountRepository;

    @MockitoBean
    private PaymentProviderService paymentProviderService;

    @MockitoBean
    private StripeConnectService stripeConnectService;

    @Test
    @Transactional
    void testSuccessfulPaymentFlow() {
        // 1. TEST DATA
        User user = createTestUser("test-user@example.com", "Test User");
        User freelancer = createTestUser("freelancer@example.com", "Freelancer");
        Order order = createTestOrder(user, freelancer);
        BigDecimal amount = new BigDecimal("100.00");

        // 2. CREATE PAYMENT
        OrderPayment payment = orderPaymentService.createPayment(
            user, amount, "USD", order
        );
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);

        // 3. PROCESS PAYMENT
        PaymentIntentResult mockResult = new PaymentIntentResult(
            "pi_test123", 
            "succeeded"
        );
        when(paymentProviderService.createOrderPaymentIntent(payment))
            .thenReturn(mockResult);

        when(paymentProviderService.capturePayment("pi_test123"))
                .thenReturn(true);

        PaymentIntentResult result = orderPaymentService.processPayment(payment.getId());
        assertThat(result.paymentIntentId()).isEqualTo("pi_test123");

        OrderPayment updated = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.INITIATED);

        // 4. MARK AS PAID
        orderPaymentService.markAsPaid("pi_test123");
        updated = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.PAID);

        // 5. COMPLETE PAYMENT
        StripeConnectAccount account = createTestAccount(freelancer);
        // Обновляем фрилансера с аккаунтом Stripe
        freelancer.setStripeConnectAccount(account);
        userRepository.save(freelancer);

        when(paymentProviderService.transferToFreelancer(
            updated, 
            account.getStripeAccountId()
        )).thenReturn("tr_test456");

        orderPaymentService.completePayment(payment.getId());
        updated = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.TRANSFERRED);
    }

    private User createTestUser(String email, String fullName) {
        User user = new User();

        user.setEmail(email);
        user.setPassword("encoded_password");
        user.setFullName(fullName);

        user.setPriorityCoefficient(1.0);
        return userRepository.save(user);
    }

    private Order createTestOrder(User customer, User freelancer) {
        Order order = new Order();

        order.setCustomer(customer);
        order.setFreelancer(freelancer);
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setPrice(new BigDecimal("100.00"));
        return orderRepository.save(order);
    }

    private StripeConnectAccount createTestAccount(User user) {
        StripeConnectAccount account = new StripeConnectAccount();

        account.setUser(user);
        account.setStripeAccountId("acct_test123456");
        account.setChargesEnabled(true);
        account.setPayoutsEnabled(true);
        account.setDetailsSubmitted(true);
        account.setRequirementsDue(false);
        return stripeConnectAccountRepository.save(account);
    }
}