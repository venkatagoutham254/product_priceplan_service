package aforo.productrateplanservice.estimator;

/**
 * Service for calculating revenue estimates based on pricing configuration.
 */
public interface RevenueEstimatorService {
    EstimateResponse estimate(EstimateRequest request);
}
