package com.abedajna.cccmapper.testutils;

import java.util.ArrayList;
import java.util.List;

import com.abedajna.cccmapper.testscenario.domain.ProductCFCompositeColumn;
import com.abedajna.cccmapper.util.Pair;
import com.abedajna.cccmapper.util.Triple;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.*;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.LongSerializer;

public class TestUtils {

	public static void printAllCassandraData(Long customerId, Keyspace keyspace) throws ConnectionException {

		System.out.println("\n\n\n Printing All Data");

		AnnotatedCompositeSerializer<ProductCFCompositeColumn> serializer = new AnnotatedCompositeSerializer<ProductCFCompositeColumn>(ProductCFCompositeColumn.class);

		ColumnFamily<Long, ProductCFCompositeColumn> productCF = ColumnFamily.newColumnFamily("ProductCF", LongSerializer.get(), new AnnotatedCompositeSerializer<ProductCFCompositeColumn>(
				ProductCFCompositeColumn.class));

		ColumnList<ProductCFCompositeColumn> columns = keyspace.prepareQuery(productCF).getKey(customerId).withColumnRange(serializer.buildRange().greaterThanEquals("A").lessThanEquals("Z"))
				.execute().getResult();

		for (Column<ProductCFCompositeColumn> c : columns) {
			System.out.println(c.getName().getKey1() + "," + c.getName().getKey1Val() + "," + c.getName().getKey2() + "," + c.getName().getKey2Val() + "," + c.getName().getKey3() + ","
					+ c.getName().getKey3Val() + "," + c.getName().getAttributename() + ":" + c.getStringValue());
		}

		System.out.println("End Printing All Data \n\n\n");
	}
	
	public static List<Pair<ProductCFCompositeColumn, String>> shedTTLs(List<Triple<ProductCFCompositeColumn, String, Integer>> allT) {
		List<Pair<ProductCFCompositeColumn, String>> allP = new ArrayList<Pair<ProductCFCompositeColumn, String>>();
		allT.stream().forEach(t -> {
			allP.add(new Pair<ProductCFCompositeColumn, String>(t.u, t.v));
		});
		return allP;

	}

}
