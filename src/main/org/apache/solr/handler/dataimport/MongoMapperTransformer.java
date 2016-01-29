package org.apache.solr.handler.dataimport;

import java.util.Map;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class MongoMapperTransformer extends Transformer {
	private static final Logger LOG = LoggerFactory.getLogger(MongoMapperTransformer.class);

	@Override
	public Object transformRow(Map<String, Object> row, Context context) {
		LOG.debug("Transforming row: " + row);
		for (Map<String, String> field : context.getAllEntityFields()) {
			String jsonPath = field.get(JSONPATH);
			if (jsonPath != null) {
				BSONObject obj = new BasicBSONObject(row);
				Object document = Configuration.defaultConfiguration().jsonProvider().parse(obj.toString());
				row.put(field.get(DataImporter.COLUMN), JsonPath.read(document, jsonPath));
			}
		}
		return row;
	}

	public static final String JSONPATH = "jsonpath";
}
