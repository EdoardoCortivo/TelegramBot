import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.ArrayList;
import java.util.List;

public class ScraperMondadori {
    public static List<Album> ScraperM(String Nome, String Autore) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        WebDriver driver = new ChromeDriver(options);
        List<Album> albums = new ArrayList<>();

        try {
            driver.get("https://www.mondadoristore.it");

            WebElement searchInput = driver.findElement(By.id("search-input"));
            searchInput.sendKeys(Autore + " " + Nome);
            searchInput.sendKeys(Keys.ENTER);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Il programma ha cercato stoppare la pagina web, ma non c'è riuscito.");
                e.printStackTrace();
            }

            List<WebElement> Divs = driver.findElements(By.xpath("//div[@data-variant='Vinile' or @data-variant='Audio CD']"));

            for (WebElement element : Divs) {
                String[] lines = element.getText().split("\n");
                String Image = element.findElement(By.xpath(".//div[@class='image-box']//img")).getAttribute("src");
                String titolo = "";
                String artista = "";
                String formato1 = "";
                String prezzo1 = "";
                String formato2 = "";
                String prezzo2 = "";

                if (lines.length >= 1) titolo = lines[0];
                if (lines.length >= 2) artista = lines[1];

                for (int i = 2; i < lines.length; i++) {
                    if (lines[i].contains("Musica -")) {
                        formato1 = lines[i].split(" - ")[1];
                        if (i + 1 < lines.length && lines[i + 1].contains("€")) {
                            prezzo1 = lines[i + 1];
                        }
                    } else if (lines[i].contains("Vinile")) {
                        formato2 = "Vinile";
                        prezzo2 = lines[i].replace("Vinile", "").trim();
                    }
                }

                // Filtro per il formato e per il titolo e artista
                if (formato2 != "") {
                    if (titolo.toLowerCase().contains(Nome.toLowerCase()) || artista.toLowerCase().contains(Autore.toLowerCase())) {
                        Album album = new Album(prezzo2, artista, titolo, formato2, Image, "Mondadori");
                        albums.add(album);
                    }
                }

                // Aggiungi un controllo per il primo formato (CD o altro)
                if (formato1 != "") {
                    if (titolo.toLowerCase().contains(Nome.toLowerCase()) || artista.toLowerCase().contains(Autore.toLowerCase())) {
                        Album album = new Album(prezzo1, artista, titolo, formato1, Image, "Mondadori");
                        albums.add(album);
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Il programma ha cercato di accedere alla pagina web, ma non c'è riuscito.");
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return albums;
    }
}