package aforo.productrateplanservice.minimumcommitment;

import aforo.productrateplanservice.cache.CacheInvalidationService;
import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MinimumCommitmentServiceImpl implements MinimumCommitmentService {

    private final MinimumCommitmentRepository repository;
    private final MinimumCommitmentMapper mapper;
    private final RatePlanRepository ratePlanRepository;
    private final CacheInvalidationService cacheInvalidationService;

    @Override
    public MinimumCommitmentDTO create(Long ratePlanId, MinimumCommitmentCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));
    
        // ❗ Validation: Only one MinimumCommitment per RatePlan
        List<MinimumCommitment> existing = repository.findByRatePlan_RatePlanId(ratePlanId);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Minimum Commitment already exists for this RatePlan. Please update the existing one.");
        }
    
        MinimumCommitment entity = mapper.toEntity(dto, ratePlan);
        MinimumCommitment saved = repository.save(entity);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        return mapper.toDTO(saved);
    }
    
    @Override
    public List<MinimumCommitmentDTO> getAllByRatePlanId(Long ratePlanId) {
        return repository.findByRatePlan_RatePlanId(ratePlanId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MinimumCommitmentDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MinimumCommitmentDTO getById(Long ratePlanId, Long id) {
        MinimumCommitment entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Minimum Commitment not found"));
        if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }
        return mapper.toDTO(entity);
    }

    @Override
    public MinimumCommitmentDTO update(Long ratePlanId, Long id, MinimumCommitmentCreateUpdateDTO dto) {
        MinimumCommitment existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Minimum Commitment not found"));
        mapper.updateEntity(existing, dto);
        MinimumCommitment saved = repository.save(existing);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        return mapper.toDTO(saved);
    }

    @Override
    public MinimumCommitmentDTO partialUpdate(Long ratePlanId, Long id, MinimumCommitmentCreateUpdateDTO dto) {
        MinimumCommitment existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Minimum Commitment not found"));
        mapper.partialUpdate(existing, dto);
        MinimumCommitment saved = repository.save(existing);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
        
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long ratePlanId, Long id) {
        MinimumCommitment entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Minimum Commitment not found"));
        repository.delete(entity);
        
        // Invalidate rate plan caches
        cacheInvalidationService.invalidateRatePlanCaches(ratePlanId);
    }
}
