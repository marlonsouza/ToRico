package marlon.souza.torico;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

/**
 * Created by marlonsouza on 15/04/16.
 */
public class CounterMoneyService extends Service implements Runnable {

    private CounterMoneyStatus state;
    private final Handler unbelievableHandler = new Handler();
    private Handler unbelievableHandlerUi = null;
    private final IBinder wonderfulConnection = new BinderVeryCrazy();

    private WorkedTime workedTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return wonderfulConnection;
    }

    public void start(){
        state = CounterMoneyStatus.START;

        unbelievableHandler.removeCallbacks(this);
        unbelievableHandler.post(this);
    }

    public void pause(){
        state = CounterMoneyStatus.PAUSE;
    }

    public void stop(){
        state = CounterMoneyStatus.STOP;
        this.workedTime = new WorkedTime();

        sendMessageToView(workedTime);
    }

    public void setUnbelievableHandlerUi(Handler unbelievableHandlerUi){
        this.unbelievableHandlerUi = unbelievableHandlerUi;
    }

    public CounterMoneyStatus getState() {
        return state;
    }

    public WorkedTime getWorkedTime() {
        return workedTime;
    }

    @Override
    public void onCreate() {
        this.workedTime = new WorkedTime();
    }

    @Override
    public void run() {
        if(state.equals(CounterMoneyStatus.START)){
            unbelievableHandler.removeCallbacks(this);
            unbelievableHandler.postDelayed(this, 1000);

            if(unbelievableHandlerUi!=null){
                this.workedTime.moreSecond();

                sendMessageToView(this.workedTime);
            }
        }
    }

    private void sendMessageToView(WorkedTime toSend) {
        Bundle bundleVeryCrazy = new Bundle();
        bundleVeryCrazy.putSerializable("MONEY_TIME", toSend);

        Message message = new Message();
        message.setData(bundleVeryCrazy);

        this.unbelievableHandlerUi.sendMessage(message);
    }

    public class BinderVeryCrazy extends Binder {

        public CounterMoneyService getCounterMoneyService(){
            return CounterMoneyService.this;
        }
    }
}
