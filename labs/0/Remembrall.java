import java.util.Arrays;

public class Remembrall {
    public static boolean isPhoneNumberForgettable(String phoneNumber){
        if (phoneNumber == null){
            return false;
        }
        if (phoneNumber.isEmpty()){
            return false;
        }
        phoneNumber = phoneNumber.replace("-","");
        phoneNumber = phoneNumber.replace(" ","");
        //System.out.println(phoneNumber);
        char[] phoneNum = phoneNumber.toCharArray();
        Arrays.sort(phoneNum);
        //System.out.println(phoneNum);
        if(phoneNum[0] < '0' || phoneNum[0] > '9' || phoneNum[phoneNum.length - 1] < '0' || phoneNum[phoneNum.length - 1] > '9'){
            return true;
        }
        boolean flag = true;
        for (int i = 0; i < phoneNum.length - 1; i++) {
            if (phoneNum[i] == phoneNum[i + 1]){
                flag = false;
                break;
            }
        }
        return flag;
    }
}
