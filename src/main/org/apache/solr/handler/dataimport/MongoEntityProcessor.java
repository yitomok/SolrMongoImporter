package org.apache.solr.handler.dataimport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

public class MongoEntityProcessor extends EntityProcessorBase {
	protected MongoDataSource dataSource;

	private String collection;

	@Override
	public void init(Context context) {
		super.init(context);
		collection = context.getEntityAttribute(COLLECTION);
		if (collection == null) {
			throw new DataImportHandlerException(DataImportHandlerException.SEVERE, "Collection must be supplied");
		}
		dataSource = (MongoDataSource) context.getDataSource();
	}

	protected void initQuery(String query) {
		this.query = query;
		DataImporter.QUERY_COUNT.get().incrementAndGet();
		rowIterator = new BsonDocumentRowIterator(dataSource.getData(query, collection));
	}

	@Override
	public Map<String, Object> nextRow() {
		if (rowIterator == null) {
			initQuery(context.replaceTokens(context.getEntityAttribute(QUERY)));
		}
		return getNext();
	}

	private class BsonDocumentRowIterator implements Iterator<Map<String, Object>> {
		private Iterator<Document> bsonDocumentIterator;

		public BsonDocumentRowIterator(Iterator<Document> bsonDocumentIterator) {
			this.bsonDocumentIterator = bsonDocumentIterator;
		}

		@Override
		public boolean hasNext() {
			return bsonDocumentIterator.hasNext();
		}

		@Override
		public Map<String, Object> next() {
			Map<String, Object> map = new HashMap<String, Object>();

			for (Entry<String, Object> e : bsonDocumentIterator.next().entrySet()) {
					map.put(e.getKey(), e.getValue());
			}

			return map;
		}
	}

	public static final String QUERY = "query";
	public static final String COLLECTION = "collection";
}
