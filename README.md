# Caas - Conversion as a Service

It's a native web application for transcoding asynchronously the videos uploaded by users to different formats and qualities.
https://conversion-149903.appspot.com

# Codes 
- [Version](https://github.com/amelieykw/conversion-as-a-service-2017) de **YU Kaiwen** - yu.kaiwen.amelie@gmail.com - @amelieykw 

- [Version](https://github.com/obisama/Cloud_Computing_Project) de **Abdelmoughit Afailal** - abdou.afailal.1994@gmail.com - @obisama 

# Contents
1. [Introduction](https://github.com/obisama/Cloud_Computing_Project#introduction)
2. [Scenario of Execution](https://github.com/obisama/Cloud_Computing_Project#scenario-of-execution)
3. [Principal Servlets of Functionalities](https://github.com/obisama/Cloud_Computing_Project#principal-servlets-of-functionalities)
4. [Global Architecture](https://github.com/obisama/Cloud_Computing_Project#global-architecture)  
5. [Detailed Architecture for Each Level of Quality of Service](https://github.com/obisama/Cloud_Computing_Project#detailed-architecture-for-each-level-of-quality-of-service)
6. [URL Mapping](https://github.com/obisama/Cloud_Computing_Project#url-mapping)
7. [Pricing](https://github.com/obisama/Cloud_Computing_Project#pricing)
8. [Conclusion](https://github.com/obisama/Cloud_Computing_Project#conclusion)
9. [Authors](https://github.com/obisama/Cloud_Computing_Project#authors)



# Introduction

Conversion as a Service (CaaS) is a native cloud application for transcoding video asynchronously. It provides only one type of conversion and 3 levels of quality of service : **“Bronze”**, **“Silver”** and **“Gold”**. 

- The **“Bronze”** level : the video duration cannot surpass 1 minute. Each time, each user can only do the conversion for one video. All the videos of the users who choose this level will be put in one commun queue and converted one by one in their order. The converted videos will be conserved for 5 min before they are deleted. 

- The **“Silver”** level : there’s no restriction on the duration of videos.  Each time, each user can do the conversion for 3 videos in parallel. The converted videos will be conserved for 5 min before they are deleted.

- The **“Gold” level”** :  there’s no restriction on the duration of videos. Each time, each user can do the conversion for 5 videos in parallel. The converted videos will be conserved for 10 min before they are deleted.

# Scenario of Execution

![Figure 1. Sequence diagram representing the scenario of execution](https://github.com/anonymous10683/Figure_ss/blob/master/Sequence-Diagram.jpg)


A user uses a Google account to login to the application. Once he logged in successfully, he’ll access to the welcome page of this application where he’ll be asked to choose one level of quality of service. *“Bronze”*? *“Silver”*? or *“Gold”*?

No matter which level of quality of service the user chooses, he will be redirected to a form where the user can upload the video. In this project, this application will not offer the conversion service. The user will be asked to enter the name and the duration of the video. An email address of the user is required in order to inform the user about the status of conversion and give the user the link of the converted video to download. 

If the user chose the *“Bronze”* level, he can only enter the information of one video in this step. The duration of video can not be larger than 1 minute. If the user chose the *“Silver”* level, he can enter the information of three videos in this step. The duration of video will not be limited. The bloque of information for one video is independent from the bloque for another video. On the server side, each bloque of information will be used for transcoding the corresponding video. Three bloques of information will be processed in parallel. If the user chose *“Gold”* level, he can enter the information of five videos in this step. The duration of video will not be limited too. In this case, five bloques of information will be processed in parallel.

Once the user finished the input of information, the application will store those bloques of information on the database where random files will be created by using those bloques of information. One random file corresponds to one bloque. Each random file will be 1Mb per second. All the random files created will be associated to the corresponding user under his Google account.  

Once the information entered by the user has been stored in the database and the random files associated to the user have been created, an email will be sent to inform the user that the conversion has started. In this email, the user will find a link to a page describing the status of conversion where the user can know if the conversion of video is in waiting, in progress or done. It shows the time used for each status. Since in this project, the application will not provide the true conversion of video, the process of conversion will be simulated by the time.

Once one video has been converted to the one format requested by the user, the user can find a link on the page to download the converted video. This link will be invalid in several minutes. If the user chose *“Bronze”* level of quality of service, the time restriction will be 5 minutes. If the user chose *“Silver”* level, the time restriction will be the same. If the user chose *“Gold”* level, the time restriction will be 10 minutes.

# Principal Servlets Functionalities

According to the scenario described in the previous section, this application has 4 functionalities : collection of information about the videos and the user’s contact, save the information collected in the database, create a random file of 1Mb/s in the database, send emails to the user. So technically, we can have 4 corresponding servlets to realise these functionalities.

- **CollectInfoServlet**

This servlet will be responsible for providing different interfaces according to the choice of quality of service made by the user. 
If the user chose the “Bronze” level, *CollectInfoServlet* will generate an interface where there’s only one bloque of form which allows the user to enter the information of one video.

If the user chose the “Silver” level, *CollectInfoServlet* will generate an interface where there will be three bloques of form which allows the user to enter the information of three different videos, so that during this session of conversion, three videos can be processed in parallel for this user.

If the user chose the “Gold” level, *CollectInfoServlet* will generate an interface where there will be five bloques of form which allows the user to enter the information of five different videos, so that during this session of conversion, five videos can be processed in parallel for this user.

- **SaveInfoServlet**

No matter what level of quality of service the user chose and how many bloques of information will be collected, all the bloques of information will be stored associated to this user in the database. *SaveInfoServlet* will be responsible for handling this task.

- **CreateFileServlet**

This application will not provide the real conversion service, so CreateFileServlet will be responsible for creating a random file of 1MB per second in the database in order to simulate the storage of real videos uploaded by the user.

- **SendEmailServlet**

SendEmailServlet will be responsible for creating and sending all the emails during each session of conversion for each user.

# Global Architecture

![Figure 2. Global Architecture](https://github.com/anonymous10683/Figure_ss/blob/master/architecture.png)

Since we are clear of the functionalities and the technical servlets, we can design the whole software architecture	for this application. 

In order to optimize the performance of the whole cloud native application, we should do the division of functionality. We should not only design a servlet for each functionality, but also put the module of each functionality into a worker (the workers can be the physical machines distributed in the different places or virtual machines on the cloud server). 
The benefit of the division of functionality is that each worker can be duplicated according to the real-time demandes. This can realise the scalability. Since we divide the application and put its’ different parts into different workers, it’ll be the workers of each functionality to be duplicated instead of the workers for the whole application, because sometimes not all the parts need more capabilities to handle the network traffic, but only several parts has crowed tasks to do. This keeps the cost of application in a good place.

However, the different parts of application are put into different workers which may be on the physical machines distantes. How can they interact with each other to be a whole service of conversion offered by this application to the users? 
We have a main worker which contains the welcome page and  the MainServlet of application. The MainServlet generates the whole flow of operations needed by the application during each session of conversion. During this flow, the four workers of functionality will be called orderly to do their jobs. Since this is also a worker, it can be duplicated according to the real-time demandes like the four workers of functionality. 

The main worker is the entry point of the application. It will receive all the requests from all the users. It will be in charge of dividing each request into small tasks according to different functionalities. It needs the task queues to help him to put the tasks beside and handle them later when it has time. 

This is the part of application. This part will be deployed to the cloud server. But how a request sent from the browser of the user can arrive to our application?

The user types the address of our web application in the browser. The request finds the cloud server where sites our web application. In the cloud server, especially the public server, there will be lots of applications. Now we need a proxy server to lead the request to arrive to the code part of our application. Since the worker which contains the mainServlet (entry point) of our application may be duplicated, we need the load balancer to find the available worker to receive the new arrived requests.

# Detailed Architecture for Each Level of Quality of Service

In our project we decided to use three queues, one queue for every quality of service level. For the “Bronze” level we use a Push queue, while for “Gold” and “Silver” levels  we will use a Pull queue for each level.

- **“Bronze” Level**

For The “Bronze” level  the video duration cannot surpass 1 minute, so the process execution time is less than 3 minutes. All the videos of the other users who choose this level are put in one commun queue and converted one by one. Hence, we have chosen to work with a Push queue and to limit our number of instances to one which should reduce costs and respect the SLA agreement at the same time. In order to that we will use basic scaling for the worker class of the bronze level conversion, and at the same time we will preserve the idle-timeout value to kill inactive instance when it is not in use

EXAMPLE of the code to put in appengine.web.xml configuration file :

![figure 3](https://github.com/anonymous10683/Figure_ss/blob/master/figure%203.png)


- **“Silver” and “Gold” levels**

In the “Silver” and “Gold” quality of service levels, there is no maximum duration for the video to convert. We know that the “Silver” level user can convert three videos simultaneously (converted in parallel) and the “Gold” level user can transcode five videos at the same time. Assuming this is possible for each user, we decided to use Pull queues. We believe using Pull queues offer more control for the execution of the tasks, unlike the default Push queues. In Push queues, we just need to specify the handler and queue configuration, App Engine will automatically scale processing capacity to match that configuration as well as delete the tasks after processing. On the other hand, Pull queues, requires manual intervention by our application which can be done internally via the Task Queue API and we will have to deal with scalability and task execution.This model will gives us the possibility to execute all the user tasks simultaneously.

For instance, assuming that we have two silver level users, one decide to convert one video, the other decides to convert three videos, the use of push queues in this case  will execute the tasks in sequence, and wen can find ourselves in the  case where  instances are running just user’s 1 task , and user’s 2 first and second task, while the third task is not executed and is still waiting in the queue causing the non respect of the SLA.

As a solution, we decided to use pull queues, this will give us the possibility to manage scalability and the tasks on the queue, which will allow our application to access to each user tasks using a group tag which can’t be done with  push queues. Hence, we can run all the tasks in the same time in order to reduce the waiting time.


# URL Mapping

The table below shows the initial URL mapping of each servlet and its corresponding URL. The web container of each servlet should be invoked from the corresponding URL entered by the user.  


| **Servlet Name**  | **Servlet** | **URL** |
|--|--|--|
|Main Servlet|```MainServlet```|```/index.html```|
|Collect Info Servlet| ```CollectInfoServlet``` | ```/collect_video_information``` |
|Save Info Servlet| ```SaveInfoServlet ``` | ```/save_video_information``` |
|Create File Servlet| ```CreateFileServlet ``` | ```/create_video_file``` |
|Send Email Servlet| ```SendEmailServlet ``` | ```/send_email``` |


# Pricing 

One of The most decisive factors is Pricing, the higher an application consumes resources, the higher the bill will get. Our goal is to reduce the cost.

Supposing the user charge is not high :

EX : 0 to 100 bronze users per day :

We have 1 instance :

running time start from 0 and can be up from 100 min to 250 min of instance work , that equal to 5 instance hour max; which will cost us nothing because it’s below the daily free quota given to basic scaling (8 instance hours).but given the use of the outgoing bandwidth (5GB appox.) and apis( Mail.. ) calls, the price can get to $0.02/day.

Wich means 0.02/5 =0.004 $ for evey instance hour.

EX:500 to 1000 per day :

We will need approx. 60GB of outgoing bandwidth , and up to 2500 min of execution time which equals : 42 instance hours which will cost 52.83$/day.


# Conclusion

This is a native cloud application which provides a service of video conversion for users. It provides 3 levels of quality of service : “Bronze”, “Silver” and “Gold”. By choosing different level of quality of service, the users can transcode different numbers of videos and the time allowing the users to download the converted videos will be different. The users will be informed by the emails with the beginning and the termination of the whole process of conversion and by a page with the intermediaire status of process. So technically, this application has four principal functionalities : collection of information about the videos, save of these information collected to the database, create a random file in the database to simulate the real video, send emails to the user. This application uses four workers, each worker for one functionality. There’s another worker for the main module to divide the incoming request into tasks according to the four principal functionalities and put these tasks into the task queue. Each level of quality of service has different configuration of task queue in order to reduce the cost and maintain a good performance. 


# Authors

- **YU Kaiwen** - yu.kaiwen.amelie@gmail.com - @amelieykw 
- **Hamza Sahli** - sh.hamza.90@gmail.com - @anonymous10683 
- **Abdelmoughit Afailal** - abdou.afailal.1994@gmail.com - @obisama 
- **Hamza Lahbabi** - hamza.lahbabi5@gmail.com - @hamzalahbabi 

