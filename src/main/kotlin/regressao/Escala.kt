package regressao
import org.nield.kotlinstatistics.standardDeviation

class Escala {
    var mediaPetro: Double = 0.0
    var desvioPetro: Double = 0.0
    var mediaDolar: Double = 0.0
    var desvioDolar: Double = 0.0

    fun padronizar(x: List<Registro>): List<Registro> {
        mediaPetro = x.map { reg -> reg.xPetro}.average()
        mediaDolar = x.map { reg -> reg.xDolar}.average()
        desvioPetro = x.map { reg -> reg.xPetro}.standardDeviation()
        desvioDolar = x.map { reg -> reg.xDolar}.standardDeviation()
        val xPadronizado = mutableListOf<Registro>()
        for (i in x.indices) {
            val zscorePetro = (x[i].xPetro - mediaPetro)/desvioPetro
            val zscoreDolar = (x[i].xDolar - mediaDolar)/desvioDolar
            val registro = Registro(zscorePetro,zscoreDolar,x[i].yPreco)
            xPadronizado.add(registro)
        }
        return xPadronizado
    }

    fun despadronizar(x: List<Registro>, modelo: RegressaoLinear) {
        mediaPetro = x.map { reg -> reg.xPetro}.average()
        mediaDolar = x.map { reg -> reg.xDolar}.average()
        desvioPetro = x.map { reg -> reg.xPetro}.standardDeviation()
        desvioDolar = x.map { reg -> reg.xDolar}.standardDeviation()
        modelo.intercepto = modelo.intercepto - (((mediaPetro/desvioPetro)*modelo.slopePetro)+
                ((mediaDolar/desvioDolar)*modelo.slopeDolar))
        modelo.slopePetro = modelo.slopePetro/desvioPetro
        modelo.slopeDolar = modelo.slopeDolar/desvioDolar
    }
}