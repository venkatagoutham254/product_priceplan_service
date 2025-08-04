package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.client.BillableMetricClient;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.product.entity.Product;
import aforo.productrateplanservice.product.enums.RatePlanStatus;
import aforo.productrateplanservice.product.repository.ProductRepository;
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
        String productName = request.getProductName().trim();

        Product product = productRepository.findByProductNameIgnoreCase(productName)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productName));

                if (!billableMetricClient.metricExists(request.getBillableMetricId())) {
                    throw new ValidationException("Invalid billableMetricId: " + request.getBillableMetricId());
                }
                
        RatePlanDTO dto = RatePlanDTO.builder()
                .ratePlanName(request.getRatePlanName())
                .description(request.getDescription())
                .billingFrequency(request.getBillingFrequency())
                .paymentType(request.getPaymentType())
                .billableMetricId(request.getBillableMetricId())
                .build();

        RatePlan ratePlan = ratePlanAssembler.toEntity(dto, product);
        ratePlan = ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    public List<RatePlanDTO> getAllRatePlans() {
        return ratePlanRepository.findAll()
                .stream()
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RatePlanDTO> getRatePlansByProductId(Long productId) {
        return ratePlanRepository.findByProduct_ProductId(productId)
                .stream()
                .map(ratePlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RatePlanDTO getRatePlanById(Long ratePlanId) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    public void deleteRatePlan(Long ratePlanId) {
        if (!ratePlanRepository.existsById(ratePlanId)) {
            throw new NotFoundException("Rate plan not found with ID: " + ratePlanId);
        }
        ratePlanRepository.deleteById(ratePlanId);
    }

    @Override
    public RatePlanDTO updateRatePlanFully(Long ratePlanId, UpdateRatePlanRequest request) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

        if (request.getRatePlanName() == null || request.getBillingFrequency() == null) {
            throw new ValidationException("All fields must be provided for full update.");
        }

        if (request.getBillableMetricId() != null) {
            billableMetricClient.metricExists(request.getBillableMetricId());
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
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("Rate plan not found with ID: " + ratePlanId));

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
            billableMetricClient.metricExists(request.getBillableMetricId());
            ratePlan.setBillableMetricId(request.getBillableMetricId());
        }

        ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }

    @Override
    public void confirmRatePlan(Long ratePlanId) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));

        if (ratePlan.getStatus() == RatePlanStatus.ACTIVE) {
            throw new IllegalStateException("RatePlan is already ACTIVE");
        }

        ratePlan.setStatus(RatePlanStatus.ACTIVE);
        ratePlanRepository.save(ratePlan);
    }
}
