package com.abedajna.cccmapper.testscenario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.BaseRepository;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

@Repository
public class AccountRepository extends BaseRepository<ProductCFCompositeColumn, Account, Long> {

	public Account getAccount(Long customerId, UUID accountId) throws TooManyResultsException {
		String accountPrefix = Constants.ACCOUNT_COMPOSITE_KEY_PREFIX;
		CompositeRangeBuilder range = this.getAnnotatedCompositeSerializer().buildRange().withPrefix(accountPrefix).greaterThanEquals(accountId).lessThanEquals(accountId);
		return queryForOne(customerId, range, Account.class);
	}

	// TODO
	public List<Account> getAllAccounts(Long customerId) {
		String accountPrefix = Constants.ACCOUNT_COMPOSITE_KEY_PREFIX;
		CompositeRangeBuilder range = this.getAnnotatedCompositeSerializer().buildRange().greaterThanEquals(accountPrefix).lessThanEquals(accountPrefix);
		return query(customerId, range, Account.class);
	}
	
}
