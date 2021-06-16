package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Evento(
    var titulo : String = "",
    val estabelecimento : String = "",
    val nome_establecimento : String = "",
    val imagem : String = "",
    val longitude : Double = 0.0,
    val latitude : Double = 0.0,
    var date : Date = Date(),
    val id : String = ""

    )
