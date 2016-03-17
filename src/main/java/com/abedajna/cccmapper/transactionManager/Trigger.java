package com.abedajna.cccmapper.transactionManager;

import java.util.List;

import com.abedajna.cccmapper.domain.CassandraDataObject;

public interface Trigger<CompositeColumnName> {

	List<CassandraDataObject> execute(CassandraDataObject source);

}
