package servidor

import BancoDePrecos
import commoditie.combustivel.Combustivel
import commoditie.combustivel.local.Revenda
import consulta.Consulta
import consulta.minimo.ConsultaMinimo
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
import previsao.Escopo
import previsao.Previsao
import usuario.Usuario

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
        usuario()
        login()
        var localCad: Revenda = cadastraLocalCombustivel()
        cadastraPrecoCombustivel(localCad)
        consultaMediaEstado()
        consultaMenorPreco()
        treinaModelo()
        calculaPrevisao()
    }
}

fun Route.meuindex() {
    get("/") {
        call.respondHtml {
            body {
                h1 { +"Banco de Preços de Combustíveis" }
                p { +"Obtenha informações de preços de combustíveis por localidade, além de previsões com base nas cotações do Dólar e do Barril de Petróleo Brent" }
                ul {
                    ol { +"POST - /usuario                      - Cadastra um usuário"}
                    ol { +"GET  - /usuario/ativo                - Consulta usuário ativo"}
                    ol { +"POST - /login                        - Efetua login com email e senha"}
                    ol { +"POST - /commoditie/combustivel/local - Cadastra Local do Combustivel"}
                    ol { +"POST - /commoditie/combustivel       - Cadastra Preço de Combustível" }
                    ol { +"GET  - /precos/media                 - Consulta Preço Médio por Estado"}
                    ol { +"GET  - /precos/estado                - Consulta Menor Preço no Muncípio"}
                    ol { +"GET  - /precos/modelo                - Treinar Modelo de Regressão"}
                    ol { +"GET  - /precos/modelo/previsao       - Calcular Previsão do Preço do Combustível"}
                }
            }
        }
    }
}

fun Route.usuario() {
    get("/usuario/ativo") {
        val usuario = bancoprecos.usuarioAtivo
        if (usuario == null) {
            val erro = ServerError(
                HttpStatusCode.NotFound.value,
                "Usuário não encontrado. Efetue login para utilizar as funcionalidades da API.")
            call.respond(HttpStatusCode.NotFound, erro)
        } else {
            call.respond(HttpStatusCode.OK, usuario)
        }
    }

    post("/usuario") {
        var novo: Usuario = call.receive<Usuario>()

        if (novo.email == null) {
            call.respond(HttpStatusCode.BadRequest, "O Endereço de E-mail é indispensável para o cadastro!")
            return@post
        }
        if (novo.senha == null) {
            call.respond(HttpStatusCode.BadRequest, "A Senha é indispensável para o cadastro!")
            return@post
        }
        if (novo.nome == null) {
            call.respond(HttpStatusCode.BadRequest, "O Nome é indispensável para o cadastro!")
            return@post
        }
        bancoprecos.criaUsuario(novo.nome!!, novo.email!!, novo.senha!!)
        call.respond(HttpStatusCode.Created)
    }
}

fun Route.login() {
    post("/login") {
        var usuario: Usuario = call.receive<Usuario>()

        if (usuario.email == null) {
            call.respond(HttpStatusCode.BadRequest, "O E-mail é obrigatório para efetuar o Login!")
            return@post
        }
        if (usuario.senha == null) {
            call.respond(HttpStatusCode.BadRequest, "A Senha é obrigatória para efetuar o Login!")
            return@post
        }

        val resultado = bancoprecos.login(usuario.email!!, usuario.senha!!)
        if (!resultado) {
            val erro = ServerError(HttpStatusCode.BadRequest.value, "Não foi possível efetuar login. Por favor verifique os dados.")
            call.respond(HttpStatusCode.BadRequest, erro)
            return@post
        }
        call.respond(HttpStatusCode.NoContent)
    }
}

fun Route.cadastraLocalCombustivel(): Revenda {
    val novoLoc: Revenda = Revenda()
    post("/commoditie/combustivel/local"){
        val localCombustivel: Revenda = call.receive<Revenda>()
        val localCadastrado = bancoprecos.cadastraLocalCombustivel(
            localCombustivel.regiao,
            localCombustivel.siglaEstado,
            localCombustivel.municipio,
            localCombustivel.nome,
            localCombustivel.cnpj,
            localCombustivel.bandeira
        )
        novoLoc.regiao = localCadastrado.regiao
        novoLoc.siglaEstado = localCadastrado.siglaEstado
        novoLoc.municipio = localCadastrado.municipio
        novoLoc.nome = localCadastrado.nome
        novoLoc.cnpj = localCadastrado.cnpj
        novoLoc.bandeira = localCadastrado.bandeira
        call.respond(localCadastrado)
    }
    return novoLoc
}

fun Route.cadastraPrecoCombustivel(localCad: Revenda) {
    post("/commoditie/combustivel"){
        val precoCombustivel: Combustivel = call.receive<Combustivel>()
        precoCombustivel.local.regiao = localCad.regiao
        precoCombustivel.local.siglaEstado = localCad.siglaEstado
        precoCombustivel.local.municipio = localCad.municipio
        precoCombustivel.local.nome = localCad.nome
        precoCombustivel.local.cnpj = localCad.cnpj
        precoCombustivel.local.bandeira = localCad.bandeira
        val precoCadastrado = bancoprecos.cadastraPrecoCombustivel(
            precoCombustivel.tipo,
            precoCombustivel.data,
            precoCombustivel.valor,
            precoCombustivel.local
        )
        call.respond(precoCadastrado)
    }
}

fun Route.consultaMediaEstado() {
    get("/precos/media") {
        val consulta = call.receive<Consulta>()
        val consultaRealizada = bancoprecos.consultaMediaEstado(
            consulta.tipoCombustivel!!,
            consulta.data!!,
            consulta.UF!!)
        call.respond(consultaRealizada)
    }
}

fun Route.consultaMenorPreco() {
    get("/precos/menor") {
        val consulta = call.receive<ConsultaMinimo>()
        val menorPreco = bancoprecos.consultaMenorPreco(
            consulta.tipoCombustivel!!, consulta.data!!, consulta.municipio!!, consulta.UF!!)
        call.respond(menorPreco)
    }
}

fun Route.treinaModelo() {
    get("/precos/modelo"){
        val escopoDesejado = call.receive<Escopo>()
        val registros = bancoprecos.getDadosTreinamento(escopoDesejado.siglaEstado, escopoDesejado.tipoCombustivel)
        val modeloTreinado = bancoprecos.treinaModelo(escopoDesejado.siglaEstado, escopoDesejado.tipoCombustivel, registros)
        call.respond(modeloTreinado)
    }
}

fun Route.calculaPrevisao() {
    get("/precos/modelo/previsao"){
        val previsaoDesejada = call.receive<Previsao>()
        val precoPrevisto = bancoprecos.calculaPrevisao(
            previsaoDesejada.siglaEstado, previsaoDesejada.tipoCombustivel,
            previsaoDesejada.valorPetroleo, previsaoDesejada.valorDolar)
        call.respond(precoPrevisto)
    }
}

