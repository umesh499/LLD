package Questions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NotificationLLD {
    public static void main(String[] args) throws InterruptedException {
        User alice = new User("u1", "Alice");
        UserPreferences.addPreferences("u1", NotificationType.EMAIL);
        UserPreferences.addPreferences("u1", NotificationType.SMS);
        NotificationFactory notificationFactory = new NotificationFactory();
        NotificationService notificationService = new NotificationService();
        NotificationServiceWorkerMain worker = new NotificationServiceWorkerMain(5, notificationService, notificationFactory);
        Thread workerThread = new Thread(worker);
        workerThread.start();

        notificationService.publishToMainQueue(
                new Notification("n1", alice, "u1", "Your order has shipped!", 1));
        notificationService.publishToMainQueue(
                new Notification("n2", alice, "u1", "OTP: 4821", 0));

        Thread.sleep(500);
        worker.stopWorker();
        workerThread.interrupt();

        System.out.println("Done.");

    }
}
/*
Functional :
1. User should be able to send notifications
2. User should be able to select preferences 
3. should be able to priortize notifications

Non-Functional 
1. Atleast once delivery
2. retry

Core entities
1. Notification
NotificationType
2. User
3. UserPreferences
4. NotificationService
5. NotificationDispatcher
6. NotificationChannel
    SMSNotificationChannel
    EMailNotificationChannel
    WhatsappNotificationChannel
7. NotificationFactory

*/

class Notification{
    String id;
    User user;
    String recipientId;
    int priortity;
    String message;
    NotificationStatus status;
    int retryCount;
    public int getPriortity() {
        return priortity;
    }
    Notification(String id, User user, String recipientId, String message, int priority) {
        this.id = id;
        this.user = user;
        this.recipientId = recipientId;
        this.message = message;
        this.priortity = priority;
        this.status = NotificationStatus.PENDING;
    }
}
enum NotificationStatus{
    SENT,FAILED,PENDING;
}
enum NotificationType{
    SMS,EMAIL,WHATSAPP;
}
class User{
    String tenantId;
    String name;
    User(String tenantId, String name){
        this.tenantId = tenantId;
        this.name = name;
    }
}
class UserPreferences{
    static Map<String, Set<NotificationType>> map = new  HashMap<>();
    static Set<NotificationType> getUserPref(String userId){
        return map.getOrDefault(userId, null);
    }

static void addPreferences(String userId, NotificationType notificationType){
        if(!map.containsKey(userId)){
            map.put(userId, new HashSet<>());
        }
        map.get(userId).add(notificationType);
    }
}

class TaskQueueMain {
    PriorityBlockingQueue<Notification> queue;

    TaskQueueMain() {
        queue = new PriorityBlockingQueue<>(11, 
            (a, b) -> Integer.compare(a.getPriortity(), b.getPriortity())
        );
    }

    synchronized void addNotificationTask(Notification notification){
        queue.add(notification);
    }

    synchronized Notification getNotificationTask(){
        if(queue.size()>0)
            return queue.poll();

        return null;
    }
}



class NotificationService{
    TaskQueueMain taskQueueMain;
    NotificationService(){
        taskQueueMain = new TaskQueueMain();
    }
    
    void publishToMainQueue(Notification notification){
        taskQueueMain.addNotificationTask(notification);
    }

    Notification consumeFromMainQueue(){
            return taskQueueMain.getNotificationTask();
    }
}

interface NotificationChannel{
    boolean sendNotification(Notification notification);
}
class SMSNotificationChannel implements NotificationChannel{

    @Override
    public boolean sendNotification(Notification notification) {
        System.out.println(" sending message via SMS: "+notification.message);
        return true;
    }
    
}

class EmailNotificationChannel implements NotificationChannel{

    @Override
    public boolean sendNotification(Notification notification) {
        System.out.println(" sending message via Email: "+notification.message);
        return true;
    }
    
}

class WhatsappNotificationChannel implements NotificationChannel{

    @Override
    public boolean sendNotification(Notification notification) {
        System.out.println(" sending message via wahtsapp: ");
        return true;
    }
    
}

class NotificationFactory{
    Map<String , NotificationChannel> registry = new HashMap<>();
    NotificationChannel getChannel(String channel){
        if(registry.get(channel) == null){
            registry.put(channel, getChannelByType(channel));
        }
        return registry.get(channel);
    }
    private NotificationChannel getChannelByType(String channel) {
       switch (channel) {
        case "SMS":
            return new SMSNotificationChannel();
        case "EMAIL":
            return new EmailNotificationChannel();
        case "WHATSAPP":
            return new WhatsappNotificationChannel();
        default:
            return null;
       }
    }
}

class NotificationServiceWorkerMain implements Runnable{

    NotificationFactory channelFactory;
    NotificationService notificationService;
    ExecutorService executor ;
    boolean running;
    NotificationServiceWorkerMain(int threadPoolSize, NotificationService notificationService, NotificationFactory notificationFactory){
        executor = Executors.newFixedThreadPool(threadPoolSize);
        this.notificationService = notificationService;
        channelFactory = notificationFactory;
    }
    @Override
    public void run() {
        running = true;
        while(running){
            Notification notification;
            try {
                notification = notificationService.consumeFromMainQueue();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return;
            }
             

            if(notification != null){
                Set<NotificationType> userPref = UserPreferences.getUserPref(notification.recipientId);

                for(NotificationType notificationType : userPref){
                    executor.submit(() -> deliverMessage(notificationType, notification));
                }
            }
        }
        
    }
    private void deliverMessage(NotificationType notificationType, Notification notification)  {
        NotificationChannel channel = channelFactory.getChannel(notificationType.toString());
        int maxRetryCount = 3;
        for(int i=0;i<maxRetryCount;i++){
            if(channel.sendNotification(notification)){
                System.out.println(" sent message: " + notification.message);
                notification.status = NotificationStatus.SENT;
                return;
            }
            notification.retryCount++;
        }
        notification.status = NotificationStatus.FAILED;
        System.out.println(" failed to send  q message: " + notification.message);
    }

    void stopWorker() throws InterruptedException{
        this.running = false;
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
    }
   
    void startWorker(){
        this.running = true;
    }
 
}


