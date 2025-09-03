package aforo.productrateplanservice.discount;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
public DiscountDTO create(Long ratePlanId, DiscountCreateUpdateDTO dto) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
            .orElseThrow(() -> new NotFoundException("RatePlan not found"));

    // ❗ Validation: Only one Discount per RatePlan
    List<Discount> existing = discountRepository.findByRatePlan_RatePlanId(ratePlanId);
    if (!existing.isEmpty()) {
        throw new IllegalStateException("Discount already exists for this RatePlan. Please update the existing one.");
    }

    Discount discount = discountMapper.toEntity(dto, ratePlan);
    return discountMapper.toDTO(discountRepository.save(discount));
}


    @Override
    public List<DiscountDTO> getAllByRatePlanId(Long ratePlanId) {
        return discountRepository.findByRatePlan_RatePlanId(ratePlanId).stream()
                .map(discountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountDTO> getAll() {
        return discountRepository.findAll()
                .stream()
                .map(discountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DiscountDTO getById(Long ratePlanId, Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        if (!discount.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }
        return discountMapper.toDTO(discount);
    }

    @Override
    public DiscountDTO update(Long ratePlanId, Long id, DiscountCreateUpdateDTO dto) {
        Discount existing = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        discountMapper.updateEntity(existing, dto);
        return discountMapper.toDTO(discountRepository.save(existing));
    }

    @Override
    public DiscountDTO partialUpdate(Long ratePlanId, Long id, DiscountCreateUpdateDTO dto) {
        Discount existing = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        discountMapper.partialUpdate(existing, dto);
        return discountMapper.toDTO(discountRepository.save(existing));
    }

    @Override
    public void delete(Long ratePlanId, Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
        discountRepository.delete(discount);
    }
}
