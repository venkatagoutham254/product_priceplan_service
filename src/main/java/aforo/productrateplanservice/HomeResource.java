package aforo.productrateplanservice;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import aforo.productrateplanservice.product.resource.ProductResource;
import aforo.productrateplanservice.rate_plan.RatePlanResource;
import aforo.productrateplanservice.discount.DiscountController;
import aforo.productrateplanservice.freemium.FreemiumController;
import aforo.productrateplanservice.minimumcommitment.MinimumCommitmentController;
import aforo.productrateplanservice.setupfee.SetupFeeController;

@RestController
public class HomeResource {

    @GetMapping("/")
public RepresentationModel<?> index() {
    return RepresentationModel.of(null)
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductResource.class).getAllProducts(false)).withRel("products"))
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RatePlanResource.class).getAllRatePlans()).withRel("ratePlans"))
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(SetupFeeController.class).getAllByRatePlan(1L)).withRel("setupFees"))
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).getAll(1L)).withRel("discounts"))
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FreemiumController.class).getAll(1L)).withRel("freemiums"))
        .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MinimumCommitmentController.class).getAll(1L)).withRel("minimumCommitments"));
}
}