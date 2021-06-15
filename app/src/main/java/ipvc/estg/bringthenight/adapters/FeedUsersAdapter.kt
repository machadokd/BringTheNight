package ipvc.estg.bringthenight.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.estg.bringthenight.R
import ipvc.estg.bringthenight.entities.Event

class FeedUsersAdapter internal constructor(
    var context: Context, private val events: List<Event>
) : RecyclerView.Adapter<FeedUsersAdapter.FeedUsersHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    class FeedUsersHolder(itemView: View, var evento: Event ?= null): RecyclerView.ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.descricao_evento_feed_users)
        val nome : TextView = itemView.findViewById(R.id.nome_empresa_feed_users)
        val imagem : ImageView = itemView.findViewById(R.id.imagem_evento_feed_users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedUsersHolder {
        val itemView = inflater.inflate(R.layout.linha_feed_user, parent, false)
        return FeedUsersHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedUsersHolder, position: Int) {
        val current = events[position]
        holder.titulo.text = current.titulo
        holder.nome.text = current.nome_estabelecimento
    }

    override fun getItemCount() = events.size
}