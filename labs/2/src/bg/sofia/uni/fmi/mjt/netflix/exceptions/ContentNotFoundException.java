package bg.sofia.uni.fmi.mjt.netflix.exceptions;

public class ContentNotFoundException extends RuntimeException{
    public ContentNotFoundException(){
        super("Content not found ex.");
    }
}
