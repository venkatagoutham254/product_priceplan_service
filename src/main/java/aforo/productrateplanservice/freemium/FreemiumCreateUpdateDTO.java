package aforo.productrateplanservice.freemium;

import aforo.productrateplanservice.freemium.FreemiumType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FreemiumCreateUpdateDTO {

    @NotNull
    private FreemiumType freemiumType;

    private Integer freeUnits;

    private Integer freeTrialDuration;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
