package designPatterns;

public class Factory {
    public static void main(String[] args) {
        Notification notification = NotificationFactory.getNotificationInstance("email");
        notification.sendNotification();
    }
   
}

interface Notification {
    void sendNotification();
} 

class SMSNotification implements Notification{

    @Override
    public void sendNotification() {
       System.out.println("Send SMS");
    }

}

class EmailNotification implements Notification{

    @Override
    public void sendNotification() {
        System.out.println("Send Email");
    }

}


class NotificationFactory{
    static Notification getNotificationInstance(String type){
        if(type.equals("sms"))
                return new SMSNotification();
        else if(type.equals("email"))
                return new EmailNotification();
        return null;
    }
}