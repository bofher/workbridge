package com.ccp.WorkBridge.user.service.connect;

import com.ccp.WorkBridge.user.StripeConnectAccount;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.repo.StripeConnectAccountRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.AccountCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StripeConnectService {

    private final StripeConnectAccountRepository stripeConnectAccountRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public StripeConnectAccount getOrCreate(User user) {
        return stripeConnectAccountRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    try {
                        StripeConnectAccount account = createConnectedAccount(user);
                        return stripeConnectAccountRepository.save(account);
                    } catch (DataIntegrityViolationException e) {
                        return stripeConnectAccountRepository.findByUser_Id(user.getId())
                                .orElseThrow(() -> new IllegalStateException("Failed to get Stripe account after race"));
                    }
                });
    }

    public StripeConnectAccount getByUser(User user) {
        return stripeConnectAccountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new IllegalStateException("Stripe connect account not found for user: " + user.getId()));
    }

    @Transactional
    public StripeConnectAccount createConnectedAccount(User user) {
        StripeConnectAccount account = newAccount(user);

        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setCountry("US")
                    .setEmail(user.getEmail())
                    .setCapabilities(
                            AccountCreateParams.Capabilities.builder()
                                    .setCardPayments(
                                            AccountCreateParams.Capabilities.CardPayments.builder()
                                                    .setRequested(true)
                                                    .build()
                                    )
                                    .setTransfers(
                                            AccountCreateParams.Capabilities.Transfers.builder()
                                                    .setRequested(true)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Account stripeAccount = Account.create(params);

            account.setStripeAccountId(stripeAccount.getId());
            syncFromStripe(account, stripeAccount);

            return account;

        } catch (StripeException e) {
            throw new IllegalStateException("Failed to create Stripe connected account", e);
        }
    }

    @Transactional
    public String createOnboardingLink(User user) {
        StripeConnectAccount connectAccount = getOrCreate(user);

        if (connectAccount.getStripeAccountId() == null) {
            throw new IllegalStateException("Stripe account ID is not set");
        }

        if (connectAccount.isReady()) {
            throw new IllegalStateException("Stripe connected account is already ready");
        }

        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(connectAccount.getStripeAccountId())
                    .setRefreshUrl(baseUrl + "/stripe/connect/refresh")
                    .setReturnUrl(baseUrl + "/stripe/connect/return")
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink link = AccountLink.create(params);
            return link.getUrl();
        } catch (StripeException e) {
            throw new IllegalStateException("Failed to create Stripe onboarding link", e);
        }
    }

    @Transactional
    public StripeConnectAccount syncAccountStatus(User user) {
        StripeConnectAccount account = getByUser(user);

        if (account.getStripeAccountId() == null) {
            return account;
        }

        try {
            Account stripeAccount = Account.retrieve(account.getStripeAccountId());
            syncFromStripe(account, stripeAccount);
            return stripeConnectAccountRepository.save(account);
        } catch (StripeException e) {
            throw new IllegalStateException("Failed to sync Stripe account status", e);
        }
    }

    @Transactional
    public void updateAccountFromWebhook(Account stripeAccount) {
        StripeConnectAccount account = stripeConnectAccountRepository
                .findByStripeAccountId(stripeAccount.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Stripe account not found: " + stripeAccount.getId()
                ));

        syncFromStripe(account, stripeAccount);
        stripeConnectAccountRepository.save(account);
    }

    public boolean isAccountReady(User user) {
        StripeConnectAccount account = stripeConnectAccountRepository.findByUser_Id(user.getId())
                .orElse(null);
        
        return account != null && account.isReady();
    }


    public List<String> getRequirements(User user) {
        StripeConnectAccount account = getByUser(user);

        if (account.getStripeAccountId() == null) {
            return List.of();
        }

        try {
            Account stripeAccount = Account.retrieve(account.getStripeAccountId());
            
            if (stripeAccount.getRequirements() == null) {
                return List.of();
            }

            List<String> requirements = stripeAccount.getRequirements().getCurrentlyDue();
            return requirements != null ? requirements : List.of();
        } catch (StripeException e) {
            throw new IllegalStateException("Failed to get Stripe account requirements", e);
        }
    }

    // ========================
    // PRIVATE HELPERS
    // ========================

    private void syncFromStripe(StripeConnectAccount account, Account stripeAccount) {
        account.setChargesEnabled(Boolean.TRUE.equals(stripeAccount.getChargesEnabled()));
        account.setPayoutsEnabled(Boolean.TRUE.equals(stripeAccount.getPayoutsEnabled()));
        account.setDetailsSubmitted(Boolean.TRUE.equals(stripeAccount.getDetailsSubmitted()));
        account.setRequirementsDue(hasRequirements(stripeAccount));
    }

    private boolean hasRequirements(Account stripeAccount) {
        if (stripeAccount.getRequirements() == null) return false;

        return (stripeAccount.getRequirements().getCurrentlyDue() != null &&
                !stripeAccount.getRequirements().getCurrentlyDue().isEmpty())
                || (stripeAccount.getRequirements().getPastDue() != null &&
                !stripeAccount.getRequirements().getPastDue().isEmpty());
    }

    private StripeConnectAccount newAccount(User user) {
        return StripeConnectAccount.builder()
                .user(user)
                .chargesEnabled(false)
                .payoutsEnabled(false)
                .detailsSubmitted(false)
                .requirementsDue(false)
                .build();
    }
}