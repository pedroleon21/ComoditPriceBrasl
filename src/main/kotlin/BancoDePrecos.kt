import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import preco.cotacao.Cotacao
import preco.minimo.Minimo
import consultaPrecos.cotacoes.Cotacoes
import consultaPrecos.extremos.Extremos
import commoditie.combustivel.local.Local
import scraping.Scraping

class BancoDePrecos {
    private var scrapper = Scraping()
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
        combustivel.local.municipio = local.municipio
        combustivel.local.regiao = local.regiao
        combustivel.local.uf = local.uf
        combustivel.local.qtdPostos = local.qtdPostos

        precosCombustiveis.add(combustivel)

        return combustivel
    }

    fun cadastraCotacaoDolar(data: String): Dolar {
        var cotacao = Dolar()

        cotacao.data = data
        cotacao.valor = scrapper.getValor(data, "https://br.investing.com/currencies/usd-brl-historical-data")

        cotacoesDolar.add(cotacao)

        return cotacao
    }

    fun cadastraCotacaoPetroleo(data: String): Petroleo {
        var cotacao = Petroleo()

        cotacao.data = data
        cotacao.valor = scrapper.getValor(data, "https://br.investing.com/commodities/brent-oil-historical-data")

        cotacoesBarrilDePetroleo.add(cotacao)

        return cotacao
    }


    fun consultaPrecos(data: String, tipoCombustivel: String, municipio: String, UF: String): Cotacao {
        var consulta = Cotacao()

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

        consulta.preco = precoCombustivel.valor!!
        consulta.cotacaoDolar = cotacaoDolar.valor!!
        consulta.cotacaoPetroleo = cotacaoPetroleo.valor!!

        return consulta
    }

    fun rankingPrecos(data: String, tipo: String, UF: String): Extremos{

        var ranking = Extremos()

        ranking.UF = UF
        ranking.data = data
        ranking.tipoCombustivel = tipo

        var menorPreco = precosCombustiveis.filter { it.data == data && it.local.uf == UF && it.tipo == tipo }.minOf { it.valor }
        var municipio = precosCombustiveis.filter { it.data == data && it.local.uf == UF && it.tipo == tipo }.minByOrNull { it?.valor }!!.local.municipio

        ranking.menorpreco = menorPreco
        ranking.municipio = municipio.toString()

        return ranking
    }
}

    fun consultaMenorPreco(data: String, tipo: String, UF: String): Minimo{

        var consulta = Minimo()

        consulta.UF = UF
        consulta.data = data
        consulta.tipoCombustivel = tipo

        var menorPreco = precosCombustiveis.filter{it.data == data && it.UF == UF && it.tipo == tipo}?.minOf { it.valor }
        var municipio = precosCombustiveis.filter{it.data == data && it.UF == UF && it.tipo == tipo}?.minByOrNull { it.valor }?.municipio

        consulta.menorpreco = menorPreco
        consulta.municipio = municipio.toString()

        return consulta
    }
}
