package assignment;

import java.util.concurrent.atomic.AtomicInteger;

public class RestuarantService {
    
}

class Restuarant{

}

interface IPayment{
    void pay();
}

class CreditCardPay implements IPayment{
    @Override
    public void pay() {
        System.out.println("Paid via Credit card");
    }
}

class UPIPay implements IPayment{
    @Override
    public void pay() {
        System.out.println("Paid via UPI");
    }   
}

class WalletPay implements IPayment{
    @Override
    public void pay() {
        System.out.println("Paid via Wallet");
    }
}

class CODPay implements IPayment{
    @Override
    public void pay() {
        System.out.println("Paid via COD");
    }   
}

class PaymenProcessorFactory{
    IPayment getPaymentInstance(String payType){
        if(payType.equals("UPI")){
            return new UPIPay();
        }else if(payType.equals("CreditCard")){
            return new CreditCardPay();
        }else if(payType.equals("Wallet")){
            return new WalletPay();
        }
        return new CODPay();
    }
}


interface Menu{
    int getTotalItemCount();
    double 
}

class OrderIdGenerator{
    static AtomicInteger count;
    private static OrderIdGenerator instance;
    private OrderIdGenerator(){
    }
    public static OrderIdGenerator getInstance(){
        if(instance == null){
            synchronized(OrderIdGenerator.class){
                if(instance == null){
                    instance = new OrderIdGenerator();
                }
            }
        }
        return instance;
    }

    public int getId(){
        return count.incrementAndGet();
    }
}
