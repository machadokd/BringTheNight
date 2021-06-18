package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@IgnoreExtraProperties
data class Evento(
    var titulo: String = "",
    val estabelecimento: String = "",
    val nome_establecimento: String = "",
    val imagem: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    var date: Date = Date(),
    val id: String = "",
    var gostos: Int = 0
    )
