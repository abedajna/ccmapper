package com.abedajna.cccmapper.testscenario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.BaseRepository;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

@Repository
public class VersionRepository extends BaseRepository<ProductCFCompositeColumn, Version, Long> {

	public Version getProductVersion(Long customerId, UUID productId, Integer versionNum) throws TooManyResultsException {
		String productPrefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX;
		String versionPrefix = Constants.VERSION_COMPOSITE_KEY_PREFIX;

		CompositeRangeBuilder range = getAnnotatedCompositeSerializer().buildRange().withPrefix(productPrefix).withPrefix(productId).withPrefix(versionPrefix).greaterThanEquals(versionNum).lessThanEquals(versionNum);
		return queryForOne(customerId, range, Version.class);
	}

	public List<Version> getAllProductVersions(Long customerId, UUID productId) {
		String productPrefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX;
		String versionPrefix = Constants.VERSION_COMPOSITE_KEY_PREFIX;

		CompositeRangeBuilder range = getAnnotatedCompositeSerializer().buildRange().withPrefix(productPrefix).withPrefix(productId).withPrefix(versionPrefix).greaterThanEquals(0).lessThanEquals(10000);
		return query(customerId, range, Version.class);
	}

}
