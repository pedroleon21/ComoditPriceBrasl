package regressao

import kotlin.random.Random

class RegressaoLinear {
    var intercepto: Float = 0.0F
    var slopeDolar: Float = 0.0F
    var slopePetro: Float = 0.0F

    fun setPesoInicial(qtdVariaveis: Int): FloatArray {
        val pesos = FloatArray(qtdVariaveis)
        for (i in 1 until qtdVariaveis){
            val w = Random.nextFloat()
            pesos[i] = w
        }
        return pesos
    }

    fun calcHipotese(x: Registro, w: FloatArray): Float {
        return (x.xPetro * w[0]) + (x.xDolar * w[1] + w[2])
    }

    fun calcErro(x: Registro, w: FloatArray): Float {
        return calcHipotese(x, w) - x.yPreco
    }

    fun calcErroQuadrado(x: Registro, w: FloatArray): Float {
        return calcErro(x, w) * calcErro(x, w)
    }

    fun calcGradiente(x: List<Registro>, w: FloatArray): FloatArray {
        val gradientes = FloatArray(w.size)
        val n: Int = x.size
        val gradientePetro: Float = (x.map { reg -> reg.xPetro * calcErro(reg,w) }.sum()) / n
        gradientes[0] = gradientePetro
        val gradienteDolar: Float = (x.map { reg -> reg.xDolar * calcErro(reg,w) }.sum()) / n
        gradientes[1] = gradienteDolar
        val gradienteIntercepto: Float = (x.map { reg -> 1 * calcErro(reg,w) }.sum()) / n
        gradientes[2] = gradienteIntercepto
        return gradientes
    }

    fun setNovoPeso(x: List<Registro>, w: FloatArray, taxa: Float): FloatArray {
        val novosPesos = FloatArray(w.size)
        val gradientes: FloatArray = calcGradiente(x,w)
        for(i in w.indices) {
            var novoPeso: Float = w[i] - (taxa * gradientes[i])
            novosPesos[i] = novoPeso
        }
        return novosPesos
    }

    fun otimizaCoeficientes(x: List<Registro>,qtdVariaveis: Int, taxa: Float, eta: Double, qtdIteracoes: Int): FloatArray {
        var w: FloatArray = setPesoInicial(qtdVariaveis)
        val histSSL = FloatArray(qtdVariaveis)
        var i: Int = 0
        for(k in 1 until qtdIteracoes) {
            val SSL = x.map { reg -> calcErroQuadrado(reg,w) }.sum()
            val novoW: FloatArray = setNovoPeso(x,w,taxa)
            val novoSSL = x.map { reg -> calcErroQuadrado(reg,novoW) }.sum()
            w = novoW
            if(k<=5 && novoSSL - SSL <= eta && novoSSL - SSL >= eta) {
                histSSL[i] = novoSSL
                i += 1
                slopePetro = w[0]
                slopeDolar = w[1]
                intercepto = w[2]
                return histSSL
            }
            if(k % (qtdIteracoes / 20) == 0) {
                histSSL[i] = novoSSL
                i += 1
            }
        }
        slopePetro = w[0]
        slopeDolar = w[1]
        intercepto = w[2]
        return histSSL
    }
}
