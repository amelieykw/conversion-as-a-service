package com.yukaiwen.Handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.yukaiwen.Back.ConvertVideo;
import com.yukaiwen.Back.SendEmail;
import com.yukaiwen.Back.StoreData;

public class PullQueueHandler {
	
	public void handleTask(JSONObject json) {
		String email = (String) json.get("email");
	    String sla = (String) json.get("sla");
	    String videoName = (String) json.get("videoName");
	    String videoDuration = (String) json.get("videoDuration");
	    
		// Store entities into Datastore
	    StoreData storeData = new StoreData(email, sla, videoName, videoDuration);
	    Key isStored_videoKey = storeData.storeIntoDatastore();
	    
	 	ConvertVideo convertVideo = new ConvertVideo(isStored_videoKey);
		try {
			convertVideo.convert();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // send mail to user 
	 	String url = String.format("http://storage.googleapis.com/conversion-149903.appspot.com/%s", isStored_videoKey);
	 	SendEmail sendEmail = new SendEmail();
	 	sendEmail.sendEmail(email, url);
	}
}
