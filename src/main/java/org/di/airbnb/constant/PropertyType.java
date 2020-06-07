package org.di.airbnb.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PropertyType {
	ROOM( 1 ),

	HOUSE( 2 );

	private static final Map<Integer, PropertyType> byId = Arrays.stream( PropertyType.values() )
			.collect( Collectors.toMap( PropertyType::getId, Function.identity() ) );
	private final int id;

	PropertyType( final int id ) {
		this.id = id;
	}

	public static PropertyType fromId( final int id ) {
		return byId.get( id );
	}

	public int getId() {
		return id;
	}
}
