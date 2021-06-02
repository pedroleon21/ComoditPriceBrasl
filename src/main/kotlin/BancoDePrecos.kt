import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import consultaPrecos.cotacoes.Cotacoes
import consultaPrecos.extremos.Extremos
import commoditie.combustivel.local.Local
import krangl.*
import regressao.Escala
import regressao.Registro
import regressao.RegressaoLinear

class BancoDePrecos {
    var precosCombustiveis = mutableListOf<Combustivel>()
    var cotacoesDolar = mutableListOf<Dolar>()
    var cotacoesBarrilDePetroleo = mutableListOf<Petroleo>()

    fun cadastraLocalCombustivel(municipio: String,
                                 regiao: String,
                                 uf: String,
                                 qtdPostos: Int): Local {

        var local = Local()

        local.municipio = municipio
        local.regiao = regiao
        local.uf = uf
        local.qtdPostos = qtdPostos

        return local
    }

    fun cadastraPrecoCombustivel(tipo: String,
                                 data: String,
                                 valor: Float,
                                 local: Local): Combustivel {

        var combustivel = Combustivel()

        combustivel.tipo = tipo
        combustivel.data = data
        combustivel.valor = valor
        combustivel.local!!.municipio = local.municipio
        combustivel.local!!.regiao = local.regiao
        combustivel.local!!.uf = local.uf
        combustivel.local!!.qtdPostos = local.qtdPostos

        precosCombustiveis.add(combustivel)

        return combustivel
    }

    fun cadastraCotacaoDolar(data: String, valor: Float): Dolar {
        var cotacao = Dolar()

        cotacao.data = data
        cotacao.valor = valor

        cotacoesDolar.add(cotacao)

        return cotacao
    }

    fun cadastraCotacaoPetroleo(data: String, valor: Float): Petroleo {
        var cotacao = Petroleo()

        cotacao.data = data
        cotacao.valor = valor

        cotacoesBarrilDePetroleo.add(cotacao)

        return cotacao
    }

    fun consultaPrecos(data: String, tipoCombustivel: String, municipio: String, UF: String): Cotacoes {
        var consulta = Cotacoes()

        consulta.tipoCombustivel = tipoCombustivel
        consulta.data = data
        consulta.municipio = municipio
        consulta.UF = UF

        var precoCombustivel = precosCombustiveis.filter { Combustivel ->
            Combustivel.tipo == tipoCombustivel && Combustivel.data == data && Combustivel.local!!.municipio == municipio && Combustivel.local!!.uf == UF
        }.first()

        var cotacaoDolar = cotacoesDolar.filter { Dolar ->
            Dolar.data == data
        }.first()

        var cotacaoPetroleo = cotacoesBarrilDePetroleo.filter { Petroleo ->
            Petroleo.data == data
        }.first()

        consulta.preco = precoCombustivel.valor
        consulta.cotacaoDolar = cotacaoDolar.valor
        consulta.cotacaoPetroleo = cotacaoPetroleo.valor

        return consulta
    }

    fun rankingPrecos(data: String, tipo: String, UF: String): Extremos{

        var ranking = Extremos()

        ranking.UF = UF
        ranking.data = data
        ranking.tipoCombustivel = tipo

        var menorPreco = precosCombustiveis.filter{it.data == data && it.local!!.uf == UF && it.tipo == tipo}!!.minOf { it.valor }
        var municipio = precosCombustiveis.filter{it.data == data && it.local!!.uf == UF && it.tipo == tipo}!!.minByOrNull { it.valor }!!.local.municipio

        ranking.menorpreco = menorPreco
        ranking.municipio = municipio.toString()

        return ranking
    }

    fun getDadosTreinamento(siglaEstado: String, municipio: String, tipoCombustivel: String): List<Registro> {
        var dados = DataFrame.readCSV("dadosConsolidados.csv")
        val registrosSelecionados = mutableListOf<Registro>()
        dados = dados.filter { it["CotacaoPetroleo"].isNotNA() }
        dados = dados.filter { it["CotacaoPetroleo"].isNotNA() }
        dados = dados.filter { it["Estado"].isEqualTo(siglaEstado) }
        dados = dados.filter { it["Municipio"].isEqualTo(municipio) }
        dados = dados.filter { it["Produto"].isEqualTo(tipoCombustivel) }
        for (i in 0 until dados.nrow) {
            var registro = Registro(dados["CotacaoPetroleo"][i] as Float,dados["CotacaoDolar"][i] as Float,
                dados["ValorVenda"][i] as Float)
            registrosSelecionados.add(registro)
        }
        return registrosSelecionados
    }

    fun treinaModelo(dados: List<Registro>): RegressaoLinear {
        val modelo = RegressaoLinear()
        val dadosPadronizados = Escala().padronizar(dados)
        val coeficientes = RegressaoLinear().otimizaCoeficientes(dadosPadronizados,2,0.01F,0.000000000001,30000)
        modelo.slopePetro = coeficientes[0]
        modelo.slopeDolar = coeficientes[1]
        modelo.intercepto = coeficientes[2]
        Escala().despadronizar(modelo)
        return modelo
    }

    fun calculaPrevisao(modelo: RegressaoLinear, valorPetroleo: Float, valorDolar: Float): Float {
        return modelo.intercepto + (modelo.slopePetro*valorPetroleo) + (modelo.slopeDolar*valorDolar)
    }
}