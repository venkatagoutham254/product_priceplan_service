package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.repository.ProductRepository;
import aforo.productrateplanservice.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import aforo.productrateplanservice.flatfee.FlatFeeRepository;
import aforo.productrateplanservice.flatfee.FlatFeeMapper;
import aforo.productrateplanservice.tieredpricing.TieredPricingRepository;
import aforo.productrateplanservice.tieredpricing.TieredPricingMapper;
import aforo.productrateplanservice.volumepricing.VolumePricingRepository;
import aforo.productrateplanservice.volumepricing.VolumePricingMapper;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingRepository;
import aforo.productrateplanservice.usagebasedpricing.UsageBasedPricingMapper;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingRepository;
import aforo.productrateplanservice.stairsteppricing.StairStepPricingMapper;
import aforo.productrateplanservice.setupfee.SetupFeeRepository;
import aforo.productrateplanservice.setupfee.SetupFeeMapper;
import aforo.productrateplanservice.discount.DiscountRepository;
import aforo.productrateplanservice.discount.DiscountMapper;
import aforo.productrateplanservice.freemium.FreemiumRepository;
import aforo.productrateplanservice.freemium.FreemiumMapper;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentRepository;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentMapper;

@Service
@RequiredArgsConstructor
public class RatePlanServiceImpl implements RatePlanService {

    private final RatePlanRepository ratePlanRepository;
    private final ProductRepository productRepository;
    private final RatePlanMapper ratePlanMapper;
    private final RatePlanAssembler ratePlanAssembler;
    private final BillableMetricClient billableMetricClient;
    // Pricing repos + mappers
    private final FlatFeeRepository flatFeeRepository;
    private final FlatFeeMapper flatFeeMapper;
    private final TieredPricingRepository tieredPricingRepository;
    private final TieredPricingMapper tieredPricingMapper;
    private final VolumePricingRepository volumePricingRepository;
    private final VolumePricingMapper volumePricingMapper;
    private final UsageBasedPricingRepository usageBasedPricingRepository;
    private final UsageBasedPricingMapper usageBasedPricingMapper;
    private final StairStepPricingRepository stairStepPricingRepository;
    private final StairStepPricingMapper stairStepPricingMapper;
    // Extras repos + mappers
    private final SetupFeeRepository setupFeeRepository;
    private final SetupFeeMapper setupFeeMapper;
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final FreemiumRepository freemiumRepository;
    private final FreemiumMapper freemiumMapper;
    private final MinimumCommitmentRepository minimumCommitmentRepository;
    private final MinimumCommitmentMapper minimumCommitmentMapper;

    /**
     * Ensure the billableMetricId on a rate plan still points to an existing Billable Metric.
     * If the metric was deleted in the external service, we null out the reference and persist.
     */
    private RatePlan ensureMetricStillExists(RatePlan ratePlan) {
        Long metricId = ratePlan.getBillableMetricId();
        if (metricId == null) {
            return ratePlan;
        }
        boolean exists;
        try {
            exists = billableMetricClient.metricExists(metricId);
        } catch (Exception e) {
            // If validation service is down, don't mutate state on reads; just return as-is
            return ratePlan;
        }
        if (!exists) {
            ratePlan.setBillableMetricId(null);
            return ratePlanRepository.save(ratePlan);
        }
        return ratePlan;
    }

