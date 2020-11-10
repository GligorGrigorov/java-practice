import java.util.Arrays;

public class SocialDistanceMaximizer {
    public static int maxDistance(int[] seats){
        if (seats == null){
            return 0;
        }
        boolean haveZeros = false;
        boolean haveOnes = false;
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] == 1){
                haveOnes = true;
            }
            if (seats[i] == 0){
                haveZeros = true;
            }
        }
        if (!haveZeros){
            return 0;
        }
        if (!haveOnes){
            return 0;
        }
        int distance = 0;
        int maxDistance = 0;
        int i = 0;
        int leftDistance = 0;
        while(seats[i] != 1){
            ++i;
            ++leftDistance;
        }
        //System.out.println(i);
        int j = seats.length - 1;
        int rightDistance = 0;
        while (seats[j] != 1){
            --j;
            ++rightDistance;
        }
        //System.out.println(j);
        for (int k = i; k < j - 1; k++) {
            if (seats[k + 1] == 0){
                distance++;
            }else {
                if (maxDistance < distance){
                    maxDistance = distance;
                }
                distance = 0;
            }
        }
        if (maxDistance < distance){
            maxDistance = distance;
        }
        if (maxDistance % 2 == 0){
            maxDistance = maxDistance / 2;
        }
        else{
            maxDistance = maxDistance / 2 + 1;
        }
        if (maxDistance < leftDistance){
            maxDistance = leftDistance;
        }
        if (maxDistance < rightDistance){
            maxDistance = rightDistance;
        }
        return maxDistance;
    }
}
