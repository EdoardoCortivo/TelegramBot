import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {
        String botToken = "7845151823:AAElw1msUpQYaVPJ5SEt37rrfqE1q13dMwg";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyTelegramBot(botToken));
            System.out.println("DiscoVibesBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}