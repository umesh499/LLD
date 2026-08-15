package Questions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class RateLimitingService {
    public static void main(String[] args) throws Exception{
        // RateLimitStratergy tokenBucketStratergy = new TokenBucketStratergy(2, 1);
        // Request request1 = new Request("umesh", null);
        // for(int i=0;i<300;i++){
        //     boolean isAllowed = tokenBucketStratergy.allowed(request1);
        //     System.out.println(isAllowed+": is allowed : "+System.currentTimeMillis());
            
        //     Thread.currentThread().sleep(100);
        // }

        // RateLimitStratergy slidingWindowStratergy = new SlidingWindowStratergy(2, 1);
        // Request request2 = new Request("umesh", null);
        // for(int i=0;i<300;i++){
        //     boolean isAllowed = slidingWindowStratergy.allowed(request2);
        //     System.out.println(isAllowed+": is allowed : "+System.currentTimeMillis());
            
        //     Thread.currentThread().sleep(1000);
        // }
       
    }
}

class Request{
    String userId;
    String ip;
    Request(String userId, String ip){
        this.userId = userId;
        this.ip = ip;
    }
}

interface RateLimitStratergy{
boolean allowed(Request request);
}



class SlidingWindowStratergy implements RateLimitStratergy{
    Map<String , SlidingWindow> map = new HashMap<>();
    int capacity;
    int windowSizeInMinutes;
    SlidingWindowStratergy(int capacity, int windowSizeInMinutes){
        this.capacity = capacity;
        this.windowSizeInMinutes = windowSizeInMinutes;
    }
    @Override
    public boolean allowed(Request request) {
        SlidingWindow tb = map.get(request.userId);
        if(tb== null){
            tb = new SlidingWindow(this.capacity, request.userId, windowSizeInMinutes);
            map.put(request.userId, tb);
           }
           synchronized(tb){
            return tb.isAllowed();
           }
    }
    static class SlidingWindow{
        Queue<Long> queue = new LinkedList<>();
        int capacity;
        String userId;
        int windowSizeInMinutes;

        SlidingWindow(int capacity, String userId, int windowSizeInMinutes){
            this.capacity = capacity;
            this.userId = userId;
            this.windowSizeInMinutes = windowSizeInMinutes;
        }
        boolean isAllowed(){
            long currentTimeMillis = System.currentTimeMillis();
            long windowStart = currentTimeMillis - this.windowSizeInMinutes*60*1000;
            while(queue.size()>0 &&( queue.peek() < windowStart)){
                    queue.poll();
            }
            if(queue.size()<capacity){
                queue.add(currentTimeMillis);
                return true;
            }
            return false;
        }
    }
}

class TokenBucketStratergy implements RateLimitStratergy{
    Map<String , TokenBucket> map = new HashMap<>();
    int capacity = 10;
    int refillRatePerSecond = 2;
    TokenBucketStratergy(int capacity , int refillRatePerSecond){
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }
    @Override
    public boolean allowed(Request request) {
       TokenBucket tb = map.get(request.userId);
       if(tb== null){
        tb = new TokenBucket(this.capacity, this.refillRatePerSecond);
        map.put(request.userId, tb);
       }
       synchronized(tb){
        return tb.isAllowed();
       }
    }
    static class TokenBucket{
        int capacity;
        long lastRequestTimeStamp;
        int tokens;
        final int refillRatePerSecond;
        TokenBucket(int capacity, int refillRatePerSecond){
            this.tokens = capacity;
            this.capacity = capacity;
            lastRequestTimeStamp = System.currentTimeMillis();
            this.refillRatePerSecond = refillRatePerSecond;
        }
        boolean isAllowed(){
            this.refillToken();
            if(this.tokens >0){
                this.tokens --;
                return true;
            }
            return false;
        }
        void refillToken(){
            long currentTime = System.currentTimeMillis();
            long diff = currentTime-this.lastRequestTimeStamp;
            
            int diffInSec = (int)diff/1000;
            if(diffInSec<=0)
                return;
            this.tokens += Math.min((diffInSec*this.refillRatePerSecond) , capacity);
            this.lastRequestTimeStamp = currentTime;
        }
    
    }
}