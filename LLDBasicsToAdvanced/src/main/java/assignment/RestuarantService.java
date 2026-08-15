package assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class RestuarantService {
    static MenuTree root = new Category("Menu");
    public static void main(String[] args) {

        System.out.println("Show Menu !\\n");
        getMenuDetails();
        //System.out.println("Customer Order Item !\n");
        MenuItem item = new MenuItem("Paneer Butter Masala", 220,1);

        //System.out.println("Customer Customise Item !");
        MenuTree m = new PackagingDecorator(new CheeseDecorator(item, 20), 10);
       // System.out.println("Customised Item Price:::"+m.printMenuItem());
        List<MenuTree> menuList = new ArrayList<>();
        menuList.add(item);
        IPayment payment = PaymenProcessorFactory.getPaymentInstance("UPI");
        Order order = new Order.OrderBuilder().setAddress("Hyderabad")
                                              .setCustomerName("Umesh")
                                              .setDeliverySlot("4-5pm")
                                              .setMenu(menuList)
                                              .setPayment(payment)
                                              .setStratergy(new PeakHourSurgeDeliveryPartnerFeeStratergy())
                                              .build();
        order.addObservers(new OrderStatusObserver("Customer app", order));
        order.addObservers(new OrderStatusObserver("Restaurant dashboard", order));
        order.addObservers(new OrderStatusObserver(" Delivery partner app", order));
        System.out.println(root.getItemCountByName("Paneer Butter Masala"));
        System.out.println("Order Created : "+order.orderId);
        OrderState state = new OrderState(order);
        if(isOrderValid(order)){
            state.confirmAndCheckoutOrder();
            state.confirmAndCheckoutOrder();
            state.prepareOrder();
            state.deliverOrder();
            System.out.println("Current Order Status::"+order.status);
        }else {
            state.cancelOrder();
            System.out.println("Current Order Status::"+order.status);
        }
        
    }




    private static boolean isOrderValid(Order order) {
        OrderValidator l4 = new FraudCheck(order, root, null);
        OrderValidator l3 = new PaymentPreCheck(order, root, l4);
        OrderValidator l2 = new DeliveryZone(order, root, l3);
        OrderValidator l1 = new ItemAvailability(order, root, l2);
        if(!l1.validate()){
            System.out.println("Invalid Order");
            order.setStatus("Cancelled");
            return false;
        }
        return true;
    }

    private static void getMenuDetails() {
       
        MenuTree starter = new Category("Starters");
        MenuTree vegStarter = new Category("Veg Starters");
        MenuTree mainCourse = new Category("Main Course");
        MenuTree vegMainCourse = new Category("Veg Main Course");
        MenuTree nvMainCourse = new Category("NonVeg Main Course");
        root.addMenu(starter);
        root.addMenu(mainCourse);
        starter.addMenu(vegStarter);
        mainCourse.addMenu(nvMainCourse);
        mainCourse.addMenu(vegMainCourse);
        vegMainCourse.addMenu(new MenuItem("Paneer Butter Masala", 220, 7));
        vegMainCourse.addMenu(new MenuItem("Dal", 180,3));
        vegMainCourse.addMenu(new MenuItem("Veg Biriyani", 250,4));
        nvMainCourse.addMenu(new MenuItem("Butter Chicken", 280,2));
        nvMainCourse.addMenu(new MenuItem("Chicken Biriyani", 300,5));
        vegStarter.addMenu(new MenuItem("Paneer tikka", 200,10));
        vegStarter.addMenu(new MenuItem("Veg Manchurian", 180,10));
        root.printMenuItem();
    }
}



// DeliveryPartnerServiceProxy - Fetch delivery Parters from the cache rather than DB 

class Partners {
    String name;
    int lat;
    int lon;
    Partners(String name, int lat, int lon){
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }
    @Override
    public String toString() {
        return this.name +" :lat: "+this.lat +" :lon: "+this.lon;
    }
}
interface IDeliveryPartnerService{
    List<Partners> getMatchingDeliveryPartners(int lat, int lon);
}
class  DeliveryPartnerService implements IDeliveryPartnerService{

    List<Partners> lst =  new ArrayList<>();
    @Override
    public List<Partners> getMatchingDeliveryPartners(int lat, int lon) {
        
        if(lat>=10 && lon>=10){
            addPartners();
        }
        return this.lst;
    }

