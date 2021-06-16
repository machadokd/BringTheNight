package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Empresa(
    val id: String = "",
    val nome:String = "",
    val email: String = "",
    val tipo: String = "",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0
)
