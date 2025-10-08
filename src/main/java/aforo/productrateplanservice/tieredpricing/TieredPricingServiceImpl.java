package aforo.productrateplanservice.tieredpricing;

import aforo.productrateplanservice.exception.ResourceNotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TieredPricingServiceImpl implements TieredPricingService {

    private final TieredPricingRepository tieredPricingRepository;
    private final TieredPricingMapper tieredPricingMapper;
    private final RatePlanRepository ratePlanRepository;

    @Override
    @Transactional
    public TieredPricingDTO create(Long ratePlanId, TieredPricingCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        TieredPricing entity = tieredPricingMapper.toEntity(dto, ratePlan);
        // Set parent reference for all tiers
        if (entity.getTiers() != null) {
            for (TieredTier tier : entity.getTiers()) {
                tier.setTieredPricing(entity);
            }
        }
        return tieredPricingMapper.toDTO(tieredPricingRepository.save(entity));
    }

    @Override
    @Transactional
    public TieredPricingDTO update(Long ratePlanId, Long tieredPricingId, TieredPricingCreateUpdateDTO dto) {
        TieredPricing existing = tieredPricingRepository.findById(tieredPricingId)
                .orElseThrow(() -> new ResourceNotFoundException("TieredPricing not found with ID: " + tieredPricingId));

        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new ResourceNotFoundException("RatePlan not found with ID: " + ratePlanId));

        existing.setRatePlan(ratePlan);
        tieredPricingMapper.updateEntity(existing, dto);
        // Set parent reference for all tiers
        if (existing.getTiers() != null) {
            for (TieredTier tier : existing.getTiers()) {
                tier.setTieredPricing(existing);
            }
        }
        return tieredPricingMapper.toDTO(tieredPricingRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TieredPricingDTO> getAllByRatePlanId(Long ratePlanId) {
        return tieredPricingRepository.findByRatePlan_RatePlanId(ratePlanId)
                .stream()
                .map(tieredPricingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TieredPricingDTO> getAll() {
        return tieredPricingRepository.findAll()
                .stream()
                .map(tieredPricingMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TieredPricingDTO getById(Long tieredPricingId) {
        return tieredPricingRepository.findByTieredPricingId(tieredPricingId)
                .map(tieredPricingMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("TieredPricing not found with ID: " + tieredPricingId));
    }

    @Override
    @Transactional
    public void deleteById(Long tieredPricingId) {
        if (!tieredPricingRepository.existsById(tieredPricingId)) {
            throw new ResourceNotFoundException("TieredPricing not found with ID: " + tieredPricingId);
        }
        tieredPricingRepository.deleteById(tieredPricingId);
    }
}
