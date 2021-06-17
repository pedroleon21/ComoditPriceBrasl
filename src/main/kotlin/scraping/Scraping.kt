package scraping

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.ElementClickInterceptedException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class Scraping {

    fun getValor(data: String, url: String): Float {

        val options = ChromeOptions()
        val driver = ChromeDriver(options.setHeadless(false))
        val wait = WebDriverWait(driver, 10)

        driver.get(url)

        wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler"))).click()

        driver.executeScript("document.getElementsByClassName('displayNone')[0].setAttribute('style', 'none')")
        driver.executeScript("window.scrollTo(0, 800)")

        try {
            manipulaData(driver, data)
        }catch(e: ElementClickInterceptedException) {
            driver.findElement(By.className("largeBannerCloser")).click()
            manipulaData(driver, data)
        }

        Thread.sleep(2000)

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

fun manipulaData(driver: WebDriver, data: String) {
    driver.findElement(By.id("flatDatePickerCanvasHol")).click()

    val inicioData: WebElement = driver.findElement(By.id("startDate"))
    inicioData.clear()
    inicioData.sendKeys(data)

    val fimData: WebElement = driver.findElement(By.id("endDate"))
    fimData.clear()
    fimData.sendKeys(data)

    Thread.sleep(2000)

    driver.findElement(By.id("applyBtn")).click()
}