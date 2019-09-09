package esp.etc.excel.utils;

public class StringUtils {

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
    
    public static String nvl(String str) {
        if ( str == null ) {
            str = "";
        }
        return str;
    }
}
