package ipvc.estg.bringthenight.models

data class Empresa(
    val email: String = "",
    val id : String = "",
    val imagem: String = "",
    val latitude: Double = 0.0,
    var longitude: Double = 0.0,
    val morada : String = "",
    val nome : String = "",
    val tipo : String = ""
)