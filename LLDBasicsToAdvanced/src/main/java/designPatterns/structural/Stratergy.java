package designPatterns.structural;

public class Stratergy {
    public static void main(String[] args) {
        //old client
        new PaymentService(new CreditCardStratergy()).makePayment(100);
        new PaymentService(new UPIStratergy()).makePayment(1000);
    }
}

class PaymentService {
    PaymentStratergy paymentStratergy;
    PaymentService(PaymentStratergy paymentStratergy){
        this.paymentStratergy = paymentStratergy;
    }

    //void makePayment(int amount, String type){
        //old client
        // if(type.equals("UPI")){
        //     new UPIStratergy().pay(amount);
        // }else if(type.equals("CC")){
        //     new CreditCardStratergy().pay(amount);
        // }

        //paymentStratergy.pay(amount);
   // }

    void makePayment(int amount){
        //old client
        // if(type.equals("UPI")){
        //     new UPIStratergy().pay(amount);
        // }else if(type.equals("CC")){
        //     new CreditCardStratergy().pay(amount);
        // }

        paymentStratergy.pay(amount);
    }
}

interface PaymentStratergy{
    void pay(int amount);
}

class CreditCardStratergy implements PaymentStratergy{
    @Override
    public void pay(int amount) {
        System.out.println("Creditcard payment"+ amount);
    }   
}

class UPIStratergy implements PaymentStratergy{
    @Override
    public void pay(int amount) {
        System.out.println("Upi payment"+ amount);
    }   
}

