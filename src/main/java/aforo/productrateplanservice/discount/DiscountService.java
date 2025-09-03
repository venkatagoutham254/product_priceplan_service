package aforo.productrateplanservice.discount;

import java.util.List;

public interface DiscountService {
    DiscountDTO create(Long ratePlanId, DiscountCreateUpdateDTO dto);
    List<DiscountDTO> getAllByRatePlanId(Long ratePlanId);
    List<DiscountDTO> getAll();
    DiscountDTO getById(Long ratePlanId, Long id);
    DiscountDTO update(Long ratePlanId, Long id, DiscountCreateUpdateDTO dto);
    DiscountDTO partialUpdate(Long ratePlanId, Long id, DiscountCreateUpdateDTO dto);
    void delete(Long ratePlanId, Long id);
}
