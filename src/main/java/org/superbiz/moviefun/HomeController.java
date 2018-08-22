package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class HomeController {

    private MoviesBean movies;

    HomeController(MoviesBean movies)
    {
        this.movies = movies;
    }

    @GetMapping("/")
    String index() {
        return("index");
    }


    @GetMapping("/setup")
    String setup(Map<String,Object> model) {


        movies.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        movies.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        movies.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        movies.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        movies.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        movies.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        movies.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));


        model.put("movies", movies.getMovies());

        return "setup";
    }
}
