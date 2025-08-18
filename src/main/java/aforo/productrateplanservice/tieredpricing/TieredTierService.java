package aforo.productrateplanservice.tieredpricing;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class TieredTierService {
    private final TieredTierRepository tieredTierRepository;

    @Autowired
    public TieredTierService(TieredTierRepository tieredTierRepository) {
        this.tieredTierRepository = tieredTierRepository;
    }

    public List<TieredTier> findByTieredPricingId(Long tieredPricingId) {
        return tieredTierRepository.findByTieredPricingTieredPricingId(tieredPricingId);
    }

    public TieredTier save(TieredTier tier) {
        return tieredTierRepository.save(tier);
    }

    public void delete(Long id) {
        tieredTierRepository.deleteById(id);
    }
}
