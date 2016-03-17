package com.abedajna.cccmapper.transactionManager;

import java.util.ArrayList;
import java.util.List;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Triple;
import com.netflix.astyanax.MutationBatch;

/*
 * K: rowkey
 * C: columnname
 */
public class Transaction<C extends CompositeColumnName, K> {

	private final List<CassandraDataObject> cds;
	private final BaseRepository<C, ?, K> repo;
	private final K k;
	private final Class<C> classC;

	public Transaction(Builder<C, K> b) {
		this.repo = b.repo;
		this.k = b.k;
		this.classC = b.classC;
		this.cds = b.cds;
	}

	public void commit() {
		try {
			List<Triple<C, String, Integer>> lst = new ArrayList<Triple<C, String, Integer>>();
			for (CassandraDataObject o : cds) {
				lst.addAll(repo.convertCDOToCCNO.convert(o, classC));
			}
			MutationBatch batch = repo.keyspace.prepareMutationBatch();
			for (Triple<C, String, Integer> p : lst) {
				batch.withRow(repo.getColumnFamily(), k).putColumn(p.u, p.v, p.w);
			}
			batch.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class Builder<C extends CompositeColumnName, K> {

		private final List<CassandraDataObject> cds = new ArrayList<CassandraDataObject>();
		private final K k;
		private final Class<C> classC;
		private final BaseRepository<C, ?, K> repo;
		private final List<Trigger<C>> triggers = new ArrayList<Trigger<C>>();

		public Builder(BaseRepository<C, ?, K> repo, K k, Class<C> classC, List<Trigger<C>> triggers) {
			this.repo = repo;
			this.k = k;
			this.classC = classC;
			this.triggers.addAll(triggers);
		}

		public Builder<C, K> with(CassandraDataObject o) {
			cds.add(o);
			return this;
		}

		public Transaction<C, K> build() {
			List<CassandraDataObject> triggerOutput = new ArrayList<CassandraDataObject>();
			for (CassandraDataObject o : cds) {
				for (Trigger<C> trigger : triggers) {
					triggerOutput.addAll(trigger.execute(o));
				}
			}
			cds.addAll(triggerOutput);
			return new Transaction<C, K>(this);
		}

	}

}
