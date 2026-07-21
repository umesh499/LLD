package designPatterns;

import java.util.ArrayList;
import java.util.List;

public class Prototype {
    public static void main(String[] args) {
        ExpensiveObj obj1 = new ExpensiveObj();//expensive object
        ExpensiveObj obj2 = obj1.clone();
        System.out.println(obj1);
        System.out.println(obj2);
    }
}

class ExpensiveObj{
    String name;
    List<String> enemies;
    public ExpensiveObj(){
        this.name = "xyz";//DB calls expensive
        enemies = new ArrayList<>();//DB calls
    }
    private ExpensiveObj(String name, List<String> enemyList){
        this.name = name;
        this.enemies = enemyList;
    }
    public ExpensiveObj clone(){
        return new ExpensiveObj(this.name, 
                                this.enemies);
    }
    @Override
    public String toString() {
        return this.name;
    }
}