package org.di.airbnb.dao;

import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;

import com.google.common.base.Throwables;

public class UniqueConstraintHelper {

	public static boolean causedByUniqueConstraint( RuntimeException e, String constraintName ) {
		final Optional<String> constraint = causedBy( e, ConstraintViolationException.class ).map(
				ConstraintViolationException::getConstraintName );

		if ( constraint.isPresent() ) {
			return constraint.filter(
					c -> c.toLowerCase().contains( constraintName.toLowerCase() ) ).isPresent();
		} else {
			return Optional.ofNullable( Throwables.getRootCause( e ).getMessage() )
					.filter( c -> c.toLowerCase().contains( constraintName.toLowerCase() ) )
					.isPresent();
		}

	}

	private static <T> Optional<T> causedBy( Exception e, Class<T> cause ) {
		return Throwables.getCausalChain( e )
				.stream()
				.filter( cause::isInstance )
				.findFirst()
				.map( cause::cast );
	}
}
