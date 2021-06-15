package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Evento(
    val titulo : String = "",
    val estabelecimento : String = "",
    val nome_establecimento : String = "",
    val imagem : String = "",
    val longitude : Double = 0.0,
    val latitude : Double = 0.0,
    val date : Date = Date()

    )
