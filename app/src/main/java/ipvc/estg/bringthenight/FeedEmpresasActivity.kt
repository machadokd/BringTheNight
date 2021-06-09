package ipvc.estg.bringthenight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_feed_empresas.*

class FeedEmpresasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_empresas)

        new_post.setOnClickListener {
            val intent = Intent(this@FeedEmpresasActivity, CriarEventoActivity::class.java)
            startActivity(intent)
        }

    }
}