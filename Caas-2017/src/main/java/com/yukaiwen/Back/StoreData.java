package com.yukaiwen.Back;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public class StoreData {
	private String email;
	private String sla;
	private String videoName;
	private String videoDuration;
	private Key videoKey = null;
	private Key userKey = null;
	
	public StoreData(String email, String sla, String videoName, String videoDuration) {
		this.email = email;
		this.sla = sla;
		this.videoName = videoName;
		this.videoDuration = videoDuration;
	}
	
	public Key storeIntoDatastore() {
		// create entities & store them into Datastore
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    // create an entity of type User
	    Entity user = new Entity("User");
	    user.setProperty("email", email);
	    user.setProperty("sla", sla);
	    // store this User entity into Datastore
	    datastore.put(user);
	    
	    // create an entity of type Video
	    Entity video = new Entity("Video", user.getKey());
	    video.setProperty("title", videoName);
	    video.setProperty("duration", videoDuration);
	    video.setProperty("date", new Date());
	    // store this Video entity into Datastore
	    datastore.put(video);
	    
	    try {
			if(datastore.get(user.getKey()) != null && datastore.get(video.getKey()) != null) {
				return video.getKey();
			} else {
				return null;
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
