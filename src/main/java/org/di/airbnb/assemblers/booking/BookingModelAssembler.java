package org.di.airbnb.assemblers.booking;

import org.di.airbnb.api.AirbnbController;
import org.di.airbnb.dao.entities.Booking;
import org.di.airbnb.dao.entities.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class BookingModelAssembler extends RepresentationModelAssemblerSupport<Booking, BookingModel> {

	public BookingModelAssembler() {
		super( AirbnbController.class, BookingModel.class );
	}

	@Override
	public BookingModel toModel( Booking bookingEntity ) {
		return instantiateModel( bookingEntity );
	}

	@Override
	public CollectionModel<BookingModel> toCollectionModel( Iterable<? extends Booking> bookingEntities ) {
		return super.toCollectionModel( bookingEntities );
	}

}
