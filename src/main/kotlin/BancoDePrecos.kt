import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import consulta.Consulta
import commoditie.combustivel.local.Local

class BancoDePrecos {
    var localizaCombustivel = mutableListOf<Local>()
    var precosCombustiveis = mutableListOf<Combustivel>()
    var cotacoesDolar = mutableListOf<Dolar>()
    var cotacoesBarrilDePetroleo = mutableListOf<Petroleo>()

    fun cadastraLocalCombustivel(
        municipio: String,
        regiao: String,
        uf: String,
        qtdPostos: Int
    ): Local {

        val local = Local()
        local.municipio = municipio
        local.regiao = regiao
        local.uf = uf
        local.qtdPostos = qtdPostos

        localizaCombustivel.add(local)

        return local
    }

    fun cadastraPrecoCombustivel(
        tipo: String,
        data: String,
        valor: Float
    ): Combustivel {

        val combustivel = Combustivel()

        combustivel.tipo = tipo
        combustivel.data = data
        combustivel.valor = valor

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

    fun consultaPrecos(data: String, tipoCombustivel: String, municipio: String, UF: String): Consulta {
        var consulta = Consulta()

        consulta.tipoCombustivel = tipoCombustivel
        consulta.data = data
        consulta.municipio = municipio
        consulta.UF = UF

        var localCombustivel = localizaCombustivel.filter { Local ->
            Local.municipio == municipio && Local.uf == UF
        }.first()

        var precoCombustivel = precosCombustiveis.filter { Combustivel ->
            Combustivel.tipo == tipoCombustivel && Combustivel.data == data
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
}

