package aforo.productrateplanservice.freemium;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FreemiumServiceImpl implements FreemiumService {

    private final FreemiumRepository freemiumRepository;
    private final RatePlanRepository ratePlanRepository;
    private final FreemiumMapper freemiumMapper;
    private final CacheInvalidationService cacheInvalidationService;

    @Override
public FreemiumDTO create(Long ratePlanId, FreemiumCreateUpdateDTO dto) {
    RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
            .orElseThrow(() -> new EntityNotFoundException("RatePlan not found"));

    // ✅ Prevent duplicate Freemium creation for a RatePlan
    List<Freemium> existing = freemiumRepository.findByRatePlan_RatePlanId(ratePlanId);
    if (!existing.isEmpty()) {
        throw new IllegalStateException("Freemium already exists for this RatePlan. Please update the existing entry.");
    }

    Freemium freemium = new Freemium();
    freemium.setRatePlan(ratePlan);
    freemiumMapper.update(freemium, dto);
    Freemium saved = freemiumRepository.save(freemium);
    
    // Invalidate rate plan caches
    cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
    
    return freemiumMapper.toDTO(saved);
}


    @Override
    public FreemiumDTO update(Long ratePlanId, Long id, FreemiumCreateUpdateDTO dto) {
        Freemium freemium = getFreemiumById(id, ratePlanId);
        freemiumMapper.update(freemium, dto);
        Freemium saved = freemiumRepository.save(freemium);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        return freemiumMapper.toDTO(saved);
    }

    @Override
    public FreemiumDTO partialUpdate(Long ratePlanId, Long id, FreemiumCreateUpdateDTO dto) {
        Freemium freemium = getFreemiumById(id, ratePlanId);
        freemiumMapper.update(freemium, dto); // You can add null checks if needed
        Freemium saved = freemiumRepository.save(freemium);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        return freemiumMapper.toDTO(saved);
    }

    @Override
    public void delete(Long ratePlanId, Long id) {
        Freemium freemium = getFreemiumById(id, ratePlanId);
        freemiumRepository.delete(freemium);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
    }

    @Override
    public FreemiumDTO getById(Long ratePlanId, Long id) {
        Freemium freemium = getFreemiumById(id, ratePlanId);
        return freemiumMapper.toDTO(freemium);
    }

    @Override
    public List<FreemiumDTO> getAllByRatePlanId(Long ratePlanId) {
        return freemiumRepository.findByRatePlan_RatePlanId(ratePlanId)
                .stream()
                .map(freemiumMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FreemiumDTO> getAll() {
        return freemiumRepository.findAll()
                .stream()
                .map(freemiumMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Freemium getFreemiumById(Long id, Long ratePlanId) {
        Freemium entity = freemiumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Freemium not found"));
        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }
        return entity;
    }
}
