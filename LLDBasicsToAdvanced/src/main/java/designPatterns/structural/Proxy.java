package designPatterns.structural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {
   public static void main(String[] args) {
        //old client
        // IObject obj = new RealObject();
        // obj.doSomething("xyz");

        //new client
        
        IObject object = new ProxyCacheObject("ADMIN1");
        object.doSomething("xyz");
   }
}

interface IObject{
    void doSomething(String inp);
}

class RealObject implements IObject{

    @Override
    public void doSomething(String inp) {
        System.out.println("Done something::"+inp);
    }
    
}


class ProxyCacheObject implements IObject{
    IObject object;
    Map<String, IObject> cache;
    String role;
    ProxyCacheObject(String role){
        cache = new HashMap<>();
        this.role = role;
    }
    
    @Override
    public void doSomething(String inp) {
        if(!this.role.equals("ADMIN"))
            return;
       if(cache.containsKey(inp)){
            object = cache.get(inp);
       }else{
            object = new RealObject();
       }
       object.doSomething(inp);
    }
    
}
