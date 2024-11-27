import java.time.LocalDate;

public class DateFac {
    static private LocalDate standard = LocalDate.of(2000, 01, 01);
    static public int dateToInt(LocalDate LD){
        return (int)(LD.toEpochDay() - standard.toEpochDay());
    }
    static public LocalDate intToDate(int days){
        return standard.plusDays(days);
    }
    static public LocalDate intToDate(String days){
        try{
            return standard.plusDays(Integer.parseInt(days));
        }catch(Exception e){
            System.out.println("格式錯誤");
            return standard;
        }
    }
    static public int intToMonth(int days){
        return standard.plusDays(days).getMonthValue();
    }
    static public int intToDayOfMonth(int days){
        return standard.plusDays(days).getDayOfMonth();
    }
    static public int intToYear(int days){
        return standard.plusDays(days).getYear();
    }
    static public String intToYMString(int days){
        LocalDate tmp = standard.plusDays(days);
        return tmp.getYear() + "-" + tmp.getMonthValue();
    }
}
