package sergutpal.dynu.com.nfc;

import android.app.PendingIntent;
import android.media.MediaPlayer;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
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
import android.widget.ToggleButton;
import android.provider.Settings.Secure;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import sergutpal.dynu.com.nfc.TelegramSGP;


public class MainActivity extends ActionBarActivity {
    NfcAdapter nfcAdapter;
    ToggleButton tglReadWrite;
    EditText txtTagContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(sergutpal.dynu.com.nfc.R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tglReadWrite = (ToggleButton)findViewById(R.id.tglReadWrite);
        txtTagContent = (EditText)findViewById(R.id.txtTagContent);

        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {

            if(tglReadWrite.isChecked()) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if(parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                }
                else {
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }
            }
            // No permitimos la escritura
/*
            else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText()+"");

                writeNdefMessage(tag, ndefMessage);
            }
*/
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        String s;

        try {
            NdefRecord[] ndefRecords = ndefMessage.getRecords();

            if(ndefRecords != null && ndefRecords.length>0){

                NdefRecord ndefRecord = ndefRecords[0];

                String tagContent = getTextFromNdefRecord(ndefRecord).toLowerCase();
                txtTagContent.setText(tagContent);

                s = tagContent.substring(0, 4);
                if (s.equals("sgp.")) {
                    // Se ha encontrado una etiqueta NFC de SGP -> Hay que enviar a Telegram el comando para su procesamiento.
                    // El formato que se envía es: Timestamp.Android_ID.comando
                    final String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID).toLowerCase();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
                    String dt = sdf.format(c.getTime());

                    tagContent = tagContent + '.' + androidId + '.' + dt;
                    TelegramSGP.sendTelegram(tagContent);
                    playOkMp3();
                    Toast.makeText(this, "Etiqueta SGP: #" + tagContent + "#", Toast.LENGTH_SHORT).show();
                    // finish();
                    // System.exit(0);
                }
                else {
                    Toast.makeText(this, '#' + s + "# No es una etiqueta SGP", Toast.LENGTH_SHORT).show();
                }
            }else
            {
                Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            txtTagContent.setText(e.getMessage());
            Log.e("formatTag", e.getMessage());
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


    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }


    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }


    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

}
