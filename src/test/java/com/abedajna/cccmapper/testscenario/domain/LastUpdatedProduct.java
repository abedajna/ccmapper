package com.abedajna.cccmapper.testscenario.domain;

import java.util.UUID;

import com.abedajna.cccmapper.annotations.CompositeKeyWithOrdinal;
import com.abedajna.cccmapper.domain.CassandraDataObject;

public class LastUpdatedProduct implements CassandraDataObject {

	@CompositeKeyWithOrdinal(prefix = Constants.LAST_UPDATED_PRODUCT_COMPOSITE_KEY_PREFIX, ordinal = 1)
	private UUID id = UUID.fromString("8c95b03e-2665-4fd0-83fe-3524967bcd50");

	private UUID productId;

	// getters & setters

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

}
