package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ipvc.estg.bringthenight.adapters.FeedUsersAdapter
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_feed_users.*

class FeedUsersActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_users)

        readEventos()

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
//            toChatEmpresaActivity()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@FeedUsersActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun readEventos(){
        database = FirebaseDatabase.getInstance().getReference("events")

        val eventos : MutableList<Evento> = ArrayList()
        eventos.clear()

        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Machado", "Deu erro a ir buscar os dados")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val children = p0!!.children
                children.forEach{
                    eventos.add(it.getValue(Evento::class.java)!!)
                }

                RecyclerFeedUser.apply{
                    layoutManager = LinearLayoutManager(this@FeedUsersActivity)
                    adapter = FeedUsersAdapter(this@FeedUsersActivity, eventos as ArrayList<Evento>)
                }


            }

        }
        database.addValueEventListener(postListener)
    }
}