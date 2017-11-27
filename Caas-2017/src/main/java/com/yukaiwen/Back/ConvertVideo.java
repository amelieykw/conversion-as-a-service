package com.yukaiwen.Back;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class ConvertVideo {
	private GcsService gcsService = GcsServiceFactory.createGcsService();
	private Key videoKey;
	private Key userKey;
	
	public ConvertVideo(Key videoKey) {
		this.videoKey = videoKey;
		this.userKey = videoKey.getParent();
	}
	
	public void convert() throws IOException {
		// simulate the conversion
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// create a random file in Google Cloud Storage
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity video = datastore.get(videoKey);
			Long fileSize = (long) video.getProperty("duration");
			// code for adding video in Google Cloud Storage
			GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		    GcsFilename fileName = new GcsFilename("conversion-149903.appspot.com", videoKey.toString());
		    GcsOutputChannel outputChannel;
		    outputChannel = gcsService.createOrReplace(fileName, instance);
		    create(Channels.newOutputStream(outputChannel), fileSize);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void create(OutputStream output, Long fileSize) throws IOException {
		try {
		      byte[] buffer = new byte[fileSize.intValue() * 1024 * 1024];
		      output.write(buffer);
		    } finally {
		      output.close();
		    }
	}
	
}
