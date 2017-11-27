package com.yukaiwen.Handler;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.yukaiwen.Back.ConvertVideo;
import com.yukaiwen.Back.SendEmail;
import com.yukaiwen.Back.StoreData;

/**
 * Servlet implementation class BronzeQueueHandler
 */
@WebServlet(name = "PushQueueHandler", urlPatterns = { "/handler/pushqueuehandler" })
public class PushQueueHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
	    String sla = request.getParameter("sla");
	    String videoName = request.getParameter("videoName");
	    String videoDuration = request.getParameter("videoDuration");
	    
		// Store entities into Datastore
	    StoreData storeData = new StoreData(email, sla, videoName, videoDuration);
	    Key isStored_videoKey = storeData.storeIntoDatastore();
	    
	 	ConvertVideo convertVideo = new ConvertVideo(isStored_videoKey);
		convertVideo.convert();
	    
	    // send mail to user 
	 	String url = String.format("http://storage.googleapis.com/conversion-149903.appspot.com/%s", isStored_videoKey);
	 	SendEmail sendEmail = new SendEmail();
	 	sendEmail.sendEmail(email, url);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
