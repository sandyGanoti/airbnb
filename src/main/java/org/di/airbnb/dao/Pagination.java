package org.di.airbnb.dao;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class Pagination {

	@PositiveOrZero
	private int limit;
	@Positive
	private int offset;

	public Pagination( final int limit, final int offset ) {
		this.limit = limit;
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit( final int limit ) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset( final int offset ) {
		this.offset = offset;
	}
}
