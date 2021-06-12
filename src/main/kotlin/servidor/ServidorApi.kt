package servidor

import BancoDePrecos
import commoditie.combustivel.Combustivel
import commoditie.materiaprima.Petroleo
import commoditie.combustivel.local.Local
import commoditie.moeda.Dolar
import consulta.CombustivelConsulta
import preco.cotacao.Cotacao
import preco.minimo.Minimo
import consultaPrecos.cotacoes.Cotacoes
import consultaPrecos.extremos.Extremos
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*
import org.slf4j.event.Level
import usuario.Usuario
import usuario.novoUsuario.NovoUsuario

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
        usuario()
        login()

    }
}

fun Route.meuindex() {
    get("/") {
        call.respondHtml {
            body {
                h1 { +"Banco de Preços de Combustíveis e Cotações do Dólar e Barril do Petróleo" }
                p { +"Obtenha informações de preços de combustíveis por município acompanhados das cotações do Dólar e do Barril de Petróleo Brent na data desejada" }
                ul {
                    ol { +"POST - /commoditie/combustivel  - Cadastra Preço de Combustível" }
                    ol { +"POST - /commoditie/moeda        - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                  - Consultar Preços e Cotações"}
                    ol { +"GET  - /precos/estado           - Consultar Menor Preço por Estado"}
                    ol { +"POST - /commoditie/combustivel/local - Cadastra Local do Combustivel"}
                    ol { +"POST - /commoditie/combustivel       - Cadastra Preço de Combustível" }
                    ol { +"POST - /commoditie/moeda             - Cadastra Cotação do Dólar" }
                    ol { +"POST - /commoditie/materiaprima      - Cadastra Cotação do Barril de Petróleo Brent" }
                    ol { +"GET  - /precos                       - Consultar Preços e Cotações"}
                    ol { +"GET  - /precos/estado                - Consultar Menor Preço por Estado"}
                    ol { +"POST - /usuario                      - Cadastra um usuário"}
                    ol { +"POST - /login                        - Efetua login com email e senha."}

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
fun Route.usuario() {
    post("/usuario") {
        var consumidor: Usuario = call.receive<Usuario>()

        if (consumidor.email == null) {
            call.respond(HttpStatusCode.BadRequest, "O Endereço de E-mail é Indispensável para o cadastro!")
            return@post
        }
        if (consumidor.senha == null) {
            call.respond(HttpStatusCode.BadRequest, "A Senha é Indispensável para o cadastro!")
            return@post
        }
        if (consumidor.nome == null) {
            call.respond(HttpStatusCode.BadRequest, "O Nome é Indispensável para o cadastro!")
            return@post
        }

        //TODO - CHAMAR METODO CRIAR USUARIO
        call.respond(HttpStatusCode.Created)
    }
}
fun Route.login() {
    post("/login") {
        var consumidor: Usuario = call.receive<Usuario>()//Usuario ??

        if (consumidor.email == null) {
            call.respond(HttpStatusCode.BadRequest, "O E-mail é obrigatório para efetuar o Login!")
            return@post
        }
        if (consumidor.senha == null) {
            call.respond(HttpStatusCode.BadRequest, "A Senha é obrigatória para efetuar o Login!")
            return@post
        }

        //var resultado = TODO CHAMAR METODO LOGIN

        /*
        if (!resultado) {
            call.respond(HttpStatusCode.BadRequest, "Não foi possível efetuar login. Por favor verifique os dados.")
            return@post
        }
         */

        call.respond(HttpStatusCode.NoContent)
    }
}


