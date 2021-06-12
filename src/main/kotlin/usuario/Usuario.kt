package usuario

import java.util.*

class Usuario(
    val email: String? = null,
    val senha: String? = null,
    val nome: String? = null,
    val id: String? = UUID.randomUUID().toString()
)

