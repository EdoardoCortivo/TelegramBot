import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public MyTelegramBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String[] messaggio = message_text.split(" ");
            String Risposta = "";
            switch (messaggio[0])
            {
                case "/start":
                    Risposta = Start();
                    break;
                case "/search":
                    Risposta = Search(messaggio[1],messaggio[2]);
                    break;
                case "/save":
                    Risposta = Save();
                    break;
                case "/history":
                    Risposta = History(messaggio[1],messaggio[2]);
                    break;
                case "/all":
                    Risposta = All();
                    break;
                case "/notify":
                    Risposta = Notify();
                    break;
                case "/help":
                    Risposta = Help();
                    break;
                default:
                    Risposta = Default();
                    break;
            }


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
    }

    public String Start()
    {
        String Risposta = "Ciao, sono DiscoVibesBot! Trova e risparmia sui dischi in vinile e CD. Cosa stai cercando oggi?";
        return Risposta;
    }
    public String Search()
    {
        String Risposta = "Ecco cosa ho trovato:\n" +
                "- Titolo\n" +
                "- Artista\n" +
                "- Formato\n" +
                "- Prezzo";
        return Risposta;
    }
    public String Save()
    {
        String Risposta = "Album aggiunto correttamente al database \uD83D\uDE01";
        return Risposta;
    }
    public String History()
    {
        String Risposta = "L'album 'Titolo' ha avuto il minimo in 'data' a 'prezzo' e il 'massimo' in 'data' a 'prezzo'";
        return Risposta;
    }
    public String All()
    {
        String  Risposta = "Ecco tutti gli Album che hai salvato:\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista\n" +
                "- Titolo Artista";
        return Risposta;
    }
    public String Notify()
    {
        String Risposta = "OK! Disattivo le notifiche";
        return Risposta;
    }
    public String Help()
    {
        String Risposta = "Ecco la lista dei comandi:\n" +
                "/start: Avvia la conversazione con il bot e fornisce una breve introduzione su come utilizzare le funzionalità principali.\n" +
                "/search <titolo> <artista>: Permette all'utente di cercare un album in base al titolo e all'artista specificato. Il bot restituirà le opzioni di acquisto disponibili (CD o vinile) e i relativi prezzi.\n" +
                "/save: Consente all'utente di salvare l'album trovato nel database per monitorarne i prezzi. Una volta salvato, il bot inizierà a tracciare i cambiamenti di prezzo e invierà notifiche in caso di variazioni.\n" +
                "/history <titolo> <artista>: Visualizza lo storico dei prezzi per l'album specificato, mostrando il prezzo minimo e massimo registrato, insieme alle date corrispondenti.\n" +
                "/all: Mostra una lista di tutti gli album che l'utente ha salvato nel database, consentendo di visualizzare rapidamente gli album monitorati e i loro prezzi attuali.\n" +
                "/notify: Abilita o disabilita le notifiche riguardo alle variazioni di prezzo per gli album salvati.\n" +
                "/help: Fornisce informazioni dettagliate sui comandi disponibili e su come utilizzarli correttamente.";
        return Risposta;
    }
    public String Default()
    {
        String Risposta = "Questo si che è imbarazzante ... \nNon capisco quello che dici.\uD83D\uDE05";
        return Risposta;
    }
}