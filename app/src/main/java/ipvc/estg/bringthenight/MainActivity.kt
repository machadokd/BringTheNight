package ipvc.estg.bringthenight

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ipvc.estg.bringthenight.models.User

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //

        auth = FirebaseAuth.getInstance()

        val button = findViewById<Button>(R.id.button_login)
        button.setOnClickListener {
            startSignIn()
        }

        val registo = findViewById<Button>(R.id.button_ir_registo)
        registo.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun startSignIn() {
        val email = findViewById<EditText>(R.id.email_login).text.toString()
        val password = findViewById<EditText>(R.id.pass_login).text.toString()
        val users : MutableList<User> = ArrayList()

        var user: User

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(baseContext, "Authentication success.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    // updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }

        database = FirebaseDatabase.getInstance().getReference("users")
        userid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val postListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Machado", "Deu erro a ir buscar os dados")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val children = p0!!.children
                children.forEach{
                    users.add(it.getValue(User::class.java)!!)
                }

                users.forEach {
                    if(it.id == auth.currentUser?.uid){
                        if(it.tipo == "empresa"){
                            val intent = Intent(this@MainActivity, FeedEmpresasActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (it.tipo == "user"){
                            val intent = Intent(this@MainActivity, FeedUsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

            }

        }
        database.addValueEventListener(postListener)
    }

    companion object {
        private const val TAG = "CustomAuthActivity"
    }

}