    void addPartners(){
        lst.add(new Partners("Ram", 10, 12));
    }
    
}

class  DeliveryPartnerServiceProxy implements IDeliveryPartnerService{
    String role;
    IDeliveryPartnerService deliveryPartnerService;
    List<Partners> cacheLstPartners;
    DeliveryPartnerServiceProxy(String role){
        this.role = role;
        cacheLstPartners = new ArrayList<>();
    }

    @Override
    public List<Partners> getMatchingDeliveryPartners(int lat, int lon) {
        if(!role.equals("ADMIN")){
            throw new RuntimeException("No access");
        }
        if(cacheLstPartners.size() == 0){
            if(deliveryPartnerService == null)
                deliveryPartnerService = new DeliveryPartnerService();

            cacheLstPartners = deliveryPartnerService.getMatchingDeliveryPartners(lat, lon);
        }
        return cacheLstPartners;
    }
    
}




//Partener fee based on 
// (flat / distance-based / peak-hour surge) 
// selected at runtime and applied to compute the final delivery charge
interface   IDeliveryPartnerFeeStratergy{
    int getPartnerFee();
}

class FlatDeliveryPartnerFeeStratergy implements IDeliveryPartnerFeeStratergy{
    @Override
    public int getPartnerFee() {
       return 10;
    }
    
}

class PeakHourSurgeDeliveryPartnerFeeStratergy implements IDeliveryPartnerFeeStratergy{
    
    @Override
    public int getPartnerFee() {
       return 15;
    } 
}

class DistanceDeliveryPartnerFeeStratergy implements IDeliveryPartnerFeeStratergy{
    int dist;
    DistanceDeliveryPartnerFeeStratergy(int dist){
        this.dist = dist;
    }
    @Override
    public int getPartnerFee() {
       return 10 * this.dist;
    } 
}

//Order status observer, subject is Order status , Customer app, Restaurant dashboard,
// and Delivery partner are the subscribers of this oject

interface IOrderStatusObserver{
    void notifyStatusChange();
}

class OrderStatusObserver implements IOrderStatusObserver{

    String observerName;
    Order order;
    OrderStatusObserver(String name, Order order){
        this.observerName = name;
        this.order = order;
    }


    @Override
    public void notifyStatusChange() {
        System.out.println(this.observerName+" : Observer Notified for the status change to "+order.status);
    }
    
}



//Order LifeCycle - State
//  PLACED -> CONFIRMED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
//PLACED->CANCELLED
//PLACED -> CONFIRMED ->CANCELLED
interface IOrderState{
    void confirmAndCheckoutOrder();
    void prepareOrder();
    void deliverOrder();
    void cancelOrder();
}

class OrderState implements IOrderState{
    IOrderState currentState;
    IOrderState placedOrderState;
    IOrderState confirmedOrderState;
    IOrderState outForDeliveryState;
    IOrderState cancelledOrderState;
    IOrderState prepareingOrderState;
    IOrderState deliveredOrderState;
    Order order;
    OrderState(Order order){
        this.order = order;
        placedOrderState = new PlaceOrder(this);
        confirmedOrderState = new ConfirmOrder(this);
        outForDeliveryState = new OutForDeliveryOrder(this);
        cancelledOrderState = new CancelledOrder(this);
        deliveredOrderState = new DeliveredOrder(this);
        prepareingOrderState = new PrepareOrder(this);
        currentState = placedOrderState;
        
    }

    

    public IOrderState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String status) {
        this.order.setStatus(status);
    }
    public IOrderState getCancelledOrderState() {
        return cancelledOrderState;
    }

    public IOrderState getConfirmedOrderState() {
        return confirmedOrderState;
    }

    public IOrderState getDeliveredOrderState() {
        return deliveredOrderState;
    }

    public IOrderState getOutForDeliveryState() {
        return outForDeliveryState;
    }

    public IOrderState getPlacedOrderState() {
        return placedOrderState;
    }

    public IOrderState getPrepareingOrderState() {
        return prepareingOrderState;
    }

    void setNextState(IOrderState nextState){
        this.currentState = nextState;
    }

    @Override
    public void confirmAndCheckoutOrder() {
        currentState.confirmAndCheckoutOrder();
    }

    @Override
    public void prepareOrder() {
       currentState.prepareOrder();
    }

    @Override
    public void deliverOrder() {
        IDeliveryPartnerService partnerService = new DeliveryPartnerServiceProxy("ADMIN");
        List<Partners> partners = partnerService.getMatchingDeliveryPartners(10, 20);
        currentState.deliverOrder();
        System.out.println("Assigned delivery Partner"+partners.get(0));
    }

    @Override
    public void cancelOrder() {
        currentState.cancelOrder();
    }
    
}

