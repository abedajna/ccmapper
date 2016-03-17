package com.abedajna.cccmapper.testscenario.domain;

import java.util.UUID;

import com.abedajna.cccmapper.annotations.CompositeKeyWithOrdinal;
import com.abedajna.cccmapper.annotations.TTL;
import com.abedajna.cccmapper.domain.CassandraDataObject;

@TTL(63244800)
public class AccountAllocation extends BaseEntity implements CassandraDataObject {

	@CompositeKeyWithOrdinal(prefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX, ordinal = 1)
	private UUID productId;

	@CompositeKeyWithOrdinal(prefix = Constants.VERSION_COMPOSITE_KEY_PREFIX, ordinal = 3)
	private Integer versionNum;

	@CompositeKeyWithOrdinal(prefix = Constants.ACCOUNT_COMPOSITE_KEY_PREFIX, ordinal = 5)
	private UUID accountId;


	private String allocReason;
	
	private Integer allocAmount;


	
	
	
	
	// getters & setters

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(Integer versionNum) {
		this.versionNum = versionNum;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public String getAllocReason() {
		return allocReason;
	}

	public void setAllocReason(String allocReason) {
		this.allocReason = allocReason;
	}

	public Integer getAllocAmount() {
		return allocAmount;
	}

	public void setAllocAmount(Integer allocAmount) {
		this.allocAmount = allocAmount;
	}


}
