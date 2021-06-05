package consulta

import scraping.CombustivelScrapper

class CombustivelConsulta {
    // TODO parse string to object
    fun getAllPrecos(): String {
        var scrapper = CombustivelScrapper()
        return scrapper.getListPrecos()
    }
}