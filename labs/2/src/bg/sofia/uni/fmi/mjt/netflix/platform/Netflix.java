package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;

import java.time.LocalDate;
import java.time.Period;

public class Netflix implements StreamingService {
    Account[] accounts;
    Streamable[] streamableContent;
    int totalWatchTime;
    int[] streamableViews;
    public Netflix(Account[] accounts, Streamable[] streamableContent){
        this.accounts = accounts;
        this.streamableContent = streamableContent;
        totalWatchTime = 0;
        streamableViews = new int[streamableContent.length];
    }
    @Override
    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        boolean isRegistered = false;
        int uIndex = 0;
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i].equals(user)){
                isRegistered = true;
                uIndex = i;
                break;
            }
        }
        if(!isRegistered){
            throw new UserNotFoundException();
        }
        boolean isVideoFound = false;
        PgRating rating = null;
        int sIndex = 0;
        for (int i = 0; i < streamableContent.length; i++) {
            if (videoContentName.compareTo(streamableContent[i].getTitle()) == 0){
                isVideoFound = true;
                rating = streamableContent[i].getRating();
                sIndex = i;
                break;
            }
        }
        if(!isVideoFound){
            throw new ContentNotFoundException();
        }
        boolean isVideoAvailable = false;
        if (rating == PgRating.G){
            isVideoAvailable = true;
        }else if(rating == PgRating.PG13){
            Period period = Period.between(user.birthdayDate().toLocalDate(),LocalDate.now());
            if(period.getYears() > 13) {
                isVideoAvailable = true;
            }
        }else {
            Period period = Period.between(user.birthdayDate().toLocalDate(),LocalDate.now());
            if(period.getYears() >= 18) {
                isVideoAvailable = true;
            }
        }
        if (!isVideoAvailable){
            throw new ContentUnavailableException();
        }
        streamableViews[sIndex]++;
        totalWatchTime += streamableContent[sIndex].getDuration();

    }

    @Override
    public Streamable findByName(String videoContentName) {
        for (Streamable s:
             streamableContent) {
            if(s.getTitle().compareTo(videoContentName) == 0){
                return s;
            }
        }
        return null;
    }

    @Override
    public Streamable mostViewed() {
        int maxViews = 0;
        int index = -1;
        for (int i = 0; i < streamableContent.length; i++) {
            if(streamableViews[i] > maxViews){
                maxViews = streamableViews[i];
                index = i;
            }
        }
        if(index != -1){
            return streamableContent[index];
        }
        return null;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        return totalWatchTime;
    }
}
