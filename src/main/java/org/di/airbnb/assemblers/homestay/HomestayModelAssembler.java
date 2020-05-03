package org.di.airbnb.assemblers.homestay;

import org.di.airbnb.api.AirbnbController;
import org.di.airbnb.dao.entities.Homestay;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class HomestayModelAssembler extends
		RepresentationModelAssemblerSupport<Homestay, HomestayModel> {

	public HomestayModelAssembler() {
		super( AirbnbController.class, HomestayModel.class );
	}

	@Override
	public HomestayModel toModel( Homestay homestayEntity ) {
		return instantiateModel( homestayEntity );
	}

	@Override
	public CollectionModel<HomestayModel> toCollectionModel(
			Iterable<? extends Homestay> homestayEntities ) {
		return super.toCollectionModel( homestayEntities );
	}

}
