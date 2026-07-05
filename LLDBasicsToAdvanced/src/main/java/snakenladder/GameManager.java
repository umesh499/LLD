package snakenladder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameManager {
    public static void main(String[] args) {
        Snake s1 = new Snake(10, 2);
        Snake s2 = new Snake(15, 8);
        Ladder l1 = new Ladder(5, 10);
        Ladder l2 = new Ladder(7, 16);
        Map<Integer , SpecialEntity> map = new HashMap<>();
        map.put(10, s1);
        map.put(15, s2);
        map.put(5, l1);
        map.put(7, l2);
        Board b = new Board(16, map);
        Player p1 = new Player("P1");
        Player p2 = new Player("P2");
        Queue<Player> queue = new LinkedList<>();
        queue.add(p1);
        queue.add(p2);
        start(16, b, queue);
    }

    static void start(int size, Board b, Queue<Player> queue){
        boolean isWinnerDeclared = false;
        while(!isWinnerDeclared){
            Player p = queue.poll();
            int diceRoll = Dice.roll();
            int currPos = p.pos;
            int nextPos = currPos + diceRoll;
            if(b.hasObstacle(nextPos)){
                nextPos = b.nextPos(nextPos);
            }
            if(nextPos>size){
                queue.add(p);
                continue;
            }
            System.out.println(" after new dice roll: "+ diceRoll +" :new position of "+p.name +" is :"+nextPos);
            if(nextPos == size){
                System.out.println(p.name +" is WINNER");
                isWinnerDeclared = true;
            }
            
            p.pos = nextPos;
            queue.add(p);
        }
    }
}
