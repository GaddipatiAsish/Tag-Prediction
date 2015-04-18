package org.so.ml.core;

import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import com.google.gson.JsonObject;

public class SaveQuestion {
	
	// communicate and write to couchdb 
	public static CouchDbClient dbClient = new CouchDbClient("couchdb.properties");

	public static void main(String[] args) {

//		delete();
		
		JsonObject json = new JsonObject();
//		String tagArray[] = {"C++", "Multithreading", "Qt", "QML"};
		String t1 = "javascript";
		String t2 = "ent2";
		String t3 = "coffee";
		String t4 = "html";
		String t5 = "chrome";
//		System.out.println(tagArray);
		json.addProperty("tag1", t1);
		json.addProperty("tag2", t2);
		json.addProperty("tag3", t3);
		json.addProperty("tag4", t4);
		json.addProperty("tag5", t5);
		json.addProperty("question", "Can Asish see this?");
		
		Response r = dbClient.save(json);
		System.out.println(r.toString());
		System.out.println(r.getError());
		
//		System.out.println(findObj.get("question"));
	}
	
	public static void delete() {
		// 
		dbClient.remove("003c973372ed4cffb3025b7df0111736","1-aaebbd04fbdc8d9e5716e75ac3b2dd8d");
		dbClient.remove("078757a189ec44578313ac09210a17a5","1-8d01bd8ce2bf602362e57ce7d6eb0542");
		dbClient.remove("0862d67c2e9444219b972b1a7115ecaf","1-5123eeba50927ffd288f74158fbc03f5");
		dbClient.remove("1eae60c2e0084d038e10b55ac05685bc","1-2fff5461bedbf2052d385c9df69bae6c");
		dbClient.remove("23174e9c265e422c9bb96f376d0256e9","1-d611f7955ba4b52b90c86f0554c43305");
		dbClient.remove("2946a070b49a402dad6adf8aa3fa08dd","1-4d203194ab0b042308d50eb8edfbd2ec");
		dbClient.remove("2b1ba39d891349a5963cfd0029f70452","1-f7733ae6f58d08dde4abd3cd4f6be564");
		dbClient.remove("390401ec0c30496a8b7830602cb6dbc1","1-2fff5461bedbf2052d385c9df69bae6c");
		dbClient.remove("4d76e120ccc94039a39f3607508ff302","1-4d203194ab0b042308d50eb8edfbd2ec");
		dbClient.remove("501c5a13971149f0b55eaaad2dfbef80","1-7a0d1753c37478212e760adc201a02f8");
		dbClient.remove("50581af8cb3e4d6eac28af02e1515948","1-2b5dfc776ecec67985a30919d3db52b8");
		dbClient.remove("540913b6b26447ae9ea8d7812e4a514b","1-6d15e6f95c619e11575dddbcf572bae6");
		dbClient.remove("5861c91c07dc4a45918fd109debbf1e2","1-195ce66850d9fc7a4a9a9c8407560285");
		dbClient.remove("58e0cc2c9aa9450699e74af60c979281","1-b660dc82ea2403e0bbbeabfe2ecc57fd");
		dbClient.remove("5bb240bca5824adbad71d503c50c9a02","1-d501e8e71712ae59976f9a6ec7503567");
		dbClient.remove("5bd24490c466461db9bba6a0089d46b7","1-8d01bd8ce2bf602362e57ce7d6eb0542");
		dbClient.remove("6364530539d741f0ba78e2074cf2fdd4","1-f7733ae6f58d08dde4abd3cd4f6be564");
		dbClient.remove("7c7da9c653d24ba89240b7343c08e31e","1-5ff7d52ff7ee0b50d2a1edcd8ca79464");
		dbClient.remove("85d6f20811b74a28977cde43b28c29ac","1-5123eeba50927ffd288f74158fbc03f5");
		dbClient.remove("9185ed1dd0f542ad9959ffb3fc0f75dc","1-b660dc82ea2403e0bbbeabfe2ecc57fd");
		dbClient.remove("9e7b71eff80d4f639fb0a2426dba1925","1-6d15e6f95c619e11575dddbcf572bae6");
		dbClient.remove("a4cfc5a1e64146bbbc0bb7a25d56e369","1-1de0932bf3b86521a4dd6cecf4472df7");
		dbClient.remove("b3c6ba4b6afb4504b5cbf4546b53b488","1-d501e8e71712ae59976f9a6ec7503567");
		dbClient.remove("b417a7b737044849a07b61bc55c3174a","1-195ce66850d9fc7a4a9a9c8407560285");
		dbClient.remove("c0497143d81143f983195376f0aa38d4","1-2b5dfc776ecec67985a30919d3db52b8");
		dbClient.remove("c1636ff6c7a744a7967bb428c935fc4e","1-5ff7d52ff7ee0b50d2a1edcd8ca79464");
		dbClient.remove("c331bcf9b4fd4884b4a5769b0965625f","1-a1f63803b46cea764209c3e5a6eeed88");
		dbClient.remove("c5d85f20c95e4f43bb1affc530e86fb8","1-a1f63803b46cea764209c3e5a6eeed88");
		dbClient.remove("cee5261070ed4c0eb1d6ad4cd77b6067","1-7a0d1753c37478212e760adc201a02f8");
		dbClient.remove("cf1035608b5c44f2ad830efa1137c043","1-77431563535c159cce450db1e09aca1a");
		dbClient.remove("e0c60b1a407245e791c73a7a19db771c","1-77431563535c159cce450db1e09aca1a");
		dbClient.remove("e2c3deb51aa14a319383ea8bcd332748","1-1de0932bf3b86521a4dd6cecf4472df7");
		dbClient.remove("ef990aa3c03b43efb6c79aa9f7ed1f73","1-aaebbd04fbdc8d9e5716e75ac3b2dd8d");
		dbClient.remove("efd01d1252bd4aeaa5bb70ebd2f2902f","1-1de0932bf3b86521a4dd6cecf4472df7");
	}
}
