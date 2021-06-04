package regressao

import kotlin.random.Random

class RegressaoLinear {
    var intercepto: Double = 0.0
    var slopeDolar: Double = 0.0
    var slopePetro: Double = 0.0

    fun setPesoInicial(qtdVariaveis: Int): DoubleArray {
        val pesos = DoubleArray(qtdVariaveis) {Random.nextDouble()}
        return pesos
    }

    fun calcHipotese(x: Registro, w: DoubleArray): Double {
        return (x.xPetro * w[0]) + (x.xDolar * w[1]) + w[2]
    }

    fun calcErro(x: Registro, w: DoubleArray): Double {
        return calcHipotese(x, w) - x.yPreco
    }

    fun calcErroQuadrado(x: Registro, w: DoubleArray): Double {
        return calcErro(x, w) * calcErro(x, w)
    }

    fun calcGradiente(x: List<Registro>, w: DoubleArray): DoubleArray {
        val gradientes = DoubleArray(w.size)
        val n: Int = x.size
        val gradientePetro: Double = (x.map { reg -> reg.xPetro * calcErro(reg,w) }.sum()) / n
        gradientes[0] = gradientePetro
        val gradienteDolar: Double = (x.map { reg -> reg.xDolar * calcErro(reg,w) }.sum()) / n
        gradientes[1] = gradienteDolar
        val gradienteIntercepto: Double = (x.map { reg -> 1 * calcErro(reg,w) }.sum()) / n
        gradientes[2] = gradienteIntercepto
        return gradientes
    }

    fun setNovoPeso(x: List<Registro>, w: DoubleArray, taxa: Double): DoubleArray {
        val novosPesos = DoubleArray(w.size)
        val gradientes: DoubleArray = calcGradiente(x,w)
        for(i in w.indices) {
            var novoPeso: Double = w[i] - (taxa * gradientes[i])
            novosPesos[i] = novoPeso
        }
        return novosPesos
    }

    fun otimizaCoeficientes(x: List<Registro>, qtdVariaveis: Int, taxa: Double, eta: Double, qtdIteracoes: Int): DoubleArray {
        var w: DoubleArray = setPesoInicial(qtdVariaveis)
        val histSSL = DoubleArray(qtdIteracoes+1)
        var i: Int = 0
        for(k in 1 until qtdIteracoes) {
            val SSL = x.map { reg -> calcErroQuadrado(reg,w) }.sum()
            val novoW: DoubleArray = setNovoPeso(x,w,taxa)
            val novoSSL = x.map { reg -> calcErroQuadrado(reg,novoW) }.sum()
            w = novoW
            if(k<=5 && novoSSL - SSL <= eta && novoSSL - SSL >= eta) {
                histSSL[i] = novoSSL
                i += 1
                return w
            }
            if(k % (qtdIteracoes / 20) == 0) {
                histSSL[i] = novoSSL
                i += 1
            }
        }
        return w
    }
}
