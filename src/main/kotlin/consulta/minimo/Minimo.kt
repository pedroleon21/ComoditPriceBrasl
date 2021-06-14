
package consulta.minimo

import commoditie.combustivel.local.Revenda

class Minimo: ConsultaMinimo() {
    var menorpreco: Double = 0.0
    var nomeRevenda: String = Revenda().nome
    var cnpjRevenda: String = Revenda().cnpj
    var bandeiraRevenda: String = Revenda().bandeira
}