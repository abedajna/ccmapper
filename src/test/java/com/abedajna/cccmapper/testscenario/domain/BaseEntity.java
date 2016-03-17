package com.abedajna.cccmapper.testscenario.domain;

import java.time.ZonedDateTime;

public abstract class BaseEntity {

	private ZonedDateTime creationTs;
	
	private String createdBy;

	public ZonedDateTime getCreationTs() {
		return creationTs;
	}

	public void setCreationTs(ZonedDateTime creationTs) {
		this.creationTs = creationTs;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	

}
