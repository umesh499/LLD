package designPatterns.structural;

public class State {
    public static void main(String[] args) {
        ATMMachine atmMachine = new ATMMachine(1000);
        atmMachine.insertCard();  
        atmMachine.enterPin(1234);
        atmMachine.checkBalance();
        atmMachine.withdrawCash(200);
        atmMachine.checkBalance();
        atmMachine.ejectCard();
    }
    
}

interface IATMMachineState{
    void insertCard();
    void ejectCard();
    void enterPin(int pin);
    void withdrawCash(int amount);
    void checkBalance();
}
//ATM States : NoCardState, NoCashState, HasCardState, AuthenticatedState

class NoCardState implements IATMMachineState{
    ATMMachine atmMachine;
    NoCardState(ATMMachine atmMachine){
        this.atmMachine = atmMachine;
    }
    @Override
    public void insertCard() {
        System.out.println("Insert card");
        atmMachine.setCurrentState(atmMachine.getHasCardState());
    }
    @Override
    public void ejectCard() {
        System.out.println("Insert card first.");
    }
    @Override
    public void enterPin(int pin) {
        System.out.println("Insert card first.");
    }
    @Override
    public void withdrawCash(int amount) {
        System.out.println("Insert card first.");
    }
    @Override
    public void checkBalance() {
        System.out.println("Insert card first.");
    }
    
}
class NoCashState implements IATMMachineState{
    ATMMachine atmMachine;
    NoCashState(ATMMachine atmMachine){
        this.atmMachine = atmMachine;
    }
    @Override
    public void insertCard() {
        System.out.println("Insert card.");
        atmMachine.setCurrentState(atmMachine.getHasCardState());
    }
    @Override
    public void ejectCard() {
        System.out.println("Insert card first.");
    }
    @Override
    public void enterPin(int pin) {
        if(atmMachine.validatePin(pin)){
            System.out.println("Valid Pin");
            atmMachine.setCurrentState(atmMachine.getAuthenticatedState());
        }
        System.out.println("Invalid Pin, renter pin");
    }
    @Override
    public void withdrawCash(int amount) {
        System.out.println("No cash , to withdraw");
    }
    @Override
    public void checkBalance() {
        System.out.println("Enter Pin");
    }
    
}

class HasCardState implements IATMMachineState{
    ATMMachine atmMachine;
    HasCardState(ATMMachine atmMachine){
        this.atmMachine = atmMachine;
    }
    @Override
    public void insertCard() {
        System.out.println("Card already Inserted");
    }
    @Override
    public void ejectCard() {
        System.out.println("Remove card");
        atmMachine.setCurrentState(atmMachine.getNoCardState());
    }
    @Override
    public void enterPin(int pin) {
        if(atmMachine.validatePin(pin)){
            System.out.println("Valid Pin");
            atmMachine.setCurrentState(atmMachine.getAuthenticatedState());
            return;
        }
        System.out.println("Invalid Pin, renter pin");
    }
    @Override
    public void withdrawCash(int amount) {
        System.out.println("Enter Pin, cannot withdraw");
    }
    @Override
    public void checkBalance() {
        System.out.println("Enter Pin, cannot check balance");
    }
   
}

class AuthenticatedState implements IATMMachineState{
    ATMMachine atmMachine;
    AuthenticatedState(ATMMachine atmMachine){
        this.atmMachine = atmMachine;
    }
    @Override
    public void insertCard() {
        System.out.println("Card already Inserted");
    }
    @Override
    public void ejectCard() {
        System.out.println("Remove card");
        atmMachine.setCurrentState(atmMachine.getNoCardState());
    }
    @Override
    public void enterPin(int pin) {
        System.out.println("Already authenticated");
    }
    @Override
    public void withdrawCash(int amount) {
        if(atmMachine.getTotalCash()>=amount){
            System.out.println("Withdraw cash");
            atmMachine.setTotalCash(atmMachine.getTotalCash() - amount);
            System.out.println("Withdrawn cash"+ amount);
            if(atmMachine.getTotalCash()==0){
                System.out.println("ATM has no cash");
                atmMachine.setCurrentState(atmMachine.getNoCashState());
            }
        }else{
            System.out.println("Atm has insufficient cash");
            return;
        }
    }
    @Override
    public void checkBalance() {
        System.out.println("Check balance");
    }
   
}

class ATMMachine implements IATMMachineState{
    IATMMachineState currentState;
    IATMMachineState noCardState;
    IATMMachineState hasCardState;
    IATMMachineState authenticatedState;
    IATMMachineState noCashState;
    int totalCash;
    ATMMachine(int totalCash){
        this.totalCash = totalCash;
        this.noCardState =new NoCardState(this);
        
        this.hasCardState = new HasCardState(this);
        this.authenticatedState = new AuthenticatedState(this);
        this.noCashState = new NoCashState(this);
        if(totalCash>0)
            this.currentState = this.noCardState;
        else
            this.currentState = this.noCashState;
    }
    public int getTotalCash() {
        return totalCash;
    }
    public void setTotalCash(int cash) {
        this.totalCash = cash;
    }
    void setCurrentState(IATMMachineState newState){
        this.currentState = newState;
    }

    public IATMMachineState getHasCardState() {
        return hasCardState;
    }
    public IATMMachineState getNoCardState() {
        return noCardState;
    }
    public IATMMachineState getNoCashState() {
        return noCashState;
    }
    public IATMMachineState getAuthenticatedState() {
        return authenticatedState;
    }
    @Override
    public void insertCard() {
       currentState.insertCard();
    }
    @Override
    public void ejectCard() {
        currentState.ejectCard();
    }
    @Override
    public void enterPin(int pin) {
        currentState.enterPin(pin);
    }
    @Override
    public void withdrawCash(int amount) {
        currentState.withdrawCash(amount);
    }

    public boolean validatePin(int pin){
        if(pin==1234)return true;
        return false;
    }
    @Override
    public void checkBalance() {
        currentState.checkBalance();
    }

   
}
