package ipvc.estg.bringthenight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem

class ChatEmpresaActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_empresa)


        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_logout)
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.i("action_bar", "Action bar ${supportActionBar}")


    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}