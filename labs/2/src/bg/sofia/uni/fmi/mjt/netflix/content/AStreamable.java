package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public abstract class AStreamable implements Streamable {
    private PgRating rating;
    private Genre genre;
    private String name;
    protected int duration;
    public AStreamable(String name, Genre genre, PgRating rating, int duration){
        this.rating = rating;
        this.genre = genre;
        this.name = name;
        this.duration = duration;
    }
    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public int getDuration(){
        return duration;
    }

    @Override
    public PgRating getRating() {
        return rating;
    }
}
