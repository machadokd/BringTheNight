package ipvc.estg.bringthenight

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.models.ChatWindowActivity
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_perfil_empresa_user.*
import kotlinx.android.synthetic.main.activity_user_new_chat.*
import kotlinx.android.synthetic.main.empresa_post.view.*
import kotlinx.android.synthetic.main.new_chat_row.view.*
import kotlinx.android.synthetic.main.user_empresa_post.view.*
import java.io.File

class PerfilEmpresaUserActivity : AppCompatActivity() {

//    private val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var empresa: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_empresa_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        empresa = intent.getParcelableExtra<User>(UserNewChatActivity.USER_KEY)!!

        supportActionBar?.title = empresa.nome

        profile_image(empresa)

        send_message_floating_button.setOnClickListener {
            send_message(empresa)
        }

        list_events()

    }

    private fun profile_image(empresa: User) {
        Log.i("empresa_imagem", "profile_image: ${empresa}")
        Log.i("empresa_imagem", "images/${empresa.nome}/perfil/${empresa.imagem}")
        val storageRef = FirebaseStorage.getInstance().getReference("images/${empresa.id}/perfil/${empresa.imagem}")
        val localFile = File.createTempFile("temp_file", "png")
        var bitmap: Bitmap? = null

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
//            Log.i("firebase_image", "Image $it")
//            holder.itemView.imageView.setImageBitmap(bitmap!!)
            user_evento_profile_image.setImageBitmap(bitmap!!)
            user_evento_profile_image.rotation = 90f
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun list_events() {
        val ref = FirebaseDatabase.getInstance().getReference("events")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val evento = it.getValue(Evento::class.java)
                    if (evento != null && evento.estabelecimento == empresa.id)  {
                        adapter.add(NewPost(evento))
                    }
                }
                user_empresa_recycler.adapter = adapter
                user_empresa_recycler.layoutManager = LinearLayoutManager(this@PerfilEmpresaUserActivity)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun send_message(empresa: User?) {
        val intent = Intent(this@PerfilEmpresaUserActivity, ChatWindowActivity::class.java)
        intent.putExtra(UserNewChatActivity.USER_KEY, empresa)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}


class NewPost(val evento : Evento) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.user_evento_titulo_text_view.text = evento.titulo

        val storageRef = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
        val localFile = File.createTempFile("temp_file", "png")
        var bitmap: Bitmap? = null

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            viewHolder.itemView.user_evento_imagem.setImageBitmap(bitmap!!)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

    }

    override fun getLayout(): Int {
        return R.layout.user_empresa_post
    }

}