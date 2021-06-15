import commoditie.combustivel.Combustivel
import commoditie.moeda.Dolar
import commoditie.materiaprima.Petroleo
import commoditie.combustivel.local.Revenda
import consulta.media.MediaEstado
import consulta.minimo.Minimo
import scraping.Scraping
import krangl.*
import regressao.Escala
import regressao.Registro
import regressao.RegressaoLinear
import usuario.Usuario
import java.io.File

class BancoDePrecos {
    var usuarioAtivo: Usuario? = null
        private set
    private var scrapper = Scraping()

    var usuarios = mutableListOf<Usuario>()
    var funcoesRegressao = mutableListOf<RegressaoLinear>()

    fun criaUsuario(nome: String, email: String, senha: String) {
        val u = Usuario(nome = nome, email = email, senha = senha)
        usuarios.add(u)
    }

    fun login(email: String, senha: String): Boolean {
        usuarioAtivo = usuarios.firstOrNull { usuario -> usuario.email == email && usuario.senha == senha }
        return usuarioAtivo != null
    }

    fun cadastraLocalCombustivel(regiao: String, siglaEstado: String, municipio: String,
                                 nome: String, cnpj: String, bandeira: String): Revenda {
        val local = Revenda()

        local.regiao = regiao
        local.siglaEstado = siglaEstado
        local.municipio = municipio
        local.nome = nome
        local.cnpj = cnpj
        local.bandeira = bandeira

        return local
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun cadastraPrecoCombustivel(tipo: String, data: String, valor: Double, local: Revenda): Combustivel {
        val combustivel = Combustivel()

        combustivel.tipo = tipo
        combustivel.data = data
        combustivel.valor = valor
        combustivel.local.regiao = local.regiao
        combustivel.local.siglaEstado = local.siglaEstado
        combustivel.local.municipio = local.municipio
        combustivel.local.nome = local.nome
        combustivel.local.cnpj = local.cnpj
        combustivel.local.bandeira = local.bandeira

        val cotacaoPetroleo = buscaCotacaoPetroleo(data)
        val cotacaoDolar = buscaCotacaoDolar(data)

        lateinit var dados: DataFrame

        if (combustivel.tipo.uppercase() == "DIESEL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDiesel.csv")
            dados = dados.addRow(listOf(combustivel.local.regiao, combustivel.local.siglaEstado, combustivel.local.municipio,
                combustivel.local.nome, combustivel.local.cnpj, combustivel.local.bandeira, tipo, combustivel.data,
                combustivel.valor, cotacaoPetroleo.valor, cotacaoDolar.valor))
            dados.writeCSV(File("dadosHistoricos/dadosDiesel.csv"))
        } else if (combustivel.tipo.uppercase() == "DIESEL S10") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDieselS10.csv")
            dados = dados.addRow(listOf(combustivel.local.regiao, combustivel.local.siglaEstado, combustivel.local.municipio,
                combustivel.local.nome, combustivel.local.cnpj, combustivel.local.bandeira, tipo, combustivel.data,
                combustivel.valor, cotacaoPetroleo.valor, cotacaoDolar.valor))
            dados.writeCSV(File("dadosHistoricos/dadosDieselS10.csv"))
        } else if (combustivel.tipo.uppercase() == "GASOLINA") {
            dados = DataFrame.readCSV("/home/bruno/Downloads/DadosHistoricos/dados202101ge.csv")
            dados = dados.addRow(listOf(combustivel.local.regiao, combustivel.local.siglaEstado, combustivel.local.municipio,
                combustivel.local.nome, combustivel.local.cnpj, combustivel.local.bandeira, tipo, combustivel.data,
                combustivel.valor, cotacaoPetroleo.valor, cotacaoDolar.valor))
            dados.writeCSV(File("/home/bruno/Downloads/DadosHistoricos/dados202101ge.csv"))
        } else if (combustivel.tipo.uppercase() == "GASOLINA ADITIVADA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaAditivada.csv")
            dados = dados.addRow(listOf(combustivel.local.regiao, combustivel.local.siglaEstado, combustivel.local.municipio,
                combustivel.local.nome, combustivel.local.cnpj, combustivel.local.bandeira, tipo, combustivel.data,
                combustivel.valor, cotacaoPetroleo.valor, cotacaoDolar.valor))
            dados.writeCSV(File("dadosHistoricos/GasolinaAditivada.csv"))
        } else if (combustivel.tipo.uppercase() == "ETANOL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosEtanol.csv")
            dados = dados.addRow(listOf(combustivel.local.regiao, combustivel.local.siglaEstado, combustivel.local.municipio,
                combustivel.local.nome, combustivel.local.cnpj, combustivel.local.bandeira, tipo, combustivel.data,
                combustivel.valor, cotacaoPetroleo.valor, cotacaoDolar.valor))
            dados.writeCSV(File("dadosHistoricos/dadosEtanol.csv"))
        }
        return combustivel
    }

    private fun buscaCotacaoDolar(data: String): Dolar {
        val cotacao = Dolar()

        cotacao.data = data
        cotacao.valor = scrapper.getValor(data, "https://br.investing.com/currencies/usd-brl-historical-data")

        return cotacao
    }

    private fun buscaCotacaoPetroleo(data: String): Petroleo {
        val cotacao = Petroleo()

        cotacao.data = data
        cotacao.valor = scrapper.getValor(data, "https://br.investing.com/commodities/brent-oil-historical-data")

        return cotacao
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun consultaMediaEstado(tipoCombustivel: String, data: String, siglaEstado: String): MediaEstado {
        val consulta = MediaEstado()

        consulta.tipoCombustivel = tipoCombustivel
        consulta.data = data
        consulta.UF = siglaEstado

        lateinit var dados: DataFrame

        if (tipoCombustivel.uppercase() == "DIESEL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDiesel.csv")
        } else if (tipoCombustivel.uppercase() == "DIESEL S10") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDieselS10.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaComum.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA ADITIVADA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaAditivada.csv")
        } else if (tipoCombustivel.uppercase() == "ETANOL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosEtanol.csv")
        }
        dados = dados.filter { it["EstadoSigla"].isEqualTo(siglaEstado) }
        dados = dados.filter {it["DataColeta"].isEqualTo(data)}
        val menor = dados.summarize("menorPreco" to { it["ValorVenda"].min() })
        consulta.menorPreco = menor["menorPreco"][0] as Double
        val maior = dados.summarize("maiorPreco" to { it["ValorVenda"].max() })
        consulta.maiorPreco = maior["maiorPreco"][0] as Double
        val media = dados.summarize("media" to { it["ValorVenda"].mean() })
        consulta.media = media["media"][0] as Double

        return consulta
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun consultaMenorPreco(tipoCombustivel: String, data: String, municipio: String, siglaEstado: String): Minimo {
        val melhorPreco = Minimo()

        melhorPreco.UF = siglaEstado
        melhorPreco.municipio = municipio
        melhorPreco.data = data
        melhorPreco.tipoCombustivel = tipoCombustivel

        lateinit var dados: DataFrame

        if (tipoCombustivel.uppercase() == "DIESEL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDiesel.csv")
        } else if (tipoCombustivel.uppercase() == "DIESEL S10") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDieselS10.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaComum.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA ADITIVADA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaAditivada.csv")
        } else if (tipoCombustivel.uppercase() == "ETANOL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosEtanol.csv")
        }
        dados = dados.filter { it["EstadoSigla"].isEqualTo(siglaEstado) }
        dados = dados.filter { it["Municipio"].isEqualTo(municipio) }
        dados = dados.filter { it["DataColeta"].isEqualTo(data) }
        var menor = dados.summarize("menorPreco" to { it["ValorVenda"].min() })
        melhorPreco.menorpreco = menor["menorPreco"][0] as Double
        menor = dados.filter { it["ValorVenda"] isEqualTo (melhorPreco.menorpreco) }
        melhorPreco.nomeRevenda = menor["Revenda"][0] as String
        melhorPreco.cnpjRevenda = menor["CNPJRevenda"][0] as String
        melhorPreco.bandeiraRevenda = menor["Bandeira"][0] as String

        return melhorPreco
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getDadosTreinamento(siglaEstado: String, tipoCombustivel: String): List<Registro> {
        lateinit var dados: DataFrame
        if (tipoCombustivel.uppercase() == "DIESEL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDiesel.csv")
        } else if (tipoCombustivel.uppercase() == "DIESEL S10") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosDieselS10.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaComum.csv")
        } else if (tipoCombustivel.uppercase() == "GASOLINA ADITIVADA") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosGasolinaAditivada.csv")
        } else if (tipoCombustivel.uppercase() == "ETANOL") {
            dados = DataFrame.readCSV("dadosHistoricos/dadosEtanol.csv")
        }
        val registrosSelecionados = mutableListOf<Registro>()
        dados = dados.filter { it["EstadoSigla"].isEqualTo(siglaEstado) }
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