class CancelledOrder implements IOrderState{
    OrderState orderState;
    CancelledOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("Order cancelled, Please create new order");
    }

    @Override
    public void prepareOrder() {
        System.out.println("Order cancelled, Please create new order");
    }

    @Override
    public void deliverOrder() {
        System.out.println("Order cancelled, Please create new order");
    }

    @Override
    public void cancelOrder() {
        System.out.println("Order already cancelled");
    }

}

class DeliveredOrder implements IOrderState{
    OrderState orderState;
    DeliveredOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("Order confirmed already, Deliverying now");
    }

    @Override
    public void prepareOrder() {
        System.out.println("Order already prepared, Deliverying now");
    }

    @Override
    public void deliverOrder() {
        System.out.println("Deliverying now");
    }

    @Override
    public void cancelOrder() {
        System.out.println("Order delivered already, cannot cancel");
    }

}

class OutForDeliveryOrder implements IOrderState{
    OrderState orderState;
    OutForDeliveryOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("Order confirmed already, out for delivery now");
    }

    @Override
    public void prepareOrder() {
        System.out.println("Order prepared already, out for delivery now");
    }

    @Override
    public void deliverOrder() {
        System.out.println("Order status changed to deliver now");
        this.orderState.setCurrentState("DELIVERED");
        this.orderState.setNextState(orderState.getDeliveredOrderState());
    }

    @Override
    public void cancelOrder() {
        System.out.println("Order prepared already, out for delivery now, cannot cancel");
    }

}
class PrepareOrder implements IOrderState{
    OrderState orderState;
    PrepareOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("Order confirmed already, preparing now");
    }

    @Override
    public void prepareOrder() {
        System.out.println("preparing order now");
        this.orderState.setCurrentState("OUT_FOR_DELIVERY");
        this.orderState.setNextState(orderState.getOutForDeliveryState());
    }

    @Override
    public void deliverOrder() {
        System.out.println("preparing order now, will be delivered in some time");
    }

    @Override
    public void cancelOrder() {
        System.out.println("preparing order now, cannot cancel");
    }

}

class ConfirmOrder implements IOrderState{
    OrderState orderState;
    ConfirmOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("confirming and checkout order now");
        this.orderState.setCurrentState("PREPARING");
        this.orderState.setNextState(orderState.getPrepareingOrderState());
    }

    @Override
    public void prepareOrder() {
        System.out.println("confirming order now, will prepare in some time");
    }

    @Override
    public void deliverOrder() {
        System.out.println("confirming order now, will deliver in some time");
    }

    @Override
    public void cancelOrder() {
        System.out.println("Cancelling the order now , processing refund");
        this.orderState.setCurrentState("CANCELLED");
        this.orderState.setNextState(orderState.getCancelledOrderState());
    }

}

class PlaceOrder implements IOrderState{
    OrderState orderState;
    PlaceOrder(OrderState orderState){
        this.orderState = orderState;
    }
    @Override
    public void confirmAndCheckoutOrder() {
        System.out.println("Order place, going to confirm order");
        this.orderState.setCurrentState("CONFIRMED");
        this.orderState.setNextState(orderState.getConfirmedOrderState());
    }

    @Override
    public void prepareOrder() {
        System.out.println("Order placed, will prepare after confirmation");
    }

    @Override
    public void deliverOrder() {
        System.out.println("Order placed, will deliver after confirmation");
    }

    @Override
    public void cancelOrder() {
        System.out.println("Cancelling the order now");
        this.orderState.setCurrentState("CANCELLED");
        this.orderState.setNextState(orderState.getCancelledOrderState());
    }

}





//Sequential Validation - chain of responsibility
abstract class OrderValidator{
    Order order;
    MenuTree root;
    OrderValidator(Order order, MenuTree root){
        this.order = order;
        this.root = root;
    }
    abstract boolean validate();
}

