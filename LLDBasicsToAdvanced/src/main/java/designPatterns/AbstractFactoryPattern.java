package designPatterns;

public class AbstractFactoryPattern {
    public static void main(String[] args) {
        NotificationService.notifyUser(new EmailNotificationFactory() );
    }
}

class NotificationService{
    static void notifyUser(NotificationFactoryOrchestrator notificationFactoryOrchestrator){
        try {
            if(notificationFactoryOrchestrator.getValidateNotification().validateNotification()){
                notificationFactoryOrchestrator.getSendNotification().sendNotification();
                throw new RuntimeException();
            }
        } catch (Exception e) {
            notificationFactoryOrchestrator.getRetryNotification().retryNotification();
        }
    }
}

interface ISendNotification {
    void sendNotification();
} 

interface IRetryNotification {
    void retryNotification();
} 

interface IValidateNotification {
    boolean validateNotification();
} 

class SMSSendNotification implements ISendNotification{

    @Override
    public void sendNotification() {
       System.out.println("Send SMS");
    }

}

class SMSRetryNotification implements IRetryNotification{

    @Override
    public void retryNotification() {
       System.out.println("Retry SMS");
    }

}

class SMSValidateNotification implements IValidateNotification{

    @Override
    public boolean validateNotification() {
       System.out.println("Validate SMS");
       return true;
    }

}

class EmailSendNotification implements ISendNotification{

    @Override
    public void sendNotification() {
        System.out.println("Send Email");
    }

}

class EmailRetryNotification implements IRetryNotification{

    @Override
    public void retryNotification() {
       System.out.println("Retry Email");
    }

}

class EmailValidateNotification implements IValidateNotification{

    @Override
    public boolean validateNotification() {
       System.out.println("Validate Email");
       return true;
    }

}

interface NotificationFactoryOrchestrator {
    IValidateNotification getValidateNotification();
    IRetryNotification getRetryNotification();
    ISendNotification getSendNotification();
}

class SMSNotificationFactory implements NotificationFactoryOrchestrator{

    @Override
    public IValidateNotification getValidateNotification() {
       return new SMSValidateNotification();
    }

    @Override
    public IRetryNotification getRetryNotification() {
        return new SMSRetryNotification();
    }

    @Override
    public ISendNotification getSendNotification() {
       return new SMSSendNotification();
    }
    
}

class EmailNotificationFactory implements NotificationFactoryOrchestrator{

    @Override
    public IValidateNotification getValidateNotification() {
       return new EmailValidateNotification();
    }

    @Override
    public IRetryNotification getRetryNotification() {
        return new EmailRetryNotification();
    }

    @Override
    public ISendNotification getSendNotification() {
       return new EmailSendNotification();
    }
    
}