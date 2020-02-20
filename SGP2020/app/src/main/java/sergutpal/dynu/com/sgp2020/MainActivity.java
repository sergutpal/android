package sergutpal.dynu.com.sgp2020;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    Button btnParking;
    Button btnExit;
    TextView txtMessage;

    private void sendMessageTelegram(String msgTelegram, String labelMsg) {
        int response;
        TelegramSGP telegram;
        try {
            final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toLowerCase();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
            String dt = sdf.format(c.getTime());

            msgTelegram = msgTelegram + '.' + androidId + '.' + dt;

            TelegramSGP.sendTelegram(msgTelegram);
            telegram = new TelegramSGP();
            telegram.execute(msgTelegram);
            txtMessage.setText("Mensaje: #" + msgTelegram + "# enviado");
        } catch (Exception e) {
            txtMessage.setText("Error: " + e.toString());
            Log.e("sgp", "Error enviando mensaje a Telegram");
        }
    }

    public void closeApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnParking = (Button)findViewById(R.id.btnParking);
        btnExit = (Button)findViewById(R.id.btnExit);
        txtMessage = (TextView)findViewById(R.id.txtMessage);
    }

    public void btnOpenPkgOnClick(View view) {
        String msgTelegram;

        msgTelegram = "sgp.abreParking";
        sendMessageTelegram(msgTelegram, msgTelegram);
    }

    public void btnCloseOnClick(View view) {
        closeApp();
    }

}
