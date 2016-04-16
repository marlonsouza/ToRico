package marlon.souza.torico;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by marlonsouza on 16/04/16.
 */
public class MoneyCalculator {

    private BigDecimal salary;
    private BigDecimal hoursWorked;
    private BigDecimal extraPercent;

    private Locale br = new Locale( "pt", "BR" );
    private NumberFormat format = NumberFormat.getCurrencyInstance(br);

    private MoneyCalculator(BigDecimal salary, BigDecimal hoursWorked, BigDecimal extraPercent) {
        this.salary = salary;
        this.hoursWorked = hoursWorked;
        this.extraPercent = extraPercent;
    }

    public static MoneyCalculator of(BigDecimal salary, BigDecimal hoursWorked, BigDecimal extraPercent){
        Preconditions.checkNotNull(salary);
        Preconditions.checkNotNull(hoursWorked);
        Preconditions.checkNotNull(extraPercent);

        return new MoneyCalculator(salary, hoursWorked, extraPercent.divide(BigDecimal.valueOf(100l)));
    }

    public BigDecimal getValueHour(){
        return salary.divide(BigDecimal.valueOf(hoursWorked.doubleValue()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getExtraValue(){
        return getValueHour().multiply(extraPercent);
    }

    public String getValueHourFormatted(){
        return format.format(getValueHour().doubleValue());
    }


    public String calcule(BigInteger absoluteSeconds){
        BigDecimal myMoney = BigDecimal.ZERO;
        BigDecimal SESSENTA = new BigDecimal("60");

        BigDecimal absoluteHours = BigDecimal
                    .valueOf(absoluteSeconds.doubleValue())
                    .divide(SESSENTA, 2, RoundingMode.HALF_UP)
                    .divide(SESSENTA, 2, RoundingMode.HALF_UP);

        if(absoluteHours.doubleValue()>hoursWorked.doubleValue()){
            BigDecimal extra = absoluteHours.subtract(hoursWorked);
            myMoney = myMoney.add(extra.multiply(getExtraValue()));
            myMoney = myMoney.add(hoursWorked.multiply(getValueHour()));
        }else{
            myMoney = myMoney.add(absoluteHours.multiply(getValueHour()));
        }

        return format.format(myMoney.doubleValue());
    }


}
