import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.awt.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import static java.lang.Math.toIntExact;

public class MyTelegramBot implements LongPollingSingleThreadUpdateConsumer {

    Map<String, String> imageLinks = new HashMap<>();
    Boolean Notiiche = true;
    static UtenteDB Udb = new UtenteDB("127.0.0.1", "3306", "root", "");
    static AlbumDB Adb = new AlbumDB("127.0.0.1", "3306", "root", "");
    static salvaDB Sdb = new salvaDB("127.0.0.1", "3306", "root", "");

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
                    if (Album.length == 0 || (Album.length == 1 && Album[0].trim().isEmpty())) {
                        SendMsg("Devi inserire il titolo e l'autore", chat_id);
                    }
                    try {
                        Search(Album[0].trim(), Album[1].trim(), chat_id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/history":
                    History(chat_id);
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
            String callbackData = update.getCallbackQuery().getData(); // Ottieni i dati della callback (ad esempio: "update_msg_text^<json_data>")
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (callbackData.startsWith("u")) {
                Album album = estraiAlbum(String.valueOf(update.getCallbackQuery().getMessage()), callbackData.substring(1));
                String imageId = callbackData.substring(1);
                album.setImmagine(imageLinks.get(imageId));
                imageLinks.remove(imageId);
                Save(chat_id, album);
            }
            else if (callbackData.startsWith("h")) {
                String nomi = String.valueOf(update.getCallbackQuery().getMessage());
                Pattern pattern = Pattern.compile("text=(.*?)[\\n]+(.*?)[\\n]+(.*?)[\\n]+(.*?), entities");
                Matcher matcher = pattern.matcher(nomi);
                if (matcher.find()) {
                    String author = matcher.group(1).trim();  // Primo gruppo: autore
                    String songTitle = matcher.group(2).trim();
                    String format = matcher.group(3).trim();
                    String seller = matcher.group(4).trim();
                    String[] risposta = Adb.selectALL("Albums", "nome_artista", "nome_album", "formato", "venditore", author, songTitle, format, seller).split("____");
                    System.out.println(songTitle);
                    if(risposta.length > 1)
                        Sendphoto(risposta[1], chat_id);
                    SendMsg(risposta[0], chat_id);
                }
            } else {
                System.out.println("Tipo di callback sconosciuto.");
            }
        }
    }

    public void Start(long chat_id) {
        String Risposta = "Ciao, sono DiscoVibesBot! Trova e risparmia sui dischi in vinile e CD. Cosa stai cercando oggi?";
        Udb.insertUser(chat_id);
        SendMsg(Risposta, chat_id);
    }

    public void Search(String titolo, String artista, long chat_id) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);  // Due thread: uno per ciascun scraper
        List<Future<List<Album>>> futures = new ArrayList<>();

        futures.add(executorService.submit(() -> ScraperMondadori.ScraperM(titolo, artista)));
        futures.add(executorService.submit(() -> ScraperFeltrinelli.ScraperF(titolo, artista)));
        List<Album> albums = new ArrayList<>();
        for (Future<List<Album>> future : futures) {
            try {
                albums.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        if (!albums.isEmpty()) {
            for (Album album : albums) {
                Sendphoto(album.getImmagine(), chat_id);
                SendSearchResult(album, chat_id);
            }
        } else {
            SendMsg("Non ho trovato nulla. 😓\n" +
                    "Sei sicuro di aver scritto giusto? 😟\n" +
                    "Tips: Prova a scrivere come solo le parti che sai che sono giuste", chat_id);
        }
    }


    public void Save(long chat_id, Album album) {
        String Risposta = "Album aggiunto correttamente al database \uD83D\uDE01";
        Adb.insertAlbum(album);
        Sdb.insertSave(chat_id, album);
        SendMsg(Risposta, chat_id);
    }

    public void History(long chat_id) {
        String[] nomi = Sdb.select("Salva", "Id_Utente", chat_id).split("69104"); //mi serviva un qualcosa per fare lo split.
        for (int i = 0; i < nomi.length; i++) {
            System.out.println(nomi[i]);
            HistoryResult(nomi[i].replace("____", "\n"), chat_id);
        }
    }

    public void All(long chat_id) {
        String Risposta = "Ecco tutti gli Album che hai salvato:\n";
        String[] nomi = Sdb.select("Salva", "Id_Utente", chat_id).split("69104");
        SendMsg(Risposta, chat_id);
        for (int i = 0; i < nomi.length; i++) {
            String reply = "Artista: " + nomi[i].split("____")[0] + "\nTitolo: " + nomi[i].split("____")[1];
            SendMsg(reply, chat_id);
        }
    }

    public void Notify(long chat_id) {
        String Risposta = "Il bot perde di significato senza le notifiche. \n" +
                "Se non ti interessa muta il bot dalle impostazioni di Telegram. \n" +
                "Al contrario, se non ti arrivano le notifiche prova a controllare se hai mutato il bot dalle imostazioni di Telegram";
        SendMsg(Risposta, chat_id);
    }

    public void Help(long chat_id) {
        String Risposta = "Ecco la lista dei comandi:\n" +
                "/start: Avvia la conversazione con il bot.\n" +
                "/search: Permette all'utente di cercare un album --Esempio: /search Hello World, Pinguini Tattici Nucleari--.\n" +
                "/history: Visualizza le informazioni dell'album specificato.\n" +
                "/all: Mostra una lista di tutti gli album che l'utente ha salvato nel database.\n" +
                "/notify: Abilita o disabilita le notifiche.\n" +
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
            System.out.println("Il programma ha cercato di inviare un messaggio, ma non c'è riuscito.");
            e.printStackTrace();
        }
    }

    public void SendSearchResult(Album album, long chat_id) throws Exception {
        String imageUrl = album.getImmagine();
        String imageId = "image_" + UUID.randomUUID().toString();
        imageLinks.put(imageId, imageUrl);
        String callback = "u" + imageId;
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(album.toString())
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Voglio seguire questo!")
                                        .callbackData(callback)
                                        .build()
                                )
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            System.out.println("Il programma ha cercato di rispondere alla ricerca, ma non c'è riuscito.");
            e.printStackTrace();
        }
    }

    public void HistoryResult(String Risposta, long chat_id) {
        String callback;

        if (URLEncoder.encode(Risposta, StandardCharsets.UTF_8).length() > 64) {
            callback = URLEncoder.encode(Risposta, StandardCharsets.UTF_8).substring(0, 63);
        } else {
            callback = URLEncoder.encode(Risposta, StandardCharsets.UTF_8);
        }
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chat_id)
                .text(Risposta)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Mostrami la storia di questo")
                                        .callbackData("h" + callback)
                                        .build()
                                )
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            System.out.println("Il programma ha cercato di inviare la storia, ma non c'è riuscito.");
            e.printStackTrace();
        }
    }

    public void Sendphoto(String image, long chat_id) {
        SendPhoto msg = SendPhoto.builder().chatId(chat_id).photo(new InputFile(image)).caption("").build();
        try {
            telegramClient.execute(msg); // Call method to send the message
        } catch (TelegramApiException e) {
            System.out.println("Il programma ha cercato di inviare una foto, ma non c'è riuscito.");
            e.printStackTrace();
        }
    }

    public static void Save(Album album) {
        Adb.updateAlbum(album);
    }

    public static void ServerUpdate() {
        String[] nomi = Sdb.selectAll("Salva").split("69104");
        for (int i = 0; i < nomi.length; i++) {
            String scraper = Sdb.selectScraper("nome_artista", "nome_album", nomi[i].split("\n")[0], nomi[i].split("\n")[1]);
            if(scraper.contains("Feltrinelli"))
            {
                List<Album> Albums = ScraperFeltrinelli.ScraperF(nomi[i].split("\n")[1], nomi[i].split("\n")[0]);
                for (Album album : Albums) {
                    if(album.getTitolo().toLowerCase().contains(nomi[i].split("\n")[1].toLowerCase()))
                    {
                        MyTelegramBot.Save(album);
                    }
                }
            }
            else if(scraper.contains("Mondadori"))
            {
                List<Album> Albums = ScraperMondadori.ScraperM(nomi[i].split("\n")[0], nomi[i].split("\n")[1]);
                for (Album album : Albums) {
                    if(album.getTitolo().toLowerCase().contains(nomi[i].split("\n")[1].toLowerCase()))
                    {
                        MyTelegramBot.Save(album);
                    }
                }
            }
        }
    }

    public static void Notify(Album album)
    {
        String risposta = "Hey! L'album " + album.getTitolo() + " di " + album.getAutore() + " è in sconto! Fai /history per vedere il prezzo";
        String botToken = "7845151823:AAElw1msUpQYaVPJ5SEt37rrfqE1q13dMwg";
        String[] id = Sdb.selectUtente("Salva", "nome_artista", "nome_album", album.getAutore(), album.getTitolo()).split("____");
        MyTelegramBot obj = new MyTelegramBot(botToken);
        for (String chat_id : id)
            obj.SendMsg(risposta, Long.parseLong(chat_id));
    }

    public static Album estraiAlbum(String Album, String immagine) {
        String fornitore = "";
        Pattern fornitorePattern = Pattern.compile("Fornitore (.*?):");
        Matcher matcher = fornitorePattern.matcher(Album);
        if (matcher.find()) {
            fornitore = matcher.group(1);
        }

        // Estrazione dei dati dell'album
        String titolo = extractData(Album, "🎵 Album: (.*?)\n");
        String autore = extractData(Album, "🧑‍🎤 Artista: (.*?)\n");
        String formato = extractData(Album, "💿 Formato: (.*?)\n");
        String prezzo = extractData(Album, "💰 Prezzo: (.*?), ");

        return new Album(prezzo, autore, titolo, formato, immagine, fornitore);
    }

    // Metodo per estrarre i dati generali come titolo, autore, formato e prezzo
    private static String extractData(String Album, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Album);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

}