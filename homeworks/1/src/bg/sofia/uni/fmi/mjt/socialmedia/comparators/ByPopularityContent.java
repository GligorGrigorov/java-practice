package bg.sofia.uni.fmi.mjt.socialmedia.comparators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.Comparator;

public class ByPopularityContent implements Comparator<Content> {
    @Override
    public int compare(Content o1, Content o2) {
        return (o1.getNumberOfLikes() + o1.getNumberOfComments()) - (o2.getNumberOfComments() + o2.getNumberOfLikes());
    }
}
