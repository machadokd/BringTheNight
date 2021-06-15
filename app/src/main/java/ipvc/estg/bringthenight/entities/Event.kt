package ipvc.estg.bringthenight.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event (
    val estabelecimento: String = "",
    val imagem: String = "",
    val titulo: String = "",
    val nome_estabelecimento: String = ""
        )