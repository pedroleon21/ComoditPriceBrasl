import commoditie.combustivel.Combustivel
import commoditie.combustivel.local.Local
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import consulta.Consulta

class BancoDePrecos {
    var localizaCombustivel = mutableListOf<Local>()
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

        localizaCombustivel.add(local)

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
        combustivel.local?.municipio = local.municipio
        combustivel.local?.regiao = local.regiao
        combustivel.local?.uf = local.uf
        combustivel.local?.qtdPostos = local.qtdPostos

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

        var precoCombustivel = precosCombustiveis.filter { Combustivel ->
            Combustivel.tipo == tipoCombustivel && Combustivel.data == data && Combustivel.local?.municipio == municipio && Combustivel.local?.uf == UF
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

