package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import ipvc.estg.bringthenight.EmpresaRecycler.PostAdapter
import ipvc.estg.bringthenight.EmpresaRecycler.PostDividerDecoration
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_feed_empresas.*
import java.security.AccessController.getContext
import java.util.zip.Inflater

class FeedEmpresasActivity : AppCompatActivity() {

    private lateinit var events : DatabaseReference
    private lateinit var userId : String

//    EVENT LIST
    private var org_events : MutableList<Evento> = ArrayList()

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_empresas)

        new_post.setOnClickListener {
            val intent = Intent(this@FeedEmpresasActivity, CriarEventoActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        events = FirebaseDatabase.getInstance().reference.child("events")


        events.get().addOnSuccessListener {
            it.children.forEach { child ->
                val event = child.getValue(Evento::class.java)
                if (event!!.estabelecimento == userId) {
                    org_events.add(event)
                }
            }

            postAdapter = PostAdapter(this@FeedEmpresasActivity, org_events)
            var recyclerView = recycler

            recyclerView.apply {
                adapter = postAdapter
                layoutManager = LinearLayoutManager(this@FeedEmpresasActivity)
                val decoration = PostDividerDecoration(30)
                addItemDecoration(decoration)
            }

        }.addOnFailureListener{
            Log.e("firebase_event", "Error getting data", it)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.empresa_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            logout()
            true
        }

        R.id.map -> {
            toEventMapActivity()
            true
        }

        R.id.chat -> {
            toChatEmpresaActivity()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun toChatEmpresaActivity() {
        val intent = Intent(this@FeedEmpresasActivity, ChatEmpresaActivity::class.java)
        startActivity(intent)
    }

    private fun toEventMapActivity() {
        val intent = Intent(this@FeedEmpresasActivity, EventMapActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@FeedEmpresasActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}