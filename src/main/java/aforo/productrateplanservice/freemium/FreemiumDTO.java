package aforo.productrateplanservice.freemium;

import lombok.Getter;
import lombok.Setter;
import aforo.productrateplanservice.freemium.FreemiumType;

import java.time.LocalDate;

@Getter
@Setter
public class FreemiumDTO {

    private Long id;
    private FreemiumType freemiumType;
    private Integer freeUnits;
    private Integer freeTrialDuration;
    private LocalDate startDate;
    private LocalDate endDate;
}
