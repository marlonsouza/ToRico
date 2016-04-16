package marlon.souza.torico;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;


public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private MainActivity instance = this;
    private Intent intent;
    private Handler unbelievableHandler;
    private CounterMoneyService counterMoneyService;

    private TextView time, money, valueHour;
    private Button startButton, pauseButton, stopButton;

    private ServiceConnection connection = this;

    public void play(View view){
        if(isConfigsOk()){
            this.counterMoneyService.start();
        }else{
            Toast.makeText(this, "Existem configurações não informadas!", Toast.LENGTH_LONG).show();
        }

        changeStateButtons();
    }

    public void pause(View view){
        counterMoneyService.pause();
        changeStateButtons();
    }

    public void stop(View view){
        counterMoneyService.stop();

        changeStateButtons();
    }

    private void changeStateButtons(){
        Optional<CounterMoneyService> counterMoneyServiceOptional = Optional.fromNullable(counterMoneyService);

        startButton.setEnabled(
                counterMoneyServiceOptional
                        .transform(c -> {
                            if (c.getState() == null) {
                                return Boolean.TRUE;
                            }

                            return !CounterMoneyStatus.START.equals(c.getState());
                        })
                        .or(Boolean.TRUE)
        );
        pauseButton.setEnabled(
                counterMoneyServiceOptional
                        .transform(c -> {
                            if (c.getState() == null) {
                                return Boolean.FALSE;
                            }

                            return !CounterMoneyStatus.PAUSE.equals(c.getState());
                        })
                        .or(Boolean.FALSE)
        );
        stopButton.setEnabled(
                counterMoneyServiceOptional
                        .transform(c -> {

                            if (c.getState() == null) {
                                return Boolean.FALSE;
                            }

                            return !CounterMoneyStatus.STOP.equals(c.getState());
                        })
                        .or(Boolean.FALSE)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(instance, CounterMoneyService.class);

        time = (TextView) findViewById(R.id.time);
        money = (TextView) findViewById(R.id.i_am_rich);
        valueHour = (TextView) findViewById(R.id.value_hour);

        startButton = (Button) findViewById(R.id.iniciar);
        pauseButton = (Button) findViewById(R.id.pausar);
        stopButton = (Button) findViewById(R.id.parar);

        unbelievableHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                WorkedTime workedTime = (WorkedTime) msg.getData().getSerializable("MONEY_TIME");
                refreshValues(workedTime);
                changeStateButtons();
            }
        };

        startService(intent);

        changeStateButtons();
    }


    private void refreshValues(WorkedTime workedTime){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(instance);

        String salary = getPreference(preferences, KeyPreference.SALARY);
        String hoursWorked = getPreference(preferences, KeyPreference.HOURS_WORKED);
        String extraPercent = getPreference(preferences, KeyPreference.EXTRA_PERCENT);

        MoneyCalculator moneyCalculator =
                MoneyCalculator.of(
                    stringToBigDecimal(salary),
                    stringToBigDecimal(hoursWorked),
                    stringToBigDecimal(extraPercent));

        money.setText(moneyCalculator.calcule(workedTime.getAbsoluteSeconds()));
        time.setText(workedTime.generateTimer());
        valueHour.setText(moneyCalculator.getValueHourFormatted());

    }

    private BigDecimal stringToBigDecimal(String value){
        return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
    }


    private String getPreference(SharedPreferences sharedPreferences, KeyPreference key){
        return sharedPreferences.getString(key.value, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menu.clear();
        menuInflater.inflate(R.menu.main, menu);

        menu.findItem(R.id.go_settings).setOnMenuItemClickListener(i -> {
            startActivity(new Intent(instance, UserSettingsActivity.class));
            return true;
        });

        return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        CounterMoneyService.BinderVeryCrazy binderVeryCrazy = (CounterMoneyService.BinderVeryCrazy) service;
        counterMoneyService = binderVeryCrazy.getCounterMoneyService();
        counterMoneyService.setUnbelievableHandlerUi(unbelievableHandler);

        WorkedTime refreshWorkedTime = counterMoneyService.getWorkedTime();
        if(refreshWorkedTime!=null){
            refreshValues(refreshWorkedTime);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        counterMoneyService = null;
    }

    private enum  KeyPreference{
        SALARY("grossSalary"),
        HOURS_WORKED("hoursWorked"),
        EXTRA_PERCENT("extraPercent");

        KeyPreference(String value){
            this.value = value;
        }

        private String value;

        public String getValue() {
            return value;
        }
    }

    private Boolean isConfigsOk(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return
            !Stream.of(
                ImmutableList.of(KeyPreference.SALARY, KeyPreference.HOURS_WORKED, KeyPreference.EXTRA_PERCENT))
                    .filter(k -> getPreference(sharedPreferences, k).isEmpty())
                    .findFirst()
                    .isPresent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(intent, connection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
    }
}
