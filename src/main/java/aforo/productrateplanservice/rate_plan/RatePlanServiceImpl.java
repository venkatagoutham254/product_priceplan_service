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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatePlanServiceImpl implements RatePlanService {

    private final RatePlanRepository ratePlanRepository;
    private final ProductRepository productRepository;
    private final RatePlanMapper ratePlanMapper;
    private final RatePlanAssembler ratePlanAssembler;
    private final BillableMetricClient billableMetricClient;

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
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanDTO> getRatePlansByProductId(Long productId) {
        Long orgId = TenantContext.require();
        return ratePlanRepository.findByProduct_ProductIdAndOrganizationId(productId, orgId)
                .stream()
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RatePlanDTO getRatePlanById(Long ratePlanId) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        return ratePlanMapper.toDTO(ratePlan);
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
    public RatePlanDTO confirmRatePlan(Long ratePlanId) {
        Long orgId = TenantContext.require();
        RatePlan ratePlan = ratePlanRepository.findByRatePlanIdAndOrganizationId(ratePlanId, orgId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));

        if (ratePlan.getStatus() == RatePlanStatus.ACTIVE) {
            throw new IllegalStateException("RatePlan is already ACTIVE");
        }

        // ✅ enforce billableMetricId must exist before activating
        if (ratePlan.getBillableMetricId() == null) {
            throw new ValidationException("Billable Metric ID is required before finalizing a RatePlan.");
        }
        
        // ✅ validate with external service: must be ACTIVE and (if product known) belong to product
        Long productId = (ratePlan.getProduct() != null) ? ratePlan.getProduct().getProductId() : null;
        billableMetricClient.validateActiveForProduct(ratePlan.getBillableMetricId(), productId);
    
        ratePlan.setStatus(RatePlanStatus.ACTIVE);
        ratePlan = ratePlanRepository.save(ratePlan);
    
        return ratePlanMapper.toDTO(ratePlan);
    }
    
}
