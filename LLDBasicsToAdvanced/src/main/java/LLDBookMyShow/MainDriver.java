package LLDBookMyShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainDriver {
    public static void main(String[] args) {
        initialize();
    }
    private static void initialize(){
        Movie m1 = new Movie(1,"Avengers", 2);
        Movie m2 = new Movie(2,"Dil Chahta Hai", 3);
        Movie m3 = new Movie(3,"Avengers2", 3);
        Movie m4 = new Movie(4,"Dil Chahta Hai2", 4);
        List<Movie> lst1 = new ArrayList<>();
        lst1.add(m1);
        lst1.add(m2);
        List<Movie> lst2 = new ArrayList<>();
        lst2.add(m3);
        lst2.add(m4);
        Map<City, List<Movie>> map = new HashMap<>();
        map.put(City.HYDERABAD, lst1);
        map.put(City.BANGALORE, lst2);
        List<Movie> lst = new ArrayList<>();
        lst.add(m1);
        lst.add(m2);
        lst.add(m3);
        lst.add(m4);
        MovieController movieController = new MovieController(map, lst);
    }
}
