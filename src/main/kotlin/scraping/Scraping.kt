package scraping

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class Scraping {

    fun getValor(data: String, url: String): Float {

        val options = ChromeOptions()
        val driver = ChromeDriver(options.setHeadless(true))
        val wait = WebDriverWait(driver, 10)

        driver.get(url)

        wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler"))).click()

        Thread.sleep(5000)

        driver.executeScript("window.scrollTo(0, 800)")

        driver.findElement(By.id("flatDatePickerCanvasHol")).click()

        val inicioData: WebElement = driver.findElement(By.id("startDate"))
        inicioData.clear()
        inicioData.sendKeys(data)

        val fimData: WebElement = driver.findElement(By.id("endDate"))
        fimData.clear()
        fimData.sendKeys(data)

        driver.findElement(By.id("applyBtn")).click()

        Thread.sleep(3000)

        val docHtml: String = driver.pageSource

        driver.quit()

        Jsoup.parse(docHtml).run {

            val valor = select("td.greenFont, td.redFont").select("td[data-real-value]")

            return if (valor.size == 1) {
                valor.text().replace(",", ".").toFloat()
            } else {
                0.0F
            }
        }
    }
}