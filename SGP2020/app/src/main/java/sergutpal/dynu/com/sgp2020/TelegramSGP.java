package sergutpal.dynu.com.sgp2020;

import android.os.AsyncTask;
import android.util.Log;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

public class TelegramSGP extends AsyncTask<String, Integer, String> {
    private static final String TELEGRAM_BOT = "320854606:AAGb95Vb9rax_pF8WZbXDIMXnzJSD1RbBP4";
    private static final Integer TELEGRAM_CHATID = 89102745;
    private static String msgToSend="";
    private static boolean msgSent=false;

    @Override
    protected void onPreExecute() {
        //Setup precondition to execute some task
    }

    @Override
    protected String doInBackground(String... params) {
        if (msgToSend !="") {
            try {
                TelegramBot bot = new TelegramBot(TELEGRAM_BOT);
                SendMessage request = new SendMessage(TELEGRAM_CHATID, msgToSend);
                bot.execute(request);
                msgSent = true;
                return "ok";
            } catch (Exception e) {
                Log.e("sgp", e.toString());
                e.printStackTrace();
                msgSent = true;
                return "KO";
            }
        }
        return "";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //Update the progress of current task
    }

    @Override
    protected void onPostExecute(String result) {
        //Show the result obtained from doInBackground
        super.onPostExecute(result);
    }

    public static int sendTelegram(String txt) {
        msgToSend = txt;
        msgSent = false;
        return 0;
    }

    public static boolean isMsgSent() {
        return msgSent;
    }
}
