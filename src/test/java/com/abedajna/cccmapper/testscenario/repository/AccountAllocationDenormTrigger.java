package com.abedajna.cccmapper.testscenario.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.Trigger;

@Component
public class AccountAllocationDenormTrigger implements Trigger<ProductCFCompositeColumn> {

	@Override
	public List<CassandraDataObject> execute(CassandraDataObject source) {
		
		List<CassandraDataObject> lst = new ArrayList<CassandraDataObject>();
		if (source instanceof Product) {
			Product p = (Product) source;
			p.getVersions().stream().forEach(v -> {
				v.getAccountAllocations().stream().forEach(a -> {
					
					AccountAllocationDenorm den = new AccountAllocationDenorm();
					den.setAccountId(a.getAccountId());
					den.setProductId(a.getProductId());
					den.setVersionNum(a.getVersionNum());
					den.setAllocAmount(a.getAllocAmount());
					den.setAllocReason(a.getAllocReason());
					lst.add(den);
					
				});
			});
		}
		return lst;		
	}
	
}
