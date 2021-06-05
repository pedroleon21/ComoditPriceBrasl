package servidor

import BancoDePrecos
import commoditie.combustivel.Combustivel
import commoditie.materiaprima.Petroleo
import commoditie.combustivel.local.Local
import commoditie.moeda.Dolar
import consulta.CombustivelConsulta
<<<<<<< HEAD
import preco.cotacao.Cotacao
import preco.minimo.Minimo
=======
import consultaPrecos.cotacoes.Cotacoes
import consultaPrecos.extremos.Extremos
>>>>>>> Luciano
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.slf4j.event.Level

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
    }
}

fun Route.meuindex() {
    get("/") {
        call.respondHtml {
            body {
                h1 { +"Banco de Preços de Combustíveis e Cotações do Dólar e Barril do Petróleo" }
                p { +"Obtenha informações de preços de combustíveis por município acompanhados das cotações do Dólar e do Barril de Petróleo Brent na data desejada" }
                ul {
<<<<<<< HEAD
                    ol { +"POST - /commoditie/combustivel  - Cadastra Preço de Combustível" }
                    ol { +"POST - /commoditie/moeda        - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                  - Consultar Preços e Cotações"}
                    ol { +"GET  - /precos/estado           - Consultar Menor Preço por Estado"}
=======
                    ol { +"POST - /commoditie/combustivel/local - Cadastra Local do Combustivel"}
                    ol { +"POST - /commoditie/combustivel       - Cadastra Preço de Combustível" }
                    ol { +"POST - /commoditie/moeda             - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima      - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                       - Consultar Preços e Cotações"}
                    ol { +"GET  - /precos/estado                - Consultar Menor Preço por Estado"}
>>>>>>> Luciano
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
        val cotacaoCadastrada = bancoprecos.cadastraCotacaoDolar(cotacaoDolar.data)
        call.respond(cotacaoCadastrada)
    }
}

fun Route.cadastraCotacaoPetroleo() {
    post("/commoditie/materiaprima") {
        val cotacaoPetroleo: Petroleo = call.receive<Petroleo>()
        val cotacaoCadastrada = bancoprecos.cadastraCotacaoPetroleo(cotacaoPetroleo.data)
        call.respond(cotacaoCadastrada)
    }
}

fun Route.consultaPrecos() {
    get("/precos") {
<<<<<<< HEAD
        var consulta: Cotacao = call.receive<Cotacao>()
=======
        var consulta: Cotacoes = call.receive<Cotacoes>()
>>>>>>> Luciano
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
<<<<<<< HEAD
        var consulta: Minimo = call.receive<Minimo>()
        var menorPreco = bancoprecos.consultaMenorPreco(consulta.data,
            consulta.tipoCombustivel, consulta.UF)
=======
        var rankingEstado: Extremos = call.receive<Extremos>()
        var menorPreco = bancoprecos.rankingPrecos(rankingEstado.data,
            rankingEstado.tipoCombustivel, rankingEstado.UF)
>>>>>>> Luciano
        call.respond(menorPreco)
    }
}