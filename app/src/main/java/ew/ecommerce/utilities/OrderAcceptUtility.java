package ew.ecommerce.utilities;

public class OrderAcceptUtility {
    public static boolean isEmailValid(String email){
        return email.contains("@");
    }
    public static boolean isPhoneValid(String number){
        return !number.equals("");
    }
}
