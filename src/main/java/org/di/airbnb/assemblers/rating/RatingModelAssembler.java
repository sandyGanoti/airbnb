package org.di.airbnb.assemblers.rating;

import org.di.airbnb.api.AirbnbController;
import org.di.airbnb.dao.entities.Rating;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RatingModelAssembler extends RepresentationModelAssemblerSupport<Rating, RatingModel> {

	public RatingModelAssembler() {
		super( AirbnbController.class, RatingModel.class );
	}

	@Override
	public RatingModel toModel( Rating ratingEntity ) {
		return instantiateModel( ratingEntity );
	}

	@Override
	public CollectionModel<RatingModel> toCollectionModel(
			Iterable<? extends Rating> ratingEntities ) {
		return super.toCollectionModel( ratingEntities );
	}

}
