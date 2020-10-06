package msmartds.in;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yuganshu on 08/03/2017.
 */

public class Service {


    public static boolean isValidEmail(String enteredEmail){

        Pattern p =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(enteredEmail);
        if(m.find()){
            return true;
        } else {
            return false;
        }
    }
}
