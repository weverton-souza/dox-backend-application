package com.dox.application.service

import com.dox.application.port.input.ActivateModuleCommand
import com.dox.application.port.input.BillingUseCase
import com.dox.application.port.input.BundleUseCase
import com.dox.application.port.input.CancelSubscriptionCommand
import com.dox.application.port.input.CustomerProfile
import com.dox.application.port.input.ModuleAccessUseCase
import com.dox.application.port.input.SubscribeBundleCommand
import com.dox.application.port.input.SubscribeModulesCommand
import com.dox.application.port.input.TokenizeCreditCardCommand
import com.dox.application.port.input.TokenizedCard
import com.dox.application.port.input.UpdateCustomerProfileCommand
import com.dox.application.port.output.AddonPersistencePort
import com.dox.application.port.output.AsaasCustomerPersistencePort
import com.dox.application.port.output.BillingPort
import com.dox.application.port.output.BundlePricePersistencePort
import com.dox.application.port.output.CreateAsaasCustomerCommand
import com.dox.application.port.output.CreateAsaasSubscriptionCommand
import com.dox.application.port.output.NfseInvoicePersistencePort
import com.dox.application.port.output.OrganizationPersistencePort
import com.dox.application.port.output.PaymentMethodCardPersistencePort
import com.dox.application.port.output.PaymentPersistencePort
import com.dox.application.port.output.SubscriptionPersistencePort
import com.dox.application.port.output.TenantAddonPersistencePort
import com.dox.application.port.output.TokenizeCardCommand
import com.dox.application.port.output.TokenizeCardHolderInfo
import com.dox.application.port.output.UpdateAsaasCustomerCommand
import com.dox.application.port.output.UpdateAsaasSubscriptionCommand
import com.dox.domain.billing.AddonType
import com.dox.domain.billing.AsaasCustomer
import com.dox.domain.billing.BillingCalculator
import com.dox.domain.billing.BillingCycle
import com.dox.domain.billing.BillingType
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
import com.dox.domain.billing.TenantAddon
import com.dox.domain.enum.MemberRole
import com.dox.domain.exception.AccessDeniedException
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.extensions.sanitizeDocument
import com.dox.shared.ContextHolder
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
    private val bundlePricePort: BundlePricePersistencePort,
    private val moduleAccessUseCase: ModuleAccessUseCase,
    private val addonPersistencePort: AddonPersistencePort,
    private val tenantAddonPort: TenantAddonPersistencePort,
    private val organizationPersistencePort: OrganizationPersistencePort,
) : BillingUseCase {
    @Transactional
    override fun subscribeBundle(command: SubscribeBundleCommand): Subscription {
        ensureNoActiveSubscription(command.tenantId)
        validatePaymentMethod(command.billingType, command.cycle)
        val bundle =
            bundleUseCase.getById(command.bundleId)
                ?: throw ResourceNotFoundException("Bundle", command.bundleId)
        val modules = bundleUseCase.expandToModules(bundle.id)
        val price = priceForCycle(bundle.priceMonthlyCents, bundle.priceYearlyCents, command.cycle)
        val currentBundlePrice = bundlePricePort.findCurrent(bundle.id)
        return subscribeAndActivate(
            tenantId = command.tenantId,
            customerName = command.customerName,
            customerCpfCnpj = command.customerCpfCnpj,
            customerEmail = command.customerEmail,
            customerMobilePhone = command.customerMobilePhone,
            customerPostalCode = command.customerPostalCode,
            customerAddress = command.customerAddress,
            customerAddressNumber = command.customerAddressNumber,
            customerAddressComplement = command.customerAddressComplement,
            customerProvince = command.customerProvince,
            modules = modules,
            cycle = command.cycle,
            billingType = command.billingType,
            valueCents = price,
            creditCardToken = command.creditCardToken,
            description = "Plano ${bundle.name}",
            source = ModuleSource.BUNDLE,
            sourceId = bundle.id,
            bundlePriceId = currentBundlePrice?.id,
        )
    }

    @Transactional
    override fun subscribeModules(command: SubscribeModulesCommand): Subscription {
        ensureNoActiveSubscription(command.tenantId)
        validatePaymentMethod(command.billingType, command.cycle)
        val modules = command.moduleIds.map { id -> Module.fromId(id) ?: throw BusinessException("Módulo '$id' não existe") }.toSet()
        val breakdown = BillingCalculator.breakdown(modules, command.cycle)
        return subscribeAndActivate(
            tenantId = command.tenantId,
            customerName = command.customerName,
            customerCpfCnpj = command.customerCpfCnpj,
            customerEmail = command.customerEmail,
            customerMobilePhone = command.customerMobilePhone,
            customerPostalCode = command.customerPostalCode,
            customerAddress = command.customerAddress,
            customerAddressNumber = command.customerAddressNumber,
            customerAddressComplement = command.customerAddressComplement,
            customerProvince = command.customerProvince,
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
    override fun addAddon(
        tenantId: UUID,
        addonId: String,
        quantity: Int,
    ): Subscription {
        if (quantity < 1) throw BusinessException("Quantidade deve ser pelo menos 1")
        requireOrgOwner(tenantId)
        val subscription = requireActiveSubscription(tenantId)
        val addon =
            addonPersistencePort.findById(addonId)
                ?: throw ResourceNotFoundException("Add-on", addonId)
        if (!addon.active) throw BusinessException("Add-on '$addonId' não está disponível")
        val unitPrice =
            addon.priceUnitCents
                ?: throw BusinessException("Add-on '$addonId' não é cobrado por unidade")
        val bundleId = subscription.bundlePriceId?.let { bundlePricePort.findById(it)?.bundleId }
        if (addon.availableForBundles.isNotEmpty() && bundleId !in addon.availableForBundles) {
            throw BusinessException("O add-on '$addonId' não está disponível para o seu plano")
        }

        val existing = tenantAddonPort.findByTenantAndAddon(tenantId, addonId)
        val newQuantity = if (existing != null && existing.canceledAt == null) existing.quantity + quantity else quantity
        val basePrice = unitPrice * newQuantity
        tenantAddonPort.save(
            existing?.copy(
                quantity = newQuantity,
                canceledAt = null,
                basePriceCents = basePrice,
                finalPriceCents = 0,
            ) ?: TenantAddon(
                tenantId = tenantId,
                addonId = addonId,
                quantity = newQuantity,
                activatedAt = LocalDateTime.now(),
                basePriceCents = basePrice,
                finalPriceCents = 0,
            ),
        )

        val newValue = subscription.valueCents + unitPrice * quantity * subscription.billingCycle.months
        chargeProration(subscription, unitPrice * quantity, "Assento adicional")
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
    override fun removeAddon(
        tenantId: UUID,
        addonId: String,
        quantity: Int,
    ): Subscription {
        if (quantity < 1) throw BusinessException("Quantidade deve ser pelo menos 1")
        requireOrgOwner(tenantId)
        val subscription = requireActiveSubscription(tenantId)
        val addon =
            addonPersistencePort.findById(addonId)
                ?: throw ResourceNotFoundException("Add-on", addonId)
        val unitPrice =
            addon.priceUnitCents
                ?: throw BusinessException("Add-on '$addonId' não é cobrado por unidade")
        val existing = tenantAddonPort.findByTenantAndAddon(tenantId, addonId)
        if (existing == null || existing.canceledAt != null || existing.quantity < 1) {
            throw BusinessException("Não há '$addonId' ativo para remover")
        }
        val removeQty = quantity.coerceAtMost(existing.quantity)
        val newQuantity = existing.quantity - removeQty

        if (addon.type == AddonType.SEAT_QUOTA) {
            val org = organizationPersistencePort.findByTenantId(tenantId)
            if (org != null) {
                val bundleSeats =
                    subscription.bundlePriceId?.let { bundlePricePort.findById(it)?.seatsIncluded } ?: 1
                val members = organizationPersistencePort.countMembers(org.id)
                if (bundleSeats + newQuantity < members) {
                    throw BusinessException(
                        "Não é possível reduzir assentos: a organização tem $members membros. Remova profissionais antes.",
                    )
                }
            }
        }

        tenantAddonPort.save(
            existing.copy(
                quantity = newQuantity,
                canceledAt = if (newQuantity == 0) LocalDateTime.now() else null,
                basePriceCents = unitPrice * newQuantity,
                finalPriceCents = 0,
            ),
        )

        val newValue =
            (subscription.valueCents - unitPrice * removeQty * subscription.billingCycle.months).coerceAtLeast(0)
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

    override fun listPaymentMethods(tenantId: UUID): List<PaymentMethodCard> = paymentMethodCardPort.findByTenantId(tenantId)

    @Transactional
    override fun setDefaultPaymentMethod(
        tenantId: UUID,
        cardId: UUID,
    ): PaymentMethodCard {
        val cards = paymentMethodCardPort.findByTenantId(tenantId).toMutableList()
        val target =
            cards.find { it.id == cardId }
                ?: throw ResourceNotFoundException("PaymentMethodCard", cardId.toString())
        if (target.isDefault) return target
        paymentMethodCardPort.clearDefault(tenantId)
        cards.remove(target)
        cards.add(0, target)
        var promoted: PaymentMethodCard = target
        cards.forEachIndexed { idx, card ->
            val saved =
                paymentMethodCardPort.save(
                    card.copy(isDefault = idx == 0, displayOrder = idx),
                )
            if (idx == 0) promoted = saved
        }
        return promoted
    }

    @Transactional
    override fun deletePaymentMethod(
        tenantId: UUID,
        cardId: UUID,
    ) {
        val cards = paymentMethodCardPort.findByTenantId(tenantId).toMutableList()
        val target =
            cards.find { it.id == cardId }
                ?: throw ResourceNotFoundException("PaymentMethodCard", cardId.toString())
        cards.remove(target)
        if (target.isDefault) {
            paymentMethodCardPort.clearDefault(tenantId)
        }
        paymentMethodCardPort.delete(cardId)
        cards.forEachIndexed { idx, card ->
            paymentMethodCardPort.save(
                card.copy(isDefault = idx == 0, displayOrder = idx),
            )
        }
    }

    override fun getCustomerProfile(tenantId: UUID): CustomerProfile? {
        val local = asaasCustomerPort.findByTenantId(tenantId) ?: return null
        return CustomerProfile(
            name = local.name,
            email = local.email,
            cpfCnpj = local.cpfCnpj,
            mobilePhone = local.billingMobilePhone,
            postalCode = local.billingPostalCode,
            address = local.billingAddress,
            addressNumber = local.billingAddressNumber,
            complement = local.billingComplement,
            province = local.billingProvince,
        )
    }

    @Transactional
    override fun updateCustomerProfile(
        tenantId: UUID,
        command: UpdateCustomerProfileCommand,
    ): CustomerProfile {
        val existing =
            asaasCustomerPort.findByTenantId(tenantId)
                ?: throw BusinessException("Cadastre uma assinatura antes de configurar o endereço de cobrança")
        billingPort.updateCustomer(
            UpdateAsaasCustomerCommand(
                asaasCustomerId = existing.asaasCustomerId,
                name = command.name,
                email = command.email,
                cpfCnpj = command.cpfCnpj,
                mobilePhone = command.mobilePhone,
                postalCode = command.postalCode,
                address = command.address,
                addressNumber = command.addressNumber,
                complement = command.complement,
                province = command.province,
            ),
        )
        val saved =
            asaasCustomerPort.save(
                existing.copy(
                    name = command.name,
                    email = command.email,
                    cpfCnpj = command.cpfCnpj.sanitizeDocument(),
                    billingMobilePhone = command.mobilePhone.filter { it.isDigit() },
                    billingPostalCode = command.postalCode.filter { it.isDigit() },
                    billingAddress = command.address,
                    billingAddressNumber = command.addressNumber,
                    billingComplement = command.complement,
                    billingProvince = command.province,
                ),
            )
        return CustomerProfile(
            name = saved.name,
            email = saved.email,
            cpfCnpj = saved.cpfCnpj,
            mobilePhone = saved.billingMobilePhone,
            postalCode = saved.billingPostalCode,
            address = saved.billingAddress,
            addressNumber = saved.billingAddressNumber,
            complement = saved.billingComplement,
            province = saved.billingProvince,
        )
    }

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
                ?: findOrCreateBasicAsaasCustomer(
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
        val existingCards = paymentMethodCardPort.findByTenantId(command.tenantId)
        if (command.makeDefault) {
            paymentMethodCardPort.clearDefault(command.tenantId)
            existingCards.forEach {
                paymentMethodCardPort.save(it.copy(isDefault = false, displayOrder = it.displayOrder + 1))
            }
        }
        val newDisplayOrder = if (command.makeDefault) 0 else existingCards.size
        paymentMethodCardPort.save(
            PaymentMethodCard(
                tenantId = command.tenantId,
                asaasCreditCardToken = tokenized.creditCardToken,
                brand = tokenized.brand,
                last4 = tokenized.last4,
                holderName = command.cardHolderName,
                isDefault = command.makeDefault,
                displayOrder = newDisplayOrder,
                expiresAt = parseCardExpiry(command.cardExpiryMonth, command.cardExpiryYear),
            ),
        )
        return TokenizedCard(token = tokenized.creditCardToken, brand = tokenized.brand, last4 = tokenized.last4)
    }

    private fun parseCardExpiry(
        month: String,
        year: String,
    ): LocalDate? {
        val m = month.toIntOrNull() ?: return null
        val y = year.toIntOrNull() ?: return null
        if (m !in 1..12) return null
        return runCatching { LocalDate.of(y, m, 1).withDayOfMonth(LocalDate.of(y, m, 1).lengthOfMonth()) }.getOrNull()
    }

    private fun subscribeAndActivate(
        tenantId: UUID,
        customerName: String,
        customerCpfCnpj: String,
        customerEmail: String?,
        customerMobilePhone: String,
        customerPostalCode: String,
        customerAddress: String,
        customerAddressNumber: String,
        customerAddressComplement: String?,
        customerProvince: String,
        modules: Set<Module>,
        cycle: BillingCycle,
        billingType: com.dox.domain.billing.BillingType,
        valueCents: Int,
        creditCardToken: String?,
        description: String,
        source: ModuleSource,
        sourceId: String?,
        bundlePriceId: UUID? = null,
    ): Subscription {
        val asaasCustomer =
            ensureAsaasCustomer(
                tenantId = tenantId,
                name = customerName,
                cpfCnpj = customerCpfCnpj,
                email = customerEmail,
                mobilePhone = customerMobilePhone,
                postalCode = customerPostalCode,
                address = customerAddress,
                addressNumber = customerAddressNumber,
                complement = customerAddressComplement,
                province = customerProvince,
            )
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
                bundlePriceId = bundlePriceId,
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

    private fun findOrCreateBasicAsaasCustomer(
        tenantId: UUID,
        name: String,
        cpfCnpj: String,
        email: String?,
    ): AsaasCustomer {
        val result =
            billingPort.createCustomer(
                CreateAsaasCustomerCommand(name = name, email = email, cpfCnpj = cpfCnpj),
            )
        return asaasCustomerPort.save(
            AsaasCustomer(
                tenantId = tenantId,
                asaasCustomerId = result.asaasCustomerId,
                cpfCnpj = cpfCnpj.sanitizeDocument(),
                email = email,
                name = name,
            ),
        )
    }

    private fun ensureAsaasCustomer(
        tenantId: UUID,
        name: String,
        cpfCnpj: String,
        email: String?,
        mobilePhone: String,
        postalCode: String,
        address: String,
        addressNumber: String,
        complement: String?,
        province: String,
    ): AsaasCustomer {
        asaasCustomerPort.findByTenantId(tenantId)?.let { existing ->
            billingPort.updateCustomer(
                UpdateAsaasCustomerCommand(
                    asaasCustomerId = existing.asaasCustomerId,
                    name = name,
                    email = email,
                    cpfCnpj = cpfCnpj,
                    mobilePhone = mobilePhone,
                    postalCode = postalCode,
                    address = address,
                    addressNumber = addressNumber,
                    complement = complement,
                    province = province,
                ),
            )
            return asaasCustomerPort.save(
                existing.copy(
                    name = name,
                    email = email,
                    cpfCnpj = cpfCnpj.sanitizeDocument(),
                    billingMobilePhone = mobilePhone.filter { it.isDigit() },
                    billingPostalCode = postalCode.filter { it.isDigit() },
                    billingAddress = address,
                    billingAddressNumber = addressNumber,
                    billingComplement = complement,
                    billingProvince = province,
                ),
            )
        }
        val result =
            billingPort.createCustomer(
                CreateAsaasCustomerCommand(
                    name = name,
                    email = email,
                    cpfCnpj = cpfCnpj,
                    mobilePhone = mobilePhone,
                    postalCode = postalCode,
                    address = address,
                    addressNumber = addressNumber,
                    complement = complement,
                    province = province,
                ),
            )
        return asaasCustomerPort.save(
            AsaasCustomer(
                tenantId = tenantId,
                asaasCustomerId = result.asaasCustomerId,
                cpfCnpj = cpfCnpj.sanitizeDocument(),
                email = email,
                name = name,
                billingMobilePhone = mobilePhone.filter { it.isDigit() },
                billingPostalCode = postalCode.filter { it.isDigit() },
                billingAddress = address,
                billingAddressNumber = addressNumber,
                billingComplement = complement,
                billingProvince = province,
            ),
        )
    }

    private fun ensureNoActiveSubscription(tenantId: UUID) {
        val existing = subscriptionPort.findByTenantId(tenantId)
        if (existing != null && existing.status !in TERMINAL_STATUSES) {
            throw BusinessException("Tenant já possui subscription ativa (status=${existing.status})")
        }
    }

    private fun validatePaymentMethod(
        billingType: BillingType,
        cycle: BillingCycle,
    ) {
        when (billingType) {
            BillingType.BOLETO -> throw BusinessException("Pagamento via boleto não é suportado")
            BillingType.UNDEFINED -> throw BusinessException("Método de pagamento é obrigatório")
            BillingType.PIX ->
                if (cycle == BillingCycle.MONTHLY) {
                    throw BusinessException("PIX só pode ser usado em planos com cobrança única (anual, semestral ou trimestral). Para mensal, use cartão.")
                }
            BillingType.CREDIT_CARD -> Unit
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

    private fun requireOrgOwner(tenantId: UUID) {
        val org = organizationPersistencePort.findByTenantId(tenantId) ?: return
        val member = organizationPersistencePort.findMember(org.id, ContextHolder.getUserIdOrThrow())
        if (member?.role != MemberRole.OWNER) {
            throw AccessDeniedException("Apenas o OWNER da organização pode gerenciar assentos")
        }
    }

    private fun chargeProrationIfNeeded(
        subscription: Subscription,
        module: Module,
    ) = chargeProration(subscription, module.basePriceMonthlyCents, module.displayName)

    private fun chargeProration(
        subscription: Subscription,
        monthlyAmountCents: Int,
        label: String,
    ) {
        val periodEnd = subscription.currentPeriodEnd ?: return
        val today = LocalDate.now()
        val end = periodEnd.toLocalDate()
        val daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, end).toInt()
        if (daysRemaining <= 0) return
        val prorationCents = BillingCalculator.computeProration(monthlyAmountCents, daysRemaining)
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
                description = "Proporcional $label ($daysRemaining dias)",
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
