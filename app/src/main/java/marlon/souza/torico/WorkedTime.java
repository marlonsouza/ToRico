package marlon.souza.torico;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by marlonsouza on 16/04/16.
 */
public class WorkedTime implements Serializable {

    private static final BigInteger MAX_HOUR_MINUTE = BigInteger.valueOf(60l);
    private static final Integer ONE_DIGIT = 10;

    private BigInteger hour, minute, second, absoluteSeconds;

    public WorkedTime() {
        this.hour = BigInteger.ZERO;
        this.minute = BigInteger.ZERO;
        this.second = BigInteger.ZERO;
        this.absoluteSeconds = BigInteger.ZERO;
    }

    private String applyMask(BigInteger value){
        if(value.intValue()<ONE_DIGIT){
            return "0"+value.intValue();
        }

        return ""+value.intValue();
    }

    public void moreSecond(){
        absoluteSeconds = absoluteSeconds.add(BigInteger.ONE);
        second = second.add(BigInteger.ONE);

        if(second.equals(MAX_HOUR_MINUTE)){
            second = BigInteger.ZERO;
            minute = minute.add(BigInteger.ONE);

            if(minute.equals(MAX_HOUR_MINUTE)){
                minute = BigInteger.ZERO;
                hour = hour.add(BigInteger.ONE);
            }
        }
    }

    public String generateTimer(){
        return new StringBuilder(applyMask(hour))
                .append(":")
                .append(applyMask(minute))
                .append(":")
                .append(applyMask(second))
                .toString();
    }

    public BigInteger getAbsoluteSeconds() {
        return absoluteSeconds;
    }
}
