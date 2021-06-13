package usuario

import java.util.*

class Usuario(
    val id: String? = UUID.randomUUID().toString(),
    val email: String? = null,
    val senha: String? = null,
    val nome: String? = null
)