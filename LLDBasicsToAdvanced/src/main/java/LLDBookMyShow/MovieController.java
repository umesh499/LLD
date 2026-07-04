package LLDBookMyShow;

import java.util.List;
import java.util.Map;

public class MovieController {
    Map<City, List<Movie>> cityMovies;
    List<Movie> allMovies;
    public MovieController(Map<City, List<Movie>> cityMovies, List<Movie> allMovies){
        this.cityMovies = cityMovies;
        this.allMovies = allMovies;
    }

    public Map<City, List<Movie>> getCityMovies() {
        return cityMovies;
    }

    public void setCityMovies(Map<City, List<Movie>> cityMovies) {
        this.cityMovies = cityMovies;
    }

    public List<Movie> getAllMovies() {
        return allMovies;
    }

    public void setAllMovies(List<Movie> allMovies) {
        this.allMovies = allMovies;
    }
}
