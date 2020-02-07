package sergutpal.dynu.com.nfc;

import android.app.PendingIntent;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.provider.Settings.Secure;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import sergutpal.dynu.com.nfc.TelegramSGP;


public class MainActivity extends ActionBarActivity {
    Button btnOpenPkg;
    Button btnClose;
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(sergutpal.dynu.com.nfc.R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnOpenPkg = (Button)findViewById(R.id.btnOpenPkg);
        btnClose = (Button)findViewById(R.id.btnClose);
        txtMessage = (TextView)findViewById(R.id.txtMessage);
    }

    public void btnOpenPkgOnClick(View view) {
        sendParkingMessage();
    }

    public void btnCloseOnClick(View view) {
        closeApp();
    }

    public void btnAlarmOnClick(View view) {
        String msgTelegram;

        msgTelegram = "sgp.alarma";
        sendMessageTelegram(msgTelegram, "Alarma activada");
    }

    public void btnAlarmOffClick(View view) {
        String msgTelegram;

        msgTelegram = "sgp.alarmaoff";
        sendMessageTelegram(msgTelegram, "Alarma desactivada");
    }

    public void btnMusicClick(View view) {
        String msgTelegram;

        msgTelegram = "sgp.musica";
        sendMessageTelegram(msgTelegram, "Musica activada");
    }

    public void closeApp() {
        finish();
        System.exit(0);
    }

    private void sendMessageTelegram(String msgTelegram, String labelMsg) {
        try {
            final String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID).toLowerCase();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
            String dt = sdf.format(c.getTime());

            msgTelegram = msgTelegram + '.' + androidId + '.' + dt;

            TelegramSGP.sendTelegram(msgTelegram);
            // Toast.makeText(this, "Abriendo parking", Toast.LENGTH_SHORT).show();
            txtMessage.setText(labelMsg);
        } catch (Exception e) {
            txtMessage.setText(e.getMessage());
        }
    }

    private void sendParkingMessage() {
        String msgTelegram;

        try {
            msgTelegram = "sgp.parking";
            sendMessageTelegram(msgTelegram, "Abriendo parking...");
            playOkMp3();
            closeApp();
        } catch (Exception e) {
            txtMessage.setText(e.getMessage());
        }
    }

    private void playOkMp3() {
        MediaPlayer bell= MediaPlayer.create(MainActivity.this,R.raw.bell);
        bell.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(sergutpal.dynu.com.nfc.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == sergutpal.dynu.com.nfc.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
