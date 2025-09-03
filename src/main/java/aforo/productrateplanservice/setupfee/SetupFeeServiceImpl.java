package aforo.productrateplanservice.setupfee;

import aforo.productrateplanservice.exception.NotFoundException;
import aforo.productrateplanservice.rate_plan.RatePlan;
import aforo.productrateplanservice.rate_plan.RatePlanRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetupFeeServiceImpl implements SetupFeeService {

    private final SetupFeeRepository setupFeeRepository;
    private final RatePlanRepository ratePlanRepository;
    private final SetupFeeMapper setupFeeMapper;

    @Override
    public SetupFeeDTO create(Long ratePlanId, SetupFeeCreateUpdateDTO dto) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new NotFoundException("RatePlan not found"));
    
        // ✅ Check if a SetupFee already exists for this RatePlan
        List<SetupFee> existing = setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId);
        if (!existing.isEmpty()) {
            throw new IllegalStateException("SetupFee already exists for this RatePlan. Please update the existing one.");
        }
    
        SetupFee entity = setupFeeMapper.toEntity(dto, ratePlan);
        SetupFee saved = setupFeeRepository.save(entity);
        return setupFeeMapper.toDTO(saved);
    }
    

    @Override
    public SetupFeeDTO update(Long ratePlanId, Long id, SetupFeeCreateUpdateDTO dto) {
        SetupFee existing = setupFeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SetupFee not found"));

        if (!existing.getRatePlan().getRatePlanId().equals(ratePlanId)) {
            throw new IllegalArgumentException("RatePlan ID mismatch");
        }

        setupFeeMapper.updateEntity(existing, dto);
        setupFeeRepository.save(existing);
        return setupFeeMapper.toDTO(existing);
    }

    @Override
public void delete(Long ratePlanId, Long id) {
    SetupFee entity = setupFeeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("SetupFee not found"));

    if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RatePlan ID mismatch for SetupFee");
    }

    setupFeeRepository.delete(entity);
}

   @Override
public SetupFeeDTO getById(Long ratePlanId, Long id) {
    SetupFee entity = setupFeeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("SetupFee not found"));

    if (!entity.getRatePlan().getRatePlanId().equals(ratePlanId)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RatePlan ID mismatch for SetupFee");
    }

    return setupFeeMapper.toDTO(entity);
}

    @Override
    public List<SetupFeeDTO> getAllByRatePlanId(Long ratePlanId) {
        List<SetupFee> list = setupFeeRepository.findByRatePlan_RatePlanId(ratePlanId);
        return list.stream()
                .map(setupFeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SetupFeeDTO> getAll() {
        return setupFeeRepository.findAll()
                .stream()
                .map(setupFeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SetupFeeDTO partialUpdate(Long ratePlanId, Long id, SetupFeeCreateUpdateDTO dto) {
        SetupFee existing = setupFeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SetupFee not found"));

        if (!existing.getRatePlan().getRatePlanId().equals(ratePlanId)) {
                        throw new IllegalArgumentException("RatePlan ID mismatch");
                    }
                
        setupFeeMapper.partialUpdate(existing, dto);
        setupFeeRepository.save(existing);
        return setupFeeMapper.toDTO(existing);
    }
}
