package usuario.novoUsuario

import usuario.Usuario

class NovoUsuario {
    var usuarioAtual: Usuario? = null
        private set

    private val usuarios = mutableListOf<Usuario>()

    fun criarUsuario(nome: String, email: String, senha: String) {
        val cliente = Usuario(nome = nome, email = email, senha = senha)
        usuarios.add(cliente)
    }
    fun login(email: String, senha: String): Boolean {
        usuarioAtual = usuarios.firstOrNull { usuario -> usuario.email == email && usuario.senha == senha }
        return usuarioAtual != null
    }

}