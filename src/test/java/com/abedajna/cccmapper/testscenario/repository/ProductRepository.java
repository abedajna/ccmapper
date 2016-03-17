package com.abedajna.cccmapper.testscenario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.BaseRepository;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

@Repository
public class ProductRepository extends BaseRepository<ProductCFCompositeColumn, Product, Long> {

	@Autowired
	LastUpdatedProductRepoisitory lastUpdatedProductRepoisitory;

	public Product getProduct(Long customerId, UUID productId) throws TooManyResultsException {
		String productPrefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX;
		CompositeRangeBuilder range = getAnnotatedCompositeSerializer().buildRange().withPrefix(productPrefix).greaterThanEquals(productId).lessThanEquals(productId);
		return queryForOne(customerId, range, Product.class);
	}

	public Product getLastUpdatedProduct(Long customerId) throws TooManyResultsException {
		return getProduct(customerId, lastUpdatedProductRepoisitory.getLastUpdatedProduct(customerId).getProductId());
	}
	
	public List<Product> getAllProducts(Long customerId) {
		String productPrefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX;
		CompositeRangeBuilder range = getAnnotatedCompositeSerializer().buildRange().greaterThanEquals(productPrefix).lessThanEquals(productPrefix);
		return query(customerId, range, Product.class);
	}

}
