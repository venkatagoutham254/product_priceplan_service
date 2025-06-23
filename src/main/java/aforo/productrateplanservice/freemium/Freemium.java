package aforo.productrateplanservice.freemium;

import aforo.productrateplanservice.freemium.FreemiumType;
import aforo.productrateplanservice.rate_plan.RatePlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Freemium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private RatePlan ratePlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FreemiumType freemiumType;

    private Integer freeUnits;

    private Integer freeTrialDuration;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
