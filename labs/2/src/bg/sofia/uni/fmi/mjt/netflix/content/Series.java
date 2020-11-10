package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public class Series extends AStreamable {

    public Series(String name, Genre genre, PgRating rating, Episode[] episodes) {

        super(name, genre, rating, 0);
        for (Episode e: episodes) {
            duration+=e.duration();
        }
    }

}
