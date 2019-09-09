package esp.etc.excel.utils;

import java.text.Format;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.FastDateFormat;

public class DateUtils {

    /**
     * Get Date Today : yyyyMMdd
     * @param exp 년, 월, 일 사이의 구분자 (ex. '.', '-', '_')
     * @return yyyyMMdd, yyyy-MM-dd, yyyy.MM.dd, yyyy_MM_dd ... 
     * @throws Exception
     */
    public static String getDateyyyyMMdd(final String exp) throws Exception {
        Date date = new Date();
        Format fdf = FastDateFormat.getInstance( "yyyyMMdd", Locale.getDefault());
        String yyyyMMdd = fdf.format(date);
        if ( exp != null && exp.length() != 0 ) {
            yyyyMMdd = yyyyMMdd.substring(0, 4) + exp + yyyyMMdd.substring(4, 6) + exp + yyyyMMdd.substring(6, 8);
        }
        return yyyyMMdd;
    }

    public static void main(String[] args) throws Exception {
//        String date = getDateyyyyMMdd(null);
        System.out.println(getDateyyyyMMdd(null));
        System.out.println(getDateyyyyMMdd("-"));
        System.out.println(getDateyyyyMMdd("."));
        System.out.println(getDateyyyyMMdd("_"));
    }
}
