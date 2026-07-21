package assignment;

public class RestuarantService {
    
}

class Restuarant{

}

class Menu{

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

class OrderIdGenerator{
    
}
