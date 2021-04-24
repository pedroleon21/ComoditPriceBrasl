import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import preco.cotacao.Cotacao
import preco.minimo.Minimo

class BancoDePrecos {
    var precosCombustiveis = mutableListOf<Combustivel>()
    var cotacoesDolar = mutableListOf<Dolar>()
    var cotacoesBarrilDePetroleo = mutableListOf<Petroleo>()

    fun cadastraPrecoCombustivel(tipo: String,
                                 data: String,
                                 valor: Float,
                                 municipio: String,
                                 regiao: String,
                                 UF: String,
                                 qtdPostos: Int): Combustivel {

        var combustivel = Combustivel()

        combustivel.tipo = tipo
        combustivel.data = data
        combustivel.valor = valor
        combustivel.municipio = municipio
        combustivel.regiao = regiao
        combustivel.UF = UF
        combustivel.qtdPostos = qtdPostos

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

    fun consultaPrecos(data: String, tipoCombustivel: String, municipio: String, UF: String): Cotacao {
        var consulta = Cotacao()

        consulta.tipoCombustivel = tipoCombustivel
        consulta.data = data
        consulta.municipio = municipio
        consulta.UF = UF

        var precoCombustivel = precosCombustiveis.filter { Combustivel ->
            Combustivel.tipo == tipoCombustivel && Combustivel.data == data && Combustivel.municipio == municipio && Combustivel.UF == UF
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
