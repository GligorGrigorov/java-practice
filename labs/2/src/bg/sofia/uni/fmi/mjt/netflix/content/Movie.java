package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public class Movie extends AStreamable {
    public Movie(String name, Genre genre, PgRating rating, int duration) {
        super(name, genre, rating, duration);
    }

}
