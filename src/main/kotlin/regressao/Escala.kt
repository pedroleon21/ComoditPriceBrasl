package regressao
import org.nield.kotlinstatistics.standardDeviation

class Escala {
    lateinit var mediasOriginais: FloatArray
    lateinit var desviosOriginais: FloatArray

    fun padronizar(x: List<Registro>): List<Registro> {
        mediasOriginais[0] = (x.map { reg -> reg.xPetro}.sum())/x.size
        mediasOriginais[1] = (x.map { reg -> reg.xDolar}.sum())/x.size
        desviosOriginais[0] = x.map { reg -> reg.xPetro}.standardDeviation().toFloat()
        desviosOriginais[0] = x.map { reg -> reg.xDolar}.standardDeviation().toFloat()
        val xPadronizado = mutableListOf<Registro>()
        for (i in x.indices) {
            val zscorePetro = (x[i].xPetro - mediasOriginais[0])/desviosOriginais[0]
            val zscoreDolar = (x[i].xDolar - mediasOriginais[1])/desviosOriginais[1]
            val registro = Registro(zscorePetro,zscoreDolar,x[i].yPreco)
            xPadronizado.add(registro)
        }
        return xPadronizado
    }

    fun despadronizar(x: RegressaoLinear) {
        x.slopePetro = (x.slopePetro*desviosOriginais[0]) + mediasOriginais[0]
        x.slopeDolar = (x.slopeDolar*desviosOriginais[1]) + mediasOriginais[1]
        x.intercepto = x.intercepto - (((mediasOriginais[0]/desviosOriginais[0])*x.slopePetro)+
                ((mediasOriginais[1]/desviosOriginais[1])*x.slopeDolar))
    }
}