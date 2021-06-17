package ipvc.estg.bringthenight

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipvc.estg.bringthenight.adapters.FeedUsersAdapter
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_feed_users.*
import kotlinx.android.synthetic.main.activity_perfil_empresa_user.*
import kotlinx.android.synthetic.main.linha_feed_user.view.*
import java.io.File

class FeedUsersActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

//    private val adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_users)

        readEventos()

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            logout()
            true
        }

//        R.id.map -> {
//            toEventMapActivity()
//            true
//        }
//
        R.id.chat -> {
            toChatUserActivity()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun toChatUserActivity() {
        val intent = Intent(this@FeedUsersActivity, ChatUserActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@FeedUsersActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun readEventos(){
        database = FirebaseDatabase.getInstance().getReference("events")


        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val evento = it.getValue(Evento::class.java)
                    if (evento != null )  {
                        val refUser = FirebaseDatabase.getInstance().getReference("users/${evento.estabelecimento}")
                        refUser.get().addOnCompleteListener {
                            val estabelecimento = it.result.getValue(User::class.java)
                            adapter.add(FeedUsersEvento(evento, estabelecimento!!, this@FeedUsersActivity))

                            adapter.setOnItemClickListener { item, view ->
                                Log.i("evento_button", "View : ${view.tag}")
                            }

                        }.addOnCanceledListener {  }

                    }
                }
                RecyclerFeedUser.adapter = adapter
                RecyclerFeedUser.layoutManager = LinearLayoutManager(this@FeedUsersActivity)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


//        val eventos : MutableList<Evento> = ArrayList()
//        eventos.clear()
//        Log.e("ERRO", "ERRO")
//
//        val postListener = object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                Log.d("Machado", "Deu erro a ir buscar os dados")
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                val children = p0!!.children
//                children.forEach{
//                    eventos.add(it.getValue(Evento::class.java)!!)
//                }
//                RecyclerFeedUser.apply{
//                    var user = auth.currentUser
//                    layoutManager = LinearLayoutManager(this@FeedUsersActivity)
//
//                    adapter = FeedUsersAdapter(this@FeedUsersActivity, eventos as ArrayList<Evento>, user as FirebaseUser)
//                }
//
//
//            }
//
//        }
//        database.addValueEventListener(postListener)
    }
}

class FeedUsersEvento(val evento : Evento, val empresa : User, val context: Context) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.descricao_evento_feed_users.text = evento.titulo
        viewHolder.itemView.nome_empresa_feed_users.text = empresa.nome

        val storageRef = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
        val localFile = File.createTempFile("temp_file", "png")
        var bitmap: Bitmap? = null

        storageRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            viewHolder.itemView.imagem_evento_feed_users.setImageBitmap(bitmap!!)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        val storageRefPerfil = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
        val localFilePerfil = File.createTempFile("temp_file", "png")
        var bitmapPerfil: Bitmap? = null

        storageRefPerfil.getFile(localFilePerfil).addOnSuccessListener {
            bitmapPerfil = BitmapFactory.decodeFile(localFilePerfil.absolutePath)
            Log.i("bitmapPerfil", "bitmapPerfil ${bitmapPerfil}")
            viewHolder.itemView.imagem_perfil_empresa_feed_users.setImageBitmap(bitmapPerfil!!)
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }


        viewHolder.itemView.ver_mais_evento_feed_users.setOnClickListener {
            val intent = Intent(context, VerMaisEventoActivity::class.java)
            intent.putExtra("evento", evento.id)

            context.startActivity(intent)

//            Log.i("evento_button", "Evento ${evento}")

        }


    }

    override fun getLayout(): Int {
        return R.layout.linha_feed_user
    }

}