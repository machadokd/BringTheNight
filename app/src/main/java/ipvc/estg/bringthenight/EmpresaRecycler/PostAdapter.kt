package ipvc.estg.bringthenight.EmpresaRecycler

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.EditarEventoActivity
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.empresa_post.view.*
import java.io.File

class PostAdapter(var context : Context, private val events: List<Evento>) : RecyclerView.Adapter<PostAdapter.EventoViewHolder>() {

    class EventoViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

    }

    private var eventList = emptyList<Evento>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        return EventoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.empresa_post, parent, false))
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val currentItem = events[position]

        val storageRef = FirebaseStorage.getInstance().getReference("images/${currentItem.estabelecimento}/${currentItem.imagem}")
        val localFile = File.createTempFile("temp_file", "png")
        var bitmap: Bitmap? = null

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            Log.i("firebase_image", "Image $it")
            holder.itemView.imageView.setImageBitmap(bitmap!!)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        Log.i("firebase_image", "Item $currentItem")

        holder.itemView.title.text = currentItem.titulo

        holder.itemView.imageButton.setOnClickListener {
            val intent = Intent(context, EditarEventoActivity::class.java)
            intent.putExtra("evento", currentItem.id)
            context.startActivity(intent)

        }

    }


    fun setData(events : List<Evento>){
        this.eventList = events
        notifyDataSetChanged()
    }


}