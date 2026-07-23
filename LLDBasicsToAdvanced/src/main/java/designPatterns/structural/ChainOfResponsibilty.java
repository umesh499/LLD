package designPatterns.structural;

public class ChainOfResponsibilty {
    public static void main(String[] args) {
        Priority p = new Priority(1);
        Level3LogHandler l3 = new Level3LogHandler(p);
        Level2LogHandler l2 = new Level2LogHandler(p, l3);
        Level1LogHandler l1 = new Level1LogHandler(p, l2);
        l1.handle();
    }
   
}

class Priority{
    private int priority;
    Priority(int priority){
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
}
abstract class LogHandler{
    Priority priority;
    LogHandler(Priority p){
        this.priority = p;
    }
   abstract void handle();
}

class Level1LogHandler extends LogHandler{
    LogHandler nextLogHandler;
    Level1LogHandler(Priority p, LogHandler newHandler) {
            super(p);   
            this.nextLogHandler = newHandler;    
    }
    
    @Override
    public void handle() {
        if(priority.getPriority() <=1){
            System.out.println("Handled priority by Loghandler 1");
        }else{
            nextLogHandler.handle();
        }
    }
    
}
class Level2LogHandler extends LogHandler{
    LogHandler nextLogHandler;
    Level2LogHandler(Priority p, LogHandler newHandler) {
            super(p);   
            this.nextLogHandler = newHandler;    
    }
    
    @Override
    public void handle() {
        if(priority.getPriority() >=2 && priority.getPriority() <= 5){
            System.out.println("Handled priority by Loghandler 2");
        }else{
            nextLogHandler.handle();
        }
    }
    
}
class Level3LogHandler extends LogHandler{
    Level3LogHandler(Priority p) {
            super(p);   
    }
    
    @Override
    public void handle() {
        if(priority.getPriority() >5){
            System.out.println("Handled priority by Loghandler 3");
        }
    }
    
}


