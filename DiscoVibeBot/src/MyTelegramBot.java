import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import static java.lang.Math.toIntExact;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {
    Boolean Notiiche = true;

    private final TelegramClient telegramClient;

    public MyTelegramBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String[] messaggio = message_text.split(" ");
            String[] Album = message_text.replace(messaggio[0], "").split(",");
            switch (messaggio[0]) {
                case "/start":
                    Start(chat_id);
                    break;
                case "/search":
                    Search(Album[0].trim(), Album[1].trim(), chat_id);
                    break;
                case "/history":
                    History(Album[0].trim(), chat_id);
                    break;
                case "/all":
                    All(chat_id);
                    break;
                case "/notify":
                    Notify(chat_id);
                    break;
                case "/help":
                    Help(chat_id);
                    break;
                default:
                    Default(chat_id);
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (call_data.equals("update_msg_text")) {
                String answer = "OK Attiveremo le notifiche per questo prodotto";
                SendMessage new_message = SendMessage.builder()
                        .chatId(chat_id)
                        .text(answer)
                        .build();
                try {
                    telegramClient.execute(new_message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Start(long chat_id) {
        String Risposta = "Ciao, sono DiscoVibesBot! Trova e risparmia sui dischi in vinile e CD. Cosa stai cercando oggi?";
        SendMsg(Risposta, chat_id);
    }

    public void Search(String titolo, String artista, long chat_id) {
        List<Album> albums = new java.util.ArrayList<>(List.of());
        albums.addAll(ScraperMondadori.ScraperM(titolo, artista));
        albums.addAll(ScraperFeltrinelli.ScraperF(titolo, artista));
        for (Album album : albums) {
            String Risposta = album.toString();
            Sendphoto(album.getImmagine(), chat_id);
            SendSearchResult(Risposta, chat_id);
        }
    }

    public void Save(long chat_id) {
        String Risposta = "Album aggiunto correttamente al database \uD83D\uDE01";
    }

    public void History(String titolo, long chat_id) {
        String Risposta = "L'album " + titolo + " ha avuto il minimo in 'data' a 'prezzo' e il 'massimo' in 'data' a 'prezzo'";
    }

    public void All(long chat_id) {
        String Risposta = "Ecco tutti gli Album che hai salvato:\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista";
        SendMsg(Risposta, chat_id);
    }

    public void Notify(long chat_id) {
        String Risposta = "OK! Disattivo le notifiche";
    }

    public void Help(long chat_id) {
        String Risposta = "Ecco la lista dei comandi:\n" +
                "/start: Avvia la conversazione con il bot e fornisce una breve introduzione su come utilizzare le funzionalità principali.\n" +
                "/search <titolo>, <artista>: Permette all'utente di cercare un album in base al titolo e all'artista specificato. Il bot restituirà le opzioni di acquisto disponibili (CD o vinile) e i relativi prezzi.\n" +
                "/save: Consente all'utente di salvare l'album trovato nel database per monitorarne i prezzi. Una volta salvato, il bot inizierà a tracciare i cambiamenti di prezzo e invierà notifiche in caso di variazioni.\n" +
                "/history <titolo>, <artista>: Visualizza lo storico dei prezzi per l'album specificato, mostrando il prezzo minimo e massimo registrato, insieme alle date corrispondenti.\n" +
                "/all: Mostra una lista di tutti gli album che l'utente ha salvato nel database, consentendo di visualizzare rapidamente gli album monitorati e i loro prezzi attuali.\n" +
                "/notify: Abilita o disabilita le notifiche riguardo alle variazioni di prezzo per gli album salvati.\n" +
                "/help: Fornisce informazioni dettagliate sui comandi disponibili e su come utilizzarli correttamente.";
        SendMsg(Risposta, chat_id);
    }

    public void Default(long chat_id) {
        String Risposta = "Questo si che è imbarazzante ... \nNon capisco quello che dici.\uD83D\uDE05";
        SendMsg(Risposta, chat_id);
    }

    public void SendMsg(String Risposta, long chat_id) {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(Risposta)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void SendSearchResult(String Risposta, long chat_id) {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(Risposta)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Voglio seguire questo!")
                                        .callbackData("update_msg_text")
                                        .build()
                                )
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void Sendphoto(String image, long chat_id) {
        SendPhoto msg = SendPhoto.builder().chatId(chat_id).photo(new InputFile(image)).caption("").build();
        try {
            telegramClient.execute(msg); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}