    private RatePlanDTO toDetailedDTO(RatePlan ratePlan) {
        RatePlanDTO dto = ratePlanMapper.toDTO(ratePlan);
        Long ratePlanId = dto.getRatePlanId();

        // Pricing configurations
        // FlatFee - at most one
        flatFeeRepository.findByRatePlanId(ratePlanId)
                .ifPresent(entity -> dto.setFlatFee(flatFeeMapper.toDTO(entity)));

        dto.setTieredPricings(
                tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(tieredPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setVolumePricings(
                volumePricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(volumePricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setUsageBasedPricings(
                usageBasedPricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(usageBasedPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        dto.setStairStepPricings(
                stairStepPricingRepository.findByRatePlanRatePlanId(ratePlanId).stream()
                        .map(stairStepPricingMapper::toDTO)
                        .collect(Collectors.toList())
        );

        // Extras
        dto.setSetupFees(
                setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(setupFeeMapper::toDTO)
                        .collect(Collectors.toList())
        );
        dto.setDiscounts(
                discountRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(discountMapper::toDTO)
                        .collect(Collectors.toList())
        );
        dto.setFreemiums(
                freemiumRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(freemiumMapper::toDTO)
                        .collect(Collectors.toList())
        );
        dto.setMinimumCommitments(
                minimumCommitmentRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                        .map(minimumCommitmentMapper::toDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public RatePlanDTO createRatePlan(CreateRatePlanRequest request) {
        Long orgId = TenantContext.require();
        Product product = null;
        if (request.getProductId() != null) {
            Long requestedProductId = request.getProductId();
            product = productRepository
                    .findByProductIdAndOrganizationId(requestedProductId, orgId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
        }
        // If metricId is provided, ensure it exists, is ACTIVE, and (when product known) belongs to that product
        if (request.getBillableMetricId() != null) {
            Long productId = (product != null && product.getProductId() != null) ? product.getProductId() : null;
            billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
        }
        RatePlanDTO dto = RatePlanDTO.builder()
                .ratePlanName(request.getRatePlanName())
                .description(request.getDescription())
                .billingFrequency(request.getBillingFrequency())
                .paymentType(request.getPaymentType())
                .billableMetricId(request.getBillableMetricId())
                .build();
        RatePlan ratePlan = ratePlanAssembler.toEntity(dto, product);
        ratePlan.setOrganizationId(orgId);
        ratePlan = ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    public List<RatePlanDTO> getAllRatePlans() {
        Long orgId = TenantContext.require();
        return ratePlanRepository.findAllByOrganizationId(orgId)
                .stream()
                .map(this::ensureMetricStillExists)
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanDTO> getRatePlansByProductId(Long productId) {
        Long orgId = TenantContext.require();
        return ratePlanRepository.findByProduct_ProductIdAndOrganizationId(productId, orgId)
                .stream()
                .map(this::ensureMetricStillExists)
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RatePlanDTO getRatePlanById(Long ratePlanId) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        ratePlan = ensureMetricStillExists(ratePlan);
        return toDetailedDTO(ratePlan);
    }

    @Override
    public void deleteRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        ratePlanRepository.deleteById(ratePlan.getRatePlanId());
    }

    @Override
    public RatePlanDTO updateRatePlanFully(Long ratePlanId, UpdateRatePlanRequest request) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

        if (request.getProductId() != null) {
            Long requestedProductId = request.getProductId();
            Product product = productRepository
                    .findByProductIdAndOrganizationId(requestedProductId, orgId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
            ratePlan.setProduct(product);
        }

        if (request.getRatePlanName() == null || request.getBillingFrequency() == null) {
            throw new ValidationException("All fields must be provided for full update.");
        }

        if (request.getBillableMetricId() != null) {
            Long productId = (ratePlan.getProduct() != null) ? ratePlan.getProduct().getProductId() : null;
            billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
            ratePlan.setBillableMetricId(request.getBillableMetricId());
        }

        ratePlan.setRatePlanName(request.getRatePlanName());
        ratePlan.setDescription(request.getDescription());
        ratePlan.setBillingFrequency(request.getBillingFrequency());
        ratePlan.setPaymentType(request.getPaymentType());

        ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    public RatePlanDTO updateRatePlanPartially(Long ratePlanId, UpdateRatePlanRequest request) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

        if (request.getProductId() != null) {
            Long requestedProductId = request.getProductId();
            Product product = productRepository
                    .findByProductIdAndOrganizationId(requestedProductId, orgId)
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + requestedProductId));
            ratePlan.setProduct(product);
        }

        if (request.getRatePlanName() != null) {
            ratePlan.setRatePlanName(request.getRatePlanName());
        }

        if (request.getDescription() != null) {
            ratePlan.setDescription(request.getDescription());
        }

        if (request.getBillingFrequency() != null) {
            ratePlan.setBillingFrequency(request.getBillingFrequency());
        }

        if (request.getPaymentType() != null) {
            ratePlan.setPaymentType(request.getPaymentType());
        }

        if (request.getBillableMetricId() != null) {
            Long productId = (ratePlan.getProduct() != null) ? ratePlan.getProduct().getProductId() : null;
            billableMetricClient.validateActiveForProduct(request.getBillableMetricId(), productId);
            ratePlan.setBillableMetricId(request.getBillableMetricId());
        }

        ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    @Transactional
    public RatePlanDTO confirmRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));

        if (ratePlan.getStatus() == RatePlanStatus.CONFIGURED
                || ratePlan.getStatus() == RatePlanStatus.LIVE) {
            throw new IllegalStateException("RatePlan is already confirmed");
        }

        // enforce billableMetricId must exist before activating
        if (ratePlan.getBillableMetricId() == null) {
            throw new ValidationException("Billable Metric ID is required before finalizing a RatePlan.");
        }
        
        // validate with external service: must be finalized and (if product known) belong to product
        Long productId = (ratePlan.getProduct() != null) ? ratePlan.getProduct().getProductId() : null;
        try {
            billableMetricClient.validateActiveForProduct(ratePlan.getBillableMetricId(), productId);
        } catch (aforo.productrateplanservice.exception.ValidationException vex) {
            String msg = vex.getMessage() == null ? "" : vex.getMessage().toLowerCase();
            // If upstream is slow/unavailable, proceed; but keep failing for real validation problems
            boolean upstreamIssue =
                    msg.contains("failed to fetch") ||
                    msg.contains("timeout") ||
                    msg.contains("service unavailable") ||
                    msg.contains("validation failed for id");
            boolean hardValidation =
                    msg.contains("invalid billablemetricid") ||
                    msg.contains("not finalized") ||
                    msg.contains("not ready") ||
                    msg.contains("does not belong");
            if (!upstreamIssue || hardValidation) {
                throw vex;
            }
            // else: best-effort confirm during upstream outage
        }
    
        ratePlan.setStatus(RatePlanStatus.CONFIGURED);
        ratePlan = ratePlanRepository.save(ratePlan);

        return toDetailedDTO(ratePlan);
    }
    
    @Override
    public void deleteByBillableMetricId(Long billableMetricId) {
        Long orgId = TenantContext.require();
        // Best-effort cleanup of rate plans pointing to this metric for the current tenant
        ratePlanRepository.deleteByBillableMetricIdAndOrganizationId(billableMetricId, orgId);
    }
    
}
