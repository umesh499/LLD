package designPatterns;

public class Singleton {
    public static void main(String[] args) {
        
        Runnable task = ()  -> {
            MySingleton.getInstance();
        };
        for(int i=0;i<100;i++){
            new Thread(task, "T"+i).start();
        }

    }
}

class MySingleton{
    static MySingleton instance;
    private MySingleton(){
        System.out.println(Thread.currentThread().getName());
    }

    public static MySingleton getInstance(){
        if(instance == null){
            synchronized(MySingleton.class){
                if(instance == null){
                    instance = new MySingleton();
                }
            }
        }
        
        return instance;
    }
}
