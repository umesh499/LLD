package snakenladder;

import java.util.Map;

public class Board {
    int size;
    Map<Integer , SpecialEntity> map;
    Board(int s, Map<Integer , SpecialEntity> map){
        this.map = map;
    }

    boolean hasObstacle(int pos){
        if(map.get(pos) != null)
            return true;

        return false;
    }

    int nextPos(int currPos){
        SpecialEntity se = map.get(currPos);
        return se.end;
    }

    
}
