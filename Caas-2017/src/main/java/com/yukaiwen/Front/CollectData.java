package com.yukaiwen.Front;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.tools.JavaFileObject;

import com.google.appengine.api.taskqueue.*;
import com.yukaiwen.Back.SendEmail;
import com.yukaiwen.Handler.PullQueueHandler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet(name = "CollectData", urlPatterns = { "/collectdata" })
public class CollectData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Add the task to the corresponding queue.
		String queueName = null;
		String queueHandlerUrl = null;
		JSONObject content = null;
		int numberOfTasksToLease = 0;
		String sla = request.getParameter("sla");

		if (sla == "BRONZE") {
			queueName = "bronzeQueue";
			queueHandlerUrl = "/handler/pushqueuehandler";
			Queue bronzeQueue = QueueFactory.getQueue(queueName);
			bronzeQueue.add(TaskOptions.Builder.withUrl(queueHandlerUrl).method(TaskOptions.Method.GET)
					.param("email", (String) request.getParameter("email"))
					.param("sla", (String) request.getParameter("sla"))
					.param("videoName", (String) request.getParameter("videoName"))
					.param("videoDuration", request.getParameter("videoDuration")));
		} 
		else {
			switch(sla) {
				case "SILVER":
					queueName = "silverQueue";
					numberOfTasksToLease = 3;
					
					content = new JSONObject();
					content.put("email", (String) request.getParameter("email"));
					content.put("sla", (String) request.getParameter("sla"));
					content.put("videoName", (String) request.getParameter("videoName"));
					content.put("videoDuration", request.getParameter("videoDuration"));
					content.put("date", new Date());
		
					Queue silverQueue = QueueFactory.getQueue(queueName);
					silverQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(content.toString())
							.tag("" + content.get("email")));
					List<TaskHandle> silverQueue_tasks = silverQueue.leaseTasksByTag(600, TimeUnit.SECONDS, numberOfTasksToLease,
							"" + content.get("email"));
					for (TaskHandle task : silverQueue_tasks) {
						String payload = new String(task.getPayload(), StandardCharsets.UTF_8);
						JSONParser parser = new JSONParser();
						JSONObject json;
						try {
							json = (JSONObject) parser.parse(payload);
							if (json != null) {
								PullQueueHandler silverQueueHandler = new PullQueueHandler();
								silverQueueHandler.handleTask(json);
								silverQueue.deleteTask(task);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case "GOLD":
					queueName = "goldQueue";
					numberOfTasksToLease = 5;
					content = new JSONObject();
					content.put("email", (String) request.getParameter("email"));
					content.put("sla", (String) request.getParameter("sla"));
					content.put("videoName", (String) request.getParameter("videoName"));
					content.put("videoDuration", request.getParameter("videoDuration"));
					content.put("date", new Date());
		
					Queue goldQueue = QueueFactory.getQueue(queueName);
					goldQueue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(content.toString())
							.tag("" + content.get("email")));
					List<TaskHandle> goldQueue_tasks = goldQueue.leaseTasksByTag(600, TimeUnit.SECONDS, numberOfTasksToLease,
							"" + content.get("email"));
					for (TaskHandle task : goldQueue_tasks) {
						String payload = new String(task.getPayload(), StandardCharsets.UTF_8);
						JSONParser parser = new JSONParser();
						JSONObject json;
						try {
							json = (JSONObject) parser.parse(payload);
							if (json != null) {
								PullQueueHandler goldQueueHandler = new PullQueueHandler();
								goldQueueHandler.handleTask(json);
								goldQueue.deleteTask(task);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
			}
		}
	}
}