import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import consulta.cotacoes.Cotacoes
import consulta.extremos.Extremos
import commoditie.combustivel.local.Revenda
import scraping.Scraping
import krangl.*
import regressao.Escala
import regressao.Registro
import regressao.RegressaoLinear
import usuario.Usuario

class BancoDePrecos {
    var usuarioAtivo: Usuario? = null
        private set
    private var scrapper = Scraping()

    var usuarios = mutableListOf<Usuario>()
    var precosCombustiveis = mutableListOf<Combustivel>()
    var cotacoesDolar = mutableListOf<Dolar>()
    var cotacoesBarrilDePetroleo = mutableListOf<Petroleo>()
    var funcoesRegressao = mutableListOf<RegressaoLinear>()

    fun criaUsuario(nome: String, email: String, senha: String) {
        val u = Usuario(nome = nome, email = email, senha = senha)
        usuarios.add(u)
    }

    fun login(email: String, senha: String): Boolean {
        usuarioAtivo = usuarios.firstOrNull { usuario -> usuario.email == email && usuario.senha == senha }
        return usuarioAtivo != null
    }

    fun cadastraLocalCombustivel(regiao: String,
                                 siglaEstado: String,
                                 municipio: String,
                                 nome: String,
                                 cnpj: String,
                                 bandeira: String): Revenda {

        var local = Revenda()

        local.regiao = regiao
        local.siglaEstado = siglaEstado
        local.municipio = municipio
        local.nome = nome
        local.cnpj = cnpj
        local.bandeira = bandeira

        return local
    }

    fun cadastraPrecoCombustivel(tipo: String,
                                 data: String,
                                 valor: Float,
                                 local: Revenda): Combustivel {

        var combustivel = Combustivel()

        combustivel.tipo = tipo
        combustivel.data = data
        combustivel.valor = valor
        combustivel.local.regiao = local.regiao
        combustivel.local.siglaEstado = local.siglaEstado
        combustivel.local.municipio = local.municipio
        combustivel.local.nome = local.nome
        combustivel.local.cnpj = local.cnpj
        combustivel.local.bandeira = local.bandeira

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

    fun consultaPrecos(data: String, tipoCombustivel: String, municipio: String, UF: String): Cotacoes {
        var consulta = Cotacoes()

        consulta.tipoCombustivel = tipoCombustivel
        consulta.data = data
        consulta.municipio = municipio
        consulta.UF = UF

        var precoCombustivel = precosCombustiveis.filter { Combustivel ->
            Combustivel.tipo == tipoCombustivel && Combustivel.data == data && Combustivel.local!!.municipio == municipio && Combustivel.local!!.siglaEstado == UF
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

        var menorPreco = precosCombustiveis.filter { it.data == data && it.local.siglaEstado == UF && it.tipo == tipo }.minOf { it.valor }
        var municipio = precosCombustiveis.filter { it.data == data && it.local.siglaEstado == UF && it.tipo == tipo }.minByOrNull { it?.valor }!!.local.municipio

        ranking.menorpreco = menorPreco
        ranking.municipio = municipio.toString()

        return ranking
    }

    fun getDadosTreinamento(siglaEstado: String, tipoCombustivel: String): List<Registro> {
        var dados = DataFrame.readCSV("dados202104ge3.csv")
        val registrosSelecionados = mutableListOf<Registro>()
        dados = dados.filter { it["EstadoSigla"].isEqualTo(siglaEstado) }
        dados = dados.filter { it["Produto"].isEqualTo(tipoCombustivel) }
        for (i in 0 until dados.nrow) {
            val registro = Registro(dados["CotacaoPetroleo"][i] as Double, dados["CotacaoDolar"][i] as Double,
                dados["ValorVenda"][i] as Double)
            registrosSelecionados.add(registro)
        }
        return registrosSelecionados
    }

    fun treinaModelo(siglaEstado: String, tipoCombustivel: String, dados: List<Registro>): RegressaoLinear {
        val modelo = RegressaoLinear()
        modelo.estado = siglaEstado
        modelo.tipoCombustivel = tipoCombustivel
        val dadosPadronizados = Escala().padronizar(dados)
        val coeficientes = RegressaoLinear().otimizaCoeficientes(dadosPadronizados,3,0.01,0.000000000001,100000)
        modelo.slopePetro = coeficientes[0]
        modelo.slopeDolar = coeficientes[1]
        modelo.intercepto = coeficientes[2]
        Escala().despadronizar(dados,modelo)

        funcoesRegressao.add(modelo)

        return modelo
    }

    fun calculaPrevisao(siglaEstado: String, tipoCombustivel: String, valorPetroleo: Double, valorDolar: Double): Double {
        val modelo = funcoesRegressao.filter { it.estado == siglaEstado && it.tipoCombustivel == tipoCombustivel }.first()
        return modelo.intercepto + (modelo.slopePetro*valorPetroleo) + (modelo.slopeDolar*valorDolar)
    }
}

