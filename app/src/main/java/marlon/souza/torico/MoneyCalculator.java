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
        return getValueHour().multiply(extraPercent).add(getValueHour());
    }

    public String getValueHourFormatted(){
        return format.format(getValueHour().doubleValue());
    }


    public String calcule(BigInteger absoluteSeconds){
        BigDecimal myMoney = BigDecimal.ZERO;
        BigDecimal SESSENTA = new BigDecimal("60");

        BigDecimal valueSeconds = getExtraValue()
                    .divide(SESSENTA, 2, RoundingMode.HALF_UP)
                    .divide(SESSENTA, 2, RoundingMode.HALF_UP);

        myMoney = myMoney.add(valueSeconds.multiply(BigDecimal.valueOf(absoluteSeconds.longValue())));

        return format.format(myMoney.doubleValue());
    }


}
