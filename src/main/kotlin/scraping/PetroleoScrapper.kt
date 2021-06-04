package scraping

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class PetroleoScrapper {

    fun getPrecoPetroleo(data: String): Float {

        val options = ChromeOptions()
        val driver = ChromeDriver(options.setHeadless(true))
        val wait = WebDriverWait(driver, 10)

        driver.get("https://br.investing.com/commodities/brent-oil-historical-data")

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

        Thread.sleep(5000)

        val docHtml: String = driver.pageSource

        driver.quit()

        Jsoup.parse(docHtml).run {
            val x = select("td.greenFont, td.redFont").select("td[data-real-value]")
            var num: Float

            return if (x.size == 1) {
                num = x.text().replace(",", ".").toFloat()
                num
            } else {
                0.0F
            }
        }
    }
}