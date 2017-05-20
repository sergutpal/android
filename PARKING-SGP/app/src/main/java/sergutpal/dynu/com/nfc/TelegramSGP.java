package sergutpal.dynu.com.nfc;

import android.util.Log;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;


public class TelegramSGP {
    private static final String TELEGRAM_BOT = "320854606:AAGb95Vb9rax_pF8WZbXDIMXnzJSD1RbBP4";
    private static final Integer TELEGRAM_CHATID = 89102745;

    public static void sendTelegram(String txt) {
        try {
            TelegramBot bot = TelegramBotAdapter.build(TELEGRAM_BOT);
            SendMessage request = new SendMessage(TELEGRAM_CHATID, txt)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true)
                    .replyToMessageId(1)
                    .replyMarkup(new ForceReply());
            bot.execute(request);
        }
        catch (Exception e) {
            Log.e("formatTag", e.getMessage());
            e.printStackTrace();
        }
    }

}
