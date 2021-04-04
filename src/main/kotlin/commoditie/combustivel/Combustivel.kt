package commoditie.combustivel

import commoditie.Commoditie

class Combustivel: Commoditie() {
    var tipo: String? = null
    var municipio: String = ""
    var regiao: String = ""
    var UF: String? = ""
    var qtdPostos: Int = 0
}