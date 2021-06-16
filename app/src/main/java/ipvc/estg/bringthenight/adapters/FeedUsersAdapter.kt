package ipvc.estg.bringthenight.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.models.Empresa
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_criar_evento.*
import java.io.File

class FeedUsersAdapter internal constructor(
    var context: Context, private val events: List<Evento>
) : RecyclerView.Adapter<FeedUsersAdapter.FeedUsersHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    class FeedUsersHolder(itemView: View, var evento: Evento ?= null): RecyclerView.ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.descricao_evento_feed_users)
        val nome : TextView = itemView.findViewById(R.id.nome_empresa_feed_users)
        val imagem : ImageView = itemView.findViewById(R.id.imagem_evento_feed_users)
        val imagemPerfil : CircularImageView = itemView.findViewById(R.id.imagem_perfil_empresa_feed_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedUsersHolder {
        val itemView = inflater.inflate(R.layout.linha_feed_user, parent, false)
        return FeedUsersHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedUsersHolder, position: Int) {
        val current = events[position]
        holder.titulo.text = current.titulo
        holder.nome.text = current.nome_establecimento

        val storageRef = FirebaseStorage.getInstance().getReference("images/${current.estabelecimento}/${current.imagem}")
        val localFile = File.createTempFile("temp_file", "png")
        var bitmap: Bitmap? = null

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            Log.i("firebase_image", "Image $it")
            holder.imagem.setImageBitmap(bitmap!!)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }



        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        ref.child("users").child(current.estabelecimento).get().addOnSuccessListener {
            val user = it.getValue(Empresa::class.java)
            var bitmap2: Bitmap? = null
            val localFile2 = File.createTempFile("temp_file", "png")
            val storageRef2= FirebaseStorage.getInstance().getReference("images/${current.estabelecimento}/perfil/${user!!.imagem}")

            storageRef2.getFile(localFile2).addOnSuccessListener {
                bitmap2 = BitmapFactory.decodeFile(localFile2.absolutePath)
                Log.i("firebase_image", "Image $it")
                holder.imagemPerfil.setImageBitmap(bitmap2!!)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }

    override fun getItemCount() = events.size
}