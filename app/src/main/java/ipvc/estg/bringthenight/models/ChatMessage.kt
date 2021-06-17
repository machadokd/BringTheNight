package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatMessage(
    val message : String = "",
    val fromId : String = "",
    val toId : String = "",
    val timestamp : Long = 0,
    val id : String = "",
)
