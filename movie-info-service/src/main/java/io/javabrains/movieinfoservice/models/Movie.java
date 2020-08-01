package io.javabrains.movieinfoservice.models;

import lombok.Data;

@Data
public class Movie {
    private String movieId;
    private String name;
    private String overView;
    public Movie(String movieId, String name,String overView) {
        this.movieId = movieId;
        this.name = name;
        this.overView=overView;
    }

//    public String getMovieId() {
//        return movieId;
//    }
//
//    public void setMovieId(String movieId) {
//        this.movieId = movieId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
}
