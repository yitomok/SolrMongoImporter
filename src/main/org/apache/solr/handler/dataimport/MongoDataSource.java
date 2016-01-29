package org.apache.solr.handler.dataimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDataSource extends DataSource<Iterator<Document>> {
	private static final Logger LOG = LoggerFactory.getLogger(MongoDataSource.class);

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;

	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public Iterator<Document> getData(String query) {
		Document doc = Document.parse(query);
		return collection.find(doc).iterator();
	}

	public Iterator<Document> getData(String query, String collection) {
		LOG.debug("Executing Query: " + query + " on collection: " + collection);
		this.collection = database.getCollection(collection, Document.class);
		return getData(query);
	}

	@Override
	public void init(Context context, Properties initProps) {
		String database = initProps.getProperty(DATABASE);
		String host = initProps.getProperty(HOST, "localhost");
		String port = initProps.getProperty(PORT, "27017");
		String username = initProps.getProperty(USERNAME);
		String password = initProps.getProperty(PASSWORD);
		if (database == null) {
			throw new DataImportHandlerException(DataImportHandlerException.SEVERE, "Database must be supplied");
		}
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		if (username != null) {
			credentials.add(MongoCredential.createCredential(username, database, password.toCharArray()));
		}
		this.client = new MongoClient(new ServerAddress(host, Integer.parseInt(port)), credentials);
		this.client.setReadPreference(ReadPreference.secondaryPreferred());
		this.database = this.client.getDatabase(database);
	}

	public static final String DATABASE = "database";
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
}
