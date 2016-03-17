package com.abedajna.cccmapper.testscenario.repository;

import org.springframework.stereotype.Repository;

import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.BaseRepository;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

@Repository
public class LastUpdatedProductRepoisitory extends BaseRepository<ProductCFCompositeColumn, LastUpdatedProduct, Long> {

	public LastUpdatedProduct getLastUpdatedProduct(Long customerId) throws TooManyResultsException {
		String prefix = Constants.LAST_UPDATED_PRODUCT_COMPOSITE_KEY_PREFIX;
		CompositeRangeBuilder range = getAnnotatedCompositeSerializer().buildRange().withPrefix(prefix).greaterThanEquals(new LastUpdatedProduct().getId()).lessThanEquals(new LastUpdatedProduct().getId());
		return queryForOne(customerId, range, LastUpdatedProduct.class);
	}

}
