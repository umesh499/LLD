package designPatterns.structural;

public class Adapter {
    
    public static void main(String[] args) {
        //old client
        IPayment payment = new Application();
        payment.pay(100);

        //new client
        IPayment adapterPayment = new StripeAdapter(new StripePayment());
        adapterPayment.pay(100);
    }
   
}

interface IPayment{
    void pay(int amount);
}

class Application implements IPayment{

    @Override
    public void pay(int amount) {
        System.out.println("Legacy Payment"+amount);
    }
    
}

//3rd Party
class StripePayment{
    void makePayment(int amount){
        System.out.println("Stripe Payment"+amount);
    }
}

class StripeAdapter implements IPayment{
    StripePayment stripePayment;
    StripeAdapter(StripePayment stripePayment){
        this.stripePayment = stripePayment;
    }
    @Override
    public void pay(int amount) {
        stripePayment.makePayment(amount);
    }

}