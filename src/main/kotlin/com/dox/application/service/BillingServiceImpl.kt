package com.dox.application.service

import com.dox.application.port.input.ActivateModuleCommand
import com.dox.application.port.input.BillingUseCase
import com.dox.application.port.input.BundleUseCase
import com.dox.application.port.input.CancelSubscriptionCommand
import com.dox.application.port.input.ModuleAccessUseCase
import com.dox.application.port.input.SubscribeBundleCommand
import com.dox.application.port.input.SubscribeModulesCommand
import com.dox.application.port.input.TokenizeCreditCardCommand
import com.dox.application.port.input.TokenizedCard
import com.dox.application.port.output.AsaasCustomerPersistencePort
import com.dox.application.port.output.BillingPort
import com.dox.application.port.output.CreateAsaasCustomerCommand
import com.dox.application.port.output.CreateAsaasSubscriptionCommand
import com.dox.application.port.output.NfseInvoicePersistencePort
import com.dox.application.port.output.PaymentMethodCardPersistencePort
import com.dox.application.port.output.PaymentPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.application.port.output.TokenizeCardCommand
import com.dox.application.port.output.TokenizeCardHolderInfo
import com.dox.application.port.output.UpdateAsaasSubscriptionCommand
import com.dox.domain.billing.AsaasCustomer
import com.dox.domain.billing.BillingCalculator
import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.Module
import com.dox.domain.billing.ModuleSource
import com.dox.domain.billing.NfseInvoice
import com.dox.domain.billing.Payment
import com.dox.domain.billing.PaymentMethodCard
import com.dox.domain.billing.PriceBreakdown
import com.dox.domain.billing.Subscription
import com.dox.domain.billing.SubscriptionEvent
import com.dox.domain.billing.SubscriptionStateMachine
import com.dox.domain.billing.SubscriptionStatus
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class BillingServiceImpl(
    private val billingPort: BillingPort,
    private val asaasCustomerPort: AsaasCustomerPersistencePort,
    private val subscriptionPort: SubscriptionPersistencePort,
    private val paymentPort: PaymentPersistencePort,
    private val invoicePort: NfseInvoicePersistencePort,
    private val paymentMethodCardPort: PaymentMethodCardPersistencePort,
    private val bundleUseCase: BundleUseCase,
    private val moduleAccessUseCase: ModuleAccessUseCase,
) : BillingUseCase {
    @Transactional
    override fun subscribeBundle(command: SubscribeBundleCommand): Subscription {
        ensureNoActiveSubscription(command.tenantId)
        val bundle =
            bundleUseCase.getById(command.bundleId)
                ?: throw ResourceNotFoundException("Bundle", command.bundleId)
        val modules = bundleUseCase.expandToModules(bundle.id)
        val price = priceForCycle(bundle.priceMonthlyCents, bundle.priceYearlyCents, command.cycle)
        return subscribeAndActivate(
            tenantId = command.tenantId,
            customerName = command.customerName,
            customerCpfCnpj = command.customerCpfCnpj,
            customerEmail = command.customerEmail,
            modules = modules,
            cycle = command.cycle,
            billingType = command.billingType,
            valueCents = price,
            creditCardToken = command.creditCardToken,
            description = "Plano ${bundle.name}",
            source = ModuleSource.BUNDLE,
            sourceId = bundle.id,
        )
    }

    @Transactional
    override fun subscribeModules(command: SubscribeModulesCommand): Subscription {
        ensureNoActiveSubscription(command.tenantId)
        val modules = command.moduleIds.map { id -> Module.fromId(id) ?: throw BusinessException("Módulo '$id' não existe") }.toSet()
        val breakdown = BillingCalculator.breakdown(modules, command.cycle)
        return subscribeAndActivate(
            tenantId = command.tenantId,
            customerName = command.customerName,
            customerCpfCnpj = command.customerCpfCnpj,
            customerEmail = command.customerEmail,
            modules = modules,
            cycle = command.cycle,
            billingType = command.billingType,
            valueCents = breakdown.finalPriceCents,
            creditCardToken = command.creditCardToken,
            description = "Assinatura módulos: ${modules.joinToString(", ") { it.displayName }}",
            source = ModuleSource.INDIVIDUAL,
            sourceId = null,
        )
    }

    @Transactional
    override fun addModule(
        tenantId: UUID,
        moduleId: String,
    ): Subscription {
        val subscription = requireActiveSubscription(tenantId)
        val module = Module.fromId(moduleId) ?: throw BusinessException("Módulo '$moduleId' não existe")
        if (moduleAccessUseCase.hasAccess(tenantId, moduleId)) {
            throw BusinessException("Módulo '$moduleId' já está ativo")
        }
        moduleAccessUseCase.activate(
            ActivateModuleCommand(tenantId = tenantId, moduleId = moduleId, source = ModuleSource.INDIVIDUAL),
        )
        val activeModules = moduleAccessUseCase.getActiveModules(tenantId)
        val newValue = BillingCalculator.computeForCycle(activeModules, subscription.billingCycle)
        chargeProrationIfNeeded(subscription, module)
        billingPort.updateSubscription(
            UpdateAsaasSubscriptionCommand(
                asaasSubscriptionId =
                    subscription.asaasSubscriptionId
                        ?: throw BusinessException("Subscription sem id Asaas"),
                newValueCents = newValue,
                updatePendingPayments = true,
            ),
        )
        return subscriptionPort.save(subscription.copy(valueCents = newValue))
    }

    @Transactional
    override fun removeModule(
        tenantId: UUID,
        moduleId: String,
    ): Subscription {
        val subscription = requireActiveSubscription(tenantId)
        Module.fromId(moduleId) ?: throw BusinessException("Módulo '$moduleId' não existe")
        moduleAccessUseCase.deactivate(tenantId, moduleId, "Removido pelo cliente")
        val activeModules = moduleAccessUseCase.getActiveModules(tenantId)
        val newValue = BillingCalculator.computeForCycle(activeModules, subscription.billingCycle)
        billingPort.updateSubscription(
            UpdateAsaasSubscriptionCommand(
                asaasSubscriptionId =
                    subscription.asaasSubscriptionId
                        ?: throw BusinessException("Subscription sem id Asaas"),
                newValueCents = newValue,
            ),
        )
        return subscriptionPort.save(subscription.copy(valueCents = newValue))
    }

    @Transactional
    override fun cancelSubscription(command: CancelSubscriptionCommand): Subscription {
        val subscription = requireActiveSubscription(command.tenantId)
        val asaasId = subscription.asaasSubscriptionId ?: throw BusinessException("Subscription sem id Asaas")
        billingPort.cancelSubscription(asaasId)
        val newStatus = SubscriptionStateMachine.transition(subscription.status, SubscriptionEvent.CANCEL)
        val now = LocalDateTime.now()
        return subscriptionPort.save(
            subscription.copy(
                status = newStatus,
                canceledAt = now,
                cancelEffectiveAt = subscription.currentPeriodEnd ?: now,
                cancelReason = command.reason,
            ),
        )
    }

    @Transactional
    override fun reactivateSubscription(tenantId: UUID): Subscription {
        val subscription =
            subscriptionPort.findByTenantId(tenantId)
                ?: throw ResourceNotFoundException("Subscription")
        val newStatus = SubscriptionStateMachine.transition(subscription.status, SubscriptionEvent.REACTIVATE)
        return subscriptionPort.save(
            subscription.copy(
                status = newStatus,
                canceledAt = null,
                cancelEffectiveAt = null,
                cancelReason = null,
            ),
        )
    }

    override fun getSubscription(tenantId: UUID): Subscription? = subscriptionPort.findByTenantId(tenantId)

    override fun listPayments(
        tenantId: UUID,
        from: LocalDate?,
        to: LocalDate?,
    ): List<Payment> {
        if (from != null && to != null) {
            return paymentPort.findByTenantIdAndDueDateBetween(tenantId, from, to)
        }
        return paymentPort.findByTenantId(tenantId)
    }

    override fun listInvoices(tenantId: UUID): List<NfseInvoice> = invoicePort.findByTenantId(tenantId)

    override fun pricePreview(
        moduleIds: List<String>,
        cycle: BillingCycle,
        bundleId: String?,
    ): PriceBreakdown {
        val modules = moduleIds.mapNotNull { Module.fromId(it) }.toSet()
        val bundlePrice =
            bundleId?.let {
                val bundle = bundleUseCase.getById(it) ?: throw ResourceNotFoundException("Bundle", it)
                priceForCycle(bundle.priceMonthlyCents, bundle.priceYearlyCents, cycle)
            }
        return BillingCalculator.breakdown(modules, cycle, bundlePrice)
    }

    @Transactional
    override fun tokenizeCreditCard(command: TokenizeCreditCardCommand): TokenizedCard {
        val asaasCustomer =
            asaasCustomerPort.findByTenantId(command.tenantId)
                ?: ensureAsaasCustomer(
                    tenantId = command.tenantId,
                    name = command.billingName,
                    cpfCnpj = command.billingCpfCnpj,
                    email = command.billingEmail,
                )
        val tokenized =
            billingPort.tokenizeCard(
                TokenizeCardCommand(
                    asaasCustomerId = asaasCustomer.asaasCustomerId,
                    holderName = command.cardHolderName,
                    number = command.cardNumber,
                    expiryMonth = command.cardExpiryMonth,
                    expiryYear = command.cardExpiryYear,
                    ccv = command.cardCcv,
                    holderInfo =
                        TokenizeCardHolderInfo(
                            name = command.billingName,
                            email = command.billingEmail,
                            cpfCnpj = command.billingCpfCnpj,
                            postalCode = command.billingPostalCode,
                            addressNumber = command.billingAddressNumber,
                            addressComplement = command.billingAddressComplement,
                            phone = command.billingPhone,
                            mobilePhone = command.billingMobilePhone,
                        ),
                    remoteIp = command.remoteIp,
                ),
            )
        if (command.makeDefault) {
            paymentMethodCardPort.findByTenantId(command.tenantId).filter { it.isDefault }.forEach {
                paymentMethodCardPort.save(it.copy(isDefault = false))
            }
        }
        paymentMethodCardPort.save(
            PaymentMethodCard(
                tenantId = command.tenantId,
                asaasCreditCardToken = tokenized.creditCardToken,
                brand = tokenized.brand,
                last4 = tokenized.last4,
                holderName = command.cardHolderName,
                isDefault = command.makeDefault,
            ),
        )
        return TokenizedCard(token = tokenized.creditCardToken, brand = tokenized.brand, last4 = tokenized.last4)
    }

    private fun subscribeAndActivate(
        tenantId: UUID,
        customerName: String,
        customerCpfCnpj: String,
        customerEmail: String?,
        modules: Set<Module>,
        cycle: BillingCycle,
        billingType: com.dox.domain.billing.BillingType,
        valueCents: Int,
        creditCardToken: String?,
        description: String,
        source: ModuleSource,
        sourceId: String?,
    ): Subscription {
        val asaasCustomer = ensureAsaasCustomer(tenantId, customerName, customerCpfCnpj, customerEmail)
        val nextDueDate = LocalDate.now().plusDays(7)
        val asaasSub =
            billingPort.createSubscription(
                CreateAsaasSubscriptionCommand(
                    asaasCustomerId = asaasCustomer.asaasCustomerId,
                    billingType = billingType,
                    cycle = cycle,
                    nextDueDate = nextDueDate,
                    valueCents = valueCents,
                    description = description,
                    creditCardToken = creditCardToken,
                ),
            )
        val now = LocalDateTime.now()
        val subscription =
            Subscription(
                tenantId = tenantId,
                asaasSubscriptionId = asaasSub.asaasSubscriptionId,
                status = SubscriptionStatus.ACTIVE,
                billingCycle = cycle,
                billingType = billingType,
                valueCents = valueCents,
                currentPeriodStart = now,
                currentPeriodEnd = now.plusMonths(cycle.months.toLong()),
                nextDueDate = asaasSub.nextDueDate,
            )
        val saved = subscriptionPort.save(subscription)
        modules.forEach { module ->
            moduleAccessUseCase.activate(
                ActivateModuleCommand(
                    tenantId = tenantId,
                    moduleId = module.id,
                    source = source,
                    sourceId = sourceId,
                ),
            )
        }
        return saved
    }

    private fun ensureAsaasCustomer(
        tenantId: UUID,
        name: String,
        cpfCnpj: String,
        email: String?,
    ): AsaasCustomer {
        asaasCustomerPort.findByTenantId(tenantId)?.let { return it }
        val result = billingPort.createCustomer(CreateAsaasCustomerCommand(name = name, email = email, cpfCnpj = cpfCnpj))
        return asaasCustomerPort.save(
            AsaasCustomer(
                tenantId = tenantId,
                asaasCustomerId = result.asaasCustomerId,
                cpfCnpj = cpfCnpj.filter { it.isDigit() },
                email = email,
                name = name,
            ),
        )
    }

    private fun ensureNoActiveSubscription(tenantId: UUID) {
        val existing = subscriptionPort.findByTenantId(tenantId)
        if (existing != null && existing.status !in TERMINAL_STATUSES) {
            throw BusinessException("Tenant já possui subscription ativa (status=${existing.status})")
        }
    }

    private fun requireActiveSubscription(tenantId: UUID): Subscription {
        val subscription =
            subscriptionPort.findByTenantId(tenantId)
                ?: throw ResourceNotFoundException("Subscription")
        if (subscription.status in TERMINAL_STATUSES) {
            throw BusinessException("Subscription não está ativa (status=${subscription.status})")
        }
        return subscription
    }

    private fun chargeProrationIfNeeded(
        subscription: Subscription,
        module: Module,
    ) {
        val periodEnd = subscription.currentPeriodEnd ?: return
        val today = LocalDate.now()
        val end = periodEnd.toLocalDate()
        val daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, end).toInt()
        if (daysRemaining <= 0) return
        val prorationCents = BillingCalculator.computeProration(module.basePriceMonthlyCents, daysRemaining)
        if (prorationCents <= 0) return
        val asaasCustomer =
            asaasCustomerPort.findByTenantId(subscription.tenantId)
                ?: throw BusinessException("Tenant sem AsaasCustomer cadastrado")
        billingPort.createOneTimePayment(
            com.dox.application.port.output.CreateOneTimePaymentCommand(
                asaasCustomerId = asaasCustomer.asaasCustomerId,
                billingType = subscription.billingType,
                valueCents = prorationCents,
                dueDate = today.plusDays(7),
                description = "Proporcional ${module.displayName} ($daysRemaining dias)",
            ),
        )
    }

    private fun priceForCycle(
        monthlyCents: Int,
        yearlyCents: Int,
        cycle: BillingCycle,
    ): Int =
        when (cycle) {
            BillingCycle.MONTHLY -> monthlyCents
            BillingCycle.YEARLY -> yearlyCents
            BillingCycle.QUARTERLY -> monthlyCents * 3
            BillingCycle.SEMIANNUALLY -> monthlyCents * 6
        }

    companion object {
        private val TERMINAL_STATUSES = setOf(SubscriptionStatus.CANCELED)
    }
}