class ItemAvailability extends OrderValidator{
    OrderValidator nextOrderValidator;
    ItemAvailability(Order order, MenuTree root , OrderValidator nextOrderValidator) {
            super(order, root);   
            this.nextOrderValidator = nextOrderValidator;
    }
    
    @Override
    boolean validate() {
        System.out.println("Validating item availabity");
        List<MenuTree> menuTrees = this.order.getMenu();
        for(MenuTree m : menuTrees){
            int itemCount = root.getItemCountByName(m.getName());
            if(itemCount<=0)return false;
        }
        if(this.nextOrderValidator.validate())
            return true;
        return false;
    }

}

class DeliveryZone extends OrderValidator{
    OrderValidator nextOrderValidator;
    DeliveryZone(Order order, MenuTree root , OrderValidator nextOrderValidator) {
            super(order, root);   
            this.nextOrderValidator = nextOrderValidator;
    }
    
    @Override
    boolean validate() {
        System.out.println("Validating delivery zone");
        if(this.nextOrderValidator.validate())
            return true;
        return false;
    }

}

class PaymentPreCheck extends OrderValidator{
    OrderValidator nextOrderValidator;
    PaymentPreCheck(Order order, MenuTree root , OrderValidator nextOrderValidator) {
            super(order, root);   
            this.nextOrderValidator = nextOrderValidator;
    }
    
    @Override
    boolean validate() {
        System.out.println("Validating Payment pre check");
        IPayment payment = this.order.payment;
        payment.pay(this.order.cost);
        if(this.nextOrderValidator.validate())
            return true;
        return false;
    }

}

class FraudCheck extends OrderValidator{
    OrderValidator nextOrderValidator;
    FraudCheck(Order order, MenuTree root , OrderValidator nextOrderValidator) {
            super(order, root);   
            this.nextOrderValidator = null;
    }
    
    @Override
    boolean validate() {
        System.out.println("Validating Fraud Check");
        return true;
    }

}

interface IPayment{
    void pay(int amount);
}

class CreditCardPay implements IPayment{
    @Override
    public void pay(int amount) {
        System.out.println("Paid via Credit card::"+ amount);
    }
}

class UPIPay implements IPayment{
    @Override
    public void pay(int amount) {
        System.out.println("Paid via UPI::"+ amount);
    }   
}

class WalletPay implements IPayment{
    @Override
    public void pay(int amount) {
        System.out.println("Paid via Wallet::"+ amount);
    }
}

class CODPay implements IPayment{
    @Override
    public void pay(int amount) {
        System.out.println("Paid via COD::"+ amount);
    }   
}

class PaymenProcessorFactory{
    static IPayment getPaymentInstance(String payType){
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




//Order builder
class Order{
    String orderId;
    String customerName;
    String address;
    String deliverySlot;
    IPayment payment;
    String status;
    List<MenuTree> menu;
    int cost;
    List<IOrderStatusObserver> lstObserver;
    IDeliveryPartnerFeeStratergy feeStratergy;
    private Order(OrderBuilder orderBuilder){
        orderId = String.valueOf(OrderIdGenerator.getInstance().getId());
        this.customerName = orderBuilder.customerName;
        this.address = orderBuilder.address;
        this.deliverySlot = orderBuilder.deliverySlot;
        this.payment = orderBuilder.payment;
        this.menu = orderBuilder.menu;
        this.status = "PLACED";
        lstObserver = new ArrayList<>();
        this.feeStratergy = orderBuilder.stratergy;
        for(MenuTree menuItem: menu)
         this.cost +=  menuItem.getPrice();

        this.cost+= this.feeStratergy.getPartnerFee();
    }

    public void addObservers(IOrderStatusObserver observer){
        lstObserver.add(observer);
    }

    public void setStatus(String status) {
        this.status = status;
        for(IOrderStatusObserver observer : lstObserver)
            observer.notifyStatusChange();
    }

    public List<MenuTree> getMenu() {
        return menu;
}

    static class OrderBuilder{
        String customerName;
        String address;
        String deliverySlot;
        IPayment payment;
        List<MenuTree> menu;
        IDeliveryPartnerFeeStratergy stratergy;
        OrderBuilder(){

        }

        public OrderBuilder setStratergy(IDeliveryPartnerFeeStratergy stratergy) {
            this.stratergy = stratergy;
            return this;
        }

        public OrderBuilder setAddress(String address) {
            this.address = address;
            return this;
        }
        public OrderBuilder setCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }
        public OrderBuilder setDeliverySlot(String deliverySlot) {
            this.deliverySlot = deliverySlot;
            return this;
        }
        public OrderBuilder setMenu(List<MenuTree> menu) {
            this.menu = menu;
            return this;
        }
        public OrderBuilder setPayment(IPayment payment) {
            this.payment = payment;
            return this;
        }
        public Order build(){
            return new Order(this);
        }

    }
}


//Wrapping with decorator

interface IMenuDecorator extends MenuTree{
}

class ToppingDecorator implements IMenuDecorator{

