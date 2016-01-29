# Solr Mongo Importer
Welcome to the Solr Mongo Importer project. This project provides MongoDb support for the Solr Data Import Handler.

I rewrited most code to suit my project's needs, but I think it is also suitable to most cases if you want to index a MongoDB with Solr.

## Features
* Retrive data from a MongoDb collection
* Authenticate using MongoDb authentication
* Map Mongo fields to Solr fields

## Future Plan
* Delta Import

## Classes

* MongoDataSource - Provides a MongoDb datasource
    * database (**required**) - The name of the database you want to connect to
    * host     (*optional* - default: localhost)
    * port     (*optional* - default: 27017)
    * username (*optional*)
    * password (*optional*)
* MongoEntityProcessor - Use with the MongoDataSource to query a MongoDb collection
    * collection (**required**)
    * query (**required**)
* BsonDocumentTransformer - Map MongoDb fields to your Solr schema
    * jsonpath (*optional*) - See [Jayway JsonPath](https://github.com/jayway/JsonPath/), and result should return [List](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)

## Installation
1. Firstly you will need a copy of the Solr Mongo Importer jar.
    ### Getting Solr Mongo Importer
    1. Build your own using the ant build script you will need the JDK installed as well as Ant and Ivy
2. You will also need the [Mongo Java driver JAR](http://mongodb.github.io/mongo-java-driver/) and [Jayway JsonPath](https://github.com/jayway/JsonPath/)
3. Place both of these jars in your Solr core lib folder (If you created a core with name 'MyCore', place 'lib' under folder 'MyCore', i.e. 'MyCore/lib')

##Usage
Here is an example showing the use of all components
MongoDB Collection: Inventory.data
```javascript
{
    "_id": ObjectId("56a5831083180eb506dafdcf"),
    "CompanyData": {
        "name": "My Company"
    },
    "ProductData": [
        {
            "title": "My Title 1",
            "description": "Long Description 1"
        },
        {
            "title": "My Title 2",
            "description": "Long Description 2"
        }
    ]
}
```
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<dataConfig>
    <dataSource name="MyMongo" type="MongoDataSource" database="Inventory" />
    <document name="Products">
        <entity processor="MongoEntityProcessor"
                query="{'CompanyData.name':'My Company'}"
                collection="data"
                datasource="MyMongo"
                transformer="MongoMapperTransformer">
            <field column="title" jsonpath="$.ProductData[*].title" />
            <field column="description" jsonpath="$.ProductData[*].description" />
        </entity>
    </document>
</dataConfig>
```
Solr
```javascript
{
    "_id": "56a5831083180eb506dafdcf",
    "title": [
        "My Title 1",
        "My Title 2"
    ],
    "description": [
        "Long Description 1",
        "Long Description 2"
    ]
}
```
