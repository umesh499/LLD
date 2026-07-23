package designPatterns.structural;

import java.util.ArrayList;
import java.util.List;

public class Observer {
    public static void main(String[] args) {
        YoutubeChannel youtubeChannel = new YoutubeChannel("MyChannel", "Uploaded v1");
        youtubeChannel.addSubscriber(new UserSubscriber("Anmol"));
        youtubeChannel.addSubscriber(new UserSubscriber("Amaya"));
        youtubeChannel.changeContent("Uploaded v2");
    }
}


//you have subject , and many subcribers/observers to the subject change in behavoir 

interface Subscriber{
    void notifyMe(String contentId);
}

class UserSubscriber implements Subscriber{
    String name;
    UserSubscriber(String name){
        this.name = name;
    }
    @Override
    public void notifyMe(String contentId) {
       System.out.println(name + ":: UserSubscriber : New content Added"+contentId);
    }
    
}


class YoutubeChannel{
    String name;
    String content;
    List<Subscriber> lst;

    YoutubeChannel(String name, String content){
        this.content = content;
        this.name = name;
        lst = new ArrayList<>();
    }
    void addSubscriber(Subscriber subscriber){
        lst.add(subscriber);
    }

    void removeSubscriber(Subscriber subscriber){
        lst.remove(subscriber);
    }

    void changeContent(String content){
        System.out.println(this.content+"::Old content Updateing the content with new one ::"+content);
        this.content = content;
        System.out.println("Updated content for the channel:::"+name);
        for(Subscriber subscriber : lst){
            subscriber.notifyMe(content);
        }
    }

}