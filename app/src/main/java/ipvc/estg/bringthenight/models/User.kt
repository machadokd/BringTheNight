package ipvc.estg.bringthenight.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(val id: String = "",
                val nome:String = "",
                val email: String = "",
                val tipo: String = "")