package com.abedajna.cccmapper.cache;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;

public class DomainScanner {

	@Value("${cassandra.data.object.pkg}")
	String cdoPkg;

	@Value("${cassandra.composite.column.name.object.pkg}")
	String ccoPkg;

	public Set<Class<CassandraDataObject>> cassandraDataObjects() {
		return findObjects(CassandraDataObject.class, cdoPkg);
	}

	public Set<Class<CompositeColumnName>> compositeColumnNameObjects() {
		return findObjects(CompositeColumnName.class, ccoPkg);
	}

	private <A extends B, B> Set<Class<B>> findObjects(Class<A> clazz, String pkg) {
		Set<Class<B>> classesFound = new HashSet<Class<B>>();

		ClassPathScanningCandidateComponentProvider p = new ClassPathScanningCandidateComponentProvider(false);
		p.addIncludeFilter(new AssignableTypeFilter(clazz));
		p.findCandidateComponents(pkg).stream().forEach((bean) -> {
			try {
				classesFound.add((Class<B>) Class.forName(bean.getBeanClassName()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return classesFound;
	}

}