    MenuTree menuTree;
    double price;
    ToppingDecorator(MenuTree m, double price){
        this.menuTree = m;
        this.price = price;
    }
    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public Double getPrice() {
        return this.price + menuTree.getPrice();
    }

    @Override
    public String printMenuItem() {
        return  menuTree.printMenuItem()+"extra topping (" +getPrice()+")";
    }
    @Override
    public void addMenu(MenuTree menuTree) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMenu'");
    }
    @Override
    public int getItemCountByName(String itemName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemCountByName'");
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
    
}

class CheeseDecorator implements IMenuDecorator{

    MenuTree menuTree;
    double price;
    CheeseDecorator(MenuTree m, double price){
        this.menuTree = m;
        this.price = price;
    }
    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public Double getPrice() {
        return this.price + menuTree.getPrice();
    }

    @Override
    public String printMenuItem() {
        return  menuTree.printMenuItem()+"extra cheese (" +getPrice()+")";
    }
    @Override
    public void addMenu(MenuTree menuTree) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMenu'");
    }
    @Override
    public int getItemCountByName(String itemName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemCountByName'");
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
    
}

class PackagingDecorator implements IMenuDecorator{

    MenuTree menuTree;
    double price;
    PackagingDecorator(MenuTree m, double price){
        this.menuTree = m;
        this.price = price;
    }
    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public Double getPrice() {
        return this.price + menuTree.getPrice();
    }

    @Override
    public String printMenuItem() {
        return  menuTree.printMenuItem()+"extra packaging (" +getPrice()+")";
    }
    @Override
    public void addMenu(MenuTree menuTree) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMenu'");
    }
    @Override
    public int getItemCountByName(String itemName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemCountByName'");
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }
    
}


interface MenuTree{
    int getItemCountByName(String itemName);
    int getItemCount();
    Double getPrice();
    String printMenuItem();
    void addMenu(MenuTree menuTree);
    String getName();
}

class MenuItem implements MenuTree{

    String name;
    double price;
    int itemCount;
    MenuItem(String name, double price, int itemCount){
        this.name = name;
        this.price = price;
        this.itemCount = itemCount;
    }
    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public Double getPrice() {
        return this.price;
    }
    @Override
    public String printMenuItem() {
        System.out.println(this.name+"("+this.price+")");
        return this.name;
    }
    @Override
    public void addMenu(MenuTree menuTree) {
        return;
    }
    @Override
    public int getItemCountByName(String itemName) {
        if(itemName.equals(this.name))
            return this.itemCount;
        return -1;
    }
    @Override
    public String getName() {
        return this.name;
    }


    
}

class Category implements MenuTree{

    List<MenuTree> lst;
    String name;
    Category(String name){
        lst= new ArrayList<>();
        this.name = name;
    }
    @Override
    public int getItemCountByName(String itemName) {
        for(MenuTree m : lst){
            int count = m.getItemCountByName(itemName);
            if(count>0){
                return count;
            }
        }
        return -1;
    }

    @Override
    public Double getPrice() {
        return null;
    }
    @Override
    public String printMenuItem() {
        System.out.println();
        System.out.println(this.name);
        
        for(MenuTree m : lst){
            m.printMenuItem();
        }
        return this.name;
    }

    public void addMenu(MenuTree menu){
        lst.add(menu);
    }

    public void removeMenu(MenuTree menu){
        lst.remove(menu);
    }

    @Override
    public int getItemCount() {
        return -1;
    }
    @Override
    public String getName() {
        return this.name;
    }
    
}

class OrderIdGenerator{
    static AtomicInteger count;
    private static OrderIdGenerator instance;
    private OrderIdGenerator(){
        count = new AtomicInteger(1000);
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
