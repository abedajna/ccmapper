package com.abedajna.cccmapper.testscenario.domain;

import java.util.*;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CassandraDataObject;

@TTL(0)
public class Account extends BaseEntity implements CassandraDataObject {

	@CompositeKeyWithOrdinal(prefix = Constants.ACCOUNT_COMPOSITE_KEY_PREFIX, ordinal = 1)
	private UUID accountId;
	
	@StoreAsJson
	private AccountMetadata acmetadata;
	
	private Status status;
	
	@OneToMany()
	private List<AccountAllocationDenorm> accountAllocations = new ArrayList<AccountAllocationDenorm>();


	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public AccountMetadata getAcmetadata() {
		return acmetadata;
	}

	public void setAcmetadata(AccountMetadata acmetadata) {
		this.acmetadata = acmetadata;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<AccountAllocationDenorm> getAccountAllocations() {
		return accountAllocations;
	}

	public void setAccountAllocations(List<AccountAllocationDenorm> accountAllocations) {
		this.accountAllocations = accountAllocations;
	}
		

}
