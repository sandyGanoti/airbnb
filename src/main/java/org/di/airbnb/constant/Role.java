package org.di.airbnb.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Role {
	TENANT( 1 ),

	TENANT_AND_LANDLORD( 2 );

	private static final Map<Integer, Role> byId = Arrays.stream( Role.values() )
			.collect( Collectors.toMap( Role::getId, Function.identity() ) );
	private final int id;

	Role( final int id ) {
		this.id = id;
	}

	public static Role fromId( final int id ) {
		return byId.get( id );
	}

	public int getId() {
		return id;
	}
}