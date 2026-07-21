package designPatterns.structural;

public class Decorator {
    public static void main(String[] args) {
        INotification notification = new JSONFormatDecorator(new RateLimitDecorator(new SMSNotification()));
        notification.sendMessage(" ::hi umesh");

    }
}

interface  INotification {
    void sendMessage(String message);
}
class  SMSNotification implements INotification {
    @Override
    public void sendMessage(String message) {
        System.out.println("Send SMS message"+message);
    }
}

class  WhatsAppNotification implements INotification {
    @Override
    public void sendMessage(String message) {
        System.out.println("Send WhatsApp message"+message);
    }
}


interface NotificationDecorator extends INotification{

}

class JSONFormatDecorator implements NotificationDecorator{

    private INotification wrappedObj;
    JSONFormatDecorator(INotification wrappedObj){
        this.wrappedObj = wrappedObj;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("JSON format message");
        wrappedObj.sendMessage(message);
    }

}

class RetryDecorator implements NotificationDecorator{

    private INotification wrappedObj;
    RetryDecorator(INotification wrappedObj){
        this.wrappedObj = wrappedObj;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("Retry message");
        wrappedObj.sendMessage(message);
    }

}


class RateLimitDecorator implements NotificationDecorator{

    private INotification wrappedObj;
    RateLimitDecorator(INotification wrappedObj){
        this.wrappedObj = wrappedObj;
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("RateLimit check message");
        wrappedObj.sendMessage(message);
    }

}
