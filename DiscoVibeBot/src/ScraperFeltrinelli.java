import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.ArrayList;
import java.util.List;


public class ScraperFeltrinelli {

    public static List<Album> ScraperF(String Nome, String Autore) {
        ChromeOptions options = new ChromeOptions();
        /*options.addArguments("--headless"); // Modalità senza interfaccia grafica
        options.addArguments("--disable-gpu"); // Necessario su alcune piattaforme
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");*/
        WebDriver driver = new ChromeDriver();
        List<Album> albums = new ArrayList<>();

        try {
            driver.get("https://www.lafeltrinelli.it/");

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            WebElement searchInput = driver.findElement(By.id("inputSearch"));
            searchInput.sendKeys(Autore + " " + Nome);
            searchInput.sendKeys(Keys.ENTER);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<WebElement> Divs = driver.findElements(By.xpath("//div[@class='cc-product-list-item']"));

            for (WebElement element : Divs) {
                String[] lines = element.getText().split("\n");
                String Image = element.findElement(By.xpath(".//a[@class='cc-img-link']//img[@class='cc-img']")).getAttribute("data-url"); //boh sto andando a caso
                String titolo = "";
                String artista = "";
                String formato = "";
                String prezzo = "";
                if(lines.length >= 1) titolo = lines[0];
                if(lines.length >= 2) artista = lines[1].replace("di ", "");
                if(lines.length >= 4) formato = lines[3].split(" | ")[0];
                if(lines[5].contains("€")) prezzo = lines[5];
                if(lines[6].contains("€")) prezzo = lines[6];
                if(titolo.toLowerCase().contains(Nome.toLowerCase())) {
                    Album album = new Album(prezzo, artista, titolo, formato, Image);
                    albums.add(album);
                }
            }

            for (Album album : albums) {
                System.out.println(album);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return albums;
    }
}
