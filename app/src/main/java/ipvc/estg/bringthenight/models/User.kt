package ipvc.estg.bringthenight.models

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class User(
    val id: String = "",
    val nome:String = "",
    val email: String = "",
    val tipo: String = "",
    val imagem: String = ""
) : Parcelable