package commoditie.combustivel

import commoditie.Commoditie
import commoditie.combustivel.local.Local

class Combustivel: Commoditie() {
    var tipo: String = ""
    var local: Local = Local()
}