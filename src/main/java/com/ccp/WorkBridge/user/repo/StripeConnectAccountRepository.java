package com.ccp.WorkBridge.user.repo;

import com.ccp.WorkBridge.user.StripeConnectAccount;
import com.ccp.WorkBridge.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StripeConnectAccountRepository extends JpaRepository<StripeConnectAccount, Long> {
    Optional<StripeConnectAccount> findByUser(User user);
    Optional<StripeConnectAccount> findByStripeAccountId(String stripeAccountId);

    Optional<StripeConnectAccount> findByUser_Id(Long userId);
}
