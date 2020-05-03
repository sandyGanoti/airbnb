package org.di.airbnb.assemblers.user;

import org.di.airbnb.api.AirbnbController;
import org.di.airbnb.dao.entities.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {

	public UserModelAssembler() {
		super( AirbnbController.class, UserModel.class );
	}

	@Override
	public UserModel toModel( User userEntity ) {
		return instantiateModel( userEntity );
	}

	@Override
	public CollectionModel<UserModel> toCollectionModel( Iterable<? extends User> userEntities ) {
		return super.toCollectionModel( userEntities );
	}

}
