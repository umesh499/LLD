package designPatterns;

public class FactoryMethod {
    static IPaymentFactory PaymentFactory;
    public static void main(String[] args) {
        PaymentFactory = new RazorpayPaymentFactory();
        IPayment Payment = PaymentFactory.getPaymentInstance();
        Payment.payUser(1000);
    }
}

interface IPayment{
    void payUser(int amount);
}

class UPIPayment implements IPayment{
    @Override
    public void payUser(int amount) {
        System.out.println("Send UPI"+ amount);
    } 
}

class RazorpayPayment implements IPayment{
    @Override
    public void payUser(int amount) {
        System.out.println("Send Razorpay"+ amount);
    } 
}

interface IPaymentFactory{
    IPayment getPaymentInstance();
}

class UPIPaymentFactory implements IPaymentFactory{
    @Override
    public IPayment getPaymentInstance() {
        return new UPIPayment();
    } 
}

class RazorpayPaymentFactory implements IPaymentFactory{
    @Override
    public IPayment getPaymentInstance() {
        return new RazorpayPayment();
    } 
}

