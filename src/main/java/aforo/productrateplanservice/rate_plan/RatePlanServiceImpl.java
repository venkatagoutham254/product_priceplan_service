package aforo.productrateplanservice.rate_plan;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.exception.ValidationException;
import aforo.productrateplanservice.product.entity.Product;
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

    @Override
public RatePlanDTO createRatePlan(CreateRatePlanRequest request) {
    String productName = request.getProductName().trim();

    Product product = productRepository.findByProductNameIgnoreCase(productName)
            .orElseThrow(() -> new NotFoundException("Product not found: " + productName));

    RatePlan ratePlan = RatePlan.builder()
            .ratePlanName(request.getRatePlanName())
            .description(request.getDescription())
            .ratePlanType(request.getRatePlanType())
            .billingFrequency(request.getBillingFrequency())
            .product(product)
            .build();

    ratePlanRepository.save(ratePlan);
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
    
        ratePlan.setRatePlanName(request.getRatePlanName());
        ratePlan.setDescription(request.getDescription());
        ratePlan.setRatePlanType(request.getRatePlanType());
        ratePlan.setBillingFrequency(request.getBillingFrequency());
    
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
    
        if (request.getRatePlanType() != null) {
            ratePlan.setRatePlanType(request.getRatePlanType());
        }
    
        if (request.getBillingFrequency() != null) {
            ratePlan.setBillingFrequency(request.getBillingFrequency());
        }
    
        ratePlanRepository.save(ratePlan);
        return ratePlanMapper.toDTO(ratePlan);
    }
}    