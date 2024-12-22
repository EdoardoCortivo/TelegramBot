import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class Main {
    
    private static final long INTERVAL = 3600 * 5; // intervallo in secondi

    public static void main(String[] args) {
        String botToken = "7845151823:AAElw1msUpQYaVPJ5SEt37rrfqE1q13dMwg";
        
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyTelegramBot(botToken));
            System.out.println("DiscoVibesBot successfully started!");

            //schedulePeriodicTask();

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void schedulePeriodicTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            MyTelegramBot.ServerUpdate();
        }, 0, INTERVAL, TimeUnit.SECONDS);
    }

}
