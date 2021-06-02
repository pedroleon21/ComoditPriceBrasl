package servidor

import BancoDePrecos
import commoditie.combustivel.Combustivel
import commoditie.materiaprima.Petroleo
import commoditie.combustivel.local.Local
import commoditie.moeda.Dolar
import consulta.CombustivelConsulta
import consultaPrecos.cotacoes.Cotacoes
import consultaPrecos.extremos.Extremos
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.slf4j.event.Level
import regressao.RegressaoLinear

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val bancoprecos = BancoDePrecos()

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.bancoprecos(testing: Boolean = false) {

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }

    routing {
        meuindex()
        var localCad: Local = cadastraLocalCombustivel()
        cadastraPrecoCombustivel(localCad)
        cadastraCotacaoDolar()
        cadastraCotacaoPetroleo()
        consultaPrecos()
        consultaPrecoCombustiveis()
        consultaPrecoEstado()
        treinaModelo()
        calculaPrevisao()
    }
}

fun Route.meuindex() {
    get("/") {
        call.respondHtml {
            body {
                h1 { +"Banco de Preços de Combustíveis e Cotações do Dólar e Barril do Petróleo" }
                p { +"Obtenha informações de preços de combustíveis por município acompanhados das cotações do Dólar e do Barril de Petróleo Brent na data desejada" }
                ul {
                    ol { +"POST - /commoditie/combustivel/local - Cadastra Local do Combustivel"}
                    ol { +"POST - /commoditie/combustivel       - Cadastra Preço de Combustível" }
                    ol { +"POST - /commoditie/moeda             - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima      - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                       - Consultar Preços e Cotações"}
                    ol { +"GET  - /precos/estado                - Consultar Menor Preço por Estado"}
                    ol { +"GET  - /precos/modelo                - Treinar Modelo de Regressão"}
                    ol { +"GET  - /precos/modelo/previsao       - Calcular Previsão do Preço do Combustível"}
                }
            }
        }
    }
}

fun Route.cadastraLocalCombustivel(): Local {
    var novoLoc: Local = Local()
    post("/commoditie/combustivel/local"){
        val localCombustivel: Local = call.receive<Local>()
        val localCadastrado = bancoprecos.cadastraLocalCombustivel(
            localCombustivel.municipio,
            localCombustivel.regiao,
            localCombustivel.uf,
            localCombustivel.qtdPostos
        )
        novoLoc.municipio = localCadastrado.municipio
        novoLoc.regiao = localCadastrado.regiao
        novoLoc.uf = localCadastrado.uf
        novoLoc.qtdPostos = localCadastrado.qtdPostos
        call.respond(localCadastrado)
    }
    return novoLoc
}

fun Route.cadastraPrecoCombustivel(localCad: Local) {
    post("/commoditie/combustivel"){
        val precoCombustivel: Combustivel = call.receive<Combustivel>()
        precoCombustivel.local!!.municipio = localCad.municipio
        precoCombustivel.local!!.regiao = localCad.regiao
        precoCombustivel.local!!.uf = localCad.uf
        precoCombustivel.local!!.qtdPostos = localCad.qtdPostos
        val precoCadastrado = bancoprecos.cadastraPrecoCombustivel(
            precoCombustivel.tipo,
            precoCombustivel.data,
            precoCombustivel.valor,
            precoCombustivel.local
        )
        call.respond(precoCadastrado)
    }
}

fun Route.cadastraCotacaoDolar() {
    post("/commoditie/moeda"){
        val cotacaoDolar: Dolar = call.receive<Dolar>()
        val cotacaoCadastrada = bancoprecos.cadastraCotacaoDolar(cotacaoDolar.data, cotacaoDolar.valor)
        call.respond(cotacaoDolar)
    }
}

fun Route.cadastraCotacaoPetroleo() {
    post("/commoditie/materiaprima") {
        val cotacaoPetroleo: Petroleo = call.receive<Petroleo>()
        val cotacaoCadastrada = bancoprecos.cadastraCotacaoPetroleo(cotacaoPetroleo.data,
            cotacaoPetroleo.valor)
        call.respond(cotacaoCadastrada)
    }
}

fun Route.consultaPrecos() {
    get("/precos") {
        var consulta: Cotacoes = call.receive<Cotacoes>()
        var consultaRealizada = bancoprecos.consultaPrecos(consulta.data,
            consulta.tipoCombustivel,
            consulta.municipio,
            consulta.UF)
        call.respond(consultaRealizada)
    }
}

fun Route.consultaPrecoCombustiveis(){
    get("/preco/combustiveis"){
        var consulta = CombustivelConsulta()
        var listaCombutiveis = consulta.getAllPrecos()
        call.respond(listaCombutiveis)
    }
}

fun Route.consultaPrecoEstado(){
    get("/precos/estado") {
        var rankingEstado: Extremos = call.receive<Extremos>()
        var menorPreco = bancoprecos.rankingPrecos(rankingEstado.data,
            rankingEstado.tipoCombustivel, rankingEstado.UF)
        call.respond(menorPreco)
    }
}

fun Route.treinaModelo() {
    get("/precos/modelo"){
        val siglaEstado = call.receive<String>()
        val municipio = call.receive<String>()
        val tipoCombustivel = call.receive<String>()
        val registros = bancoprecos.getDadosTreinamento(siglaEstado,municipio,tipoCombustivel)
        val modeloTreinado = bancoprecos.treinaModelo(registros)
        call.respond(modeloTreinado)
    }
}

fun Route.calculaPrevisao() {
    get("/precos/modelo/previsao"){
        val modeloTreinado = call.receive<RegressaoLinear>()
        val valorPetroleo = call.receive<Float>()
        val valorDolar = call.receive<Float>()
        val precoPrevisto = bancoprecos.calculaPrevisao(modeloTreinado, valorPetroleo, valorDolar)
        call.respond(precoPrevisto)
    }
}
