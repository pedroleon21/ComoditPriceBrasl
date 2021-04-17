package servidor

import BancoDePrecos
import consulta.Consulta
import commoditie.combustivel.Combustivel
import commoditie.materiaprima.Petroleo
import commoditie.moeda.Dolar
import commoditie.combustivel.local.Local
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
        cadastraLocalCombustivel()
        cadastraPrecoCombustivel()
        cadastraCotacaoDolar()
        cadastraCotacaoPetroleo()
        consultaPrecos()
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
                    ol { +"POST - /commoditie/combustivel       - Cadastra Preço do Combustível" }
                    ol { +"POST - /commoditie/moeda             - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima      - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                       - Consultar Preços e Cotações"}
                }
            }
        }
    }
}

fun Route.cadastraLocalCombustivel(){
    post("/commoditie/combustivel/local"){
        val localCombustivel: Local = call.receive<Local>()
        val localCadastrado = bancoprecos.cadastraLocalCombustivel(
            localCombustivel.municipio,
            localCombustivel.regiao,
            localCombustivel.uf,
            localCombustivel.qtdPostos
        )
        call.respond(localCadastrado)
    }
}



fun Route.cadastraPrecoCombustivel(){
    post("/commoditie/combustivel"){
        val precoCombustivel: Combustivel = call.receive<Combustivel>()
        val precoCadastrado = bancoprecos.cadastraPrecoCombustivel(
            precoCombustivel.tipo,
            precoCombustivel.data,
            precoCombustivel.valor)
        call.respond(precoCombustivel)
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
        var consulta: Consulta = call.receive<Consulta>()
        var consultaRealizada = bancoprecos.consultaPrecos(consulta.data,
            consulta.tipoCombustivel,
            consulta.municipio,
            consulta.UF)
        call.respond(consultaRealizada)
    }
}