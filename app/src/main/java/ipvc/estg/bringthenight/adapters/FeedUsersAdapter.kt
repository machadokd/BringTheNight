package ipvc.estg.bringthenight.adapters

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.models.Empresa
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_criar_evento.*
import java.io.*

class FeedUsersAdapter internal constructor(
    var context: Context, private val events: List<Evento>, private val user: FirebaseUser
) : RecyclerView.Adapter<FeedUsersAdapter.FeedUsersHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    class FeedUsersHolder(itemView: View, var evento: Evento ?= null): RecyclerView.ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.descricao_evento_feed_users)
        val nome : TextView = itemView.findViewById(R.id.nome_empresa_feed_users)
        val imagem : ImageView = itemView.findViewById(R.id.imagem_evento_feed_users)
        val imagemPerfil : CircularImageView = itemView.findViewById(R.id.imagem_perfil_empresa_feed_users)
        val like : ImageView= itemView.findViewById(R.id.likes_evento_feed_user)
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
        holder.like.setOnClickListener {
            daLike(current.id, holder.like)
        }

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            Log.i("firebase_image", "Image $it")
            holder.imagem.setImageBitmap(bitmap!!)
            Log.i("file_download_recycler", "${ holder.imagem }")
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }



        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        ref.child("events").child(current.id).get().addOnSuccessListener {
            if (it.hasChild("gostos")){
                if(it.child("gostos").hasChild(user.uid)){
                    holder.like.setBackgroundResource(R.drawable.ic_liked)
                }
            }
        }

        ref.child("users").child(current.estabelecimento).get().addOnSuccessListener {
            val user = it.getValue(Empresa::class.java)
            var bitmap2: Bitmap? = null
            val localFile2 = File.createTempFile("temp_file", "png")
            val storageRef2= FirebaseStorage.getInstance().getReference("images/${current.estabelecimento}/perfil/${user!!.imagem}")


            storageRef2.getFile(localFile2).addOnSuccessListener {
                bitmap2 = BitmapFactory.decodeFile(localFile2.absolutePath)

                val file : File = convertBitmapToFile("file", bitmap2!!)
                Picasso.get().load(file).rotate(90f).into(holder.imagemPerfil);
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }


    }

    override fun getItemCount() = events.size

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun daLike(id: String, like: ImageView){
        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        ref.child("events").child(id).get().addOnSuccessListener {
            Log.e("eventos", "${it.value}")
            Log.e("eventos", user.uid)
            if(it.hasChild("gostos")){
                Log.e("eventos", "existe")
            }else{

                var hashMap : HashMap<String, Any> = HashMap()

                hashMap.put("id", user.uid)

                val ref : DatabaseReference =  FirebaseDatabase.getInstance().getReference().child("events").child(id).child("gostos").child(user.uid)

                ref.setValue(hashMap).addOnCompleteListener{ task->
                    if(task.isSuccessful){
                        Log.e("eventos", "sucesso")

                    } else{
                        Log.e("eventos", "insucesso")
                    }
                }
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }


}