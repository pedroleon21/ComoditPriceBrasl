package commoditie.combustivel

import commoditie.Commoditie
import commoditie.combustivel.local.Revenda

class Combustivel: Commoditie() {
    var tipo: String = ""
    var local: Revenda = Revenda()
}