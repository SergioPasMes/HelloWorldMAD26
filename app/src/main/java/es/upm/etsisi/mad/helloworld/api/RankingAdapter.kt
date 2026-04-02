package es.upm.etsisi.mad.helloworld.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.upm.etsisi.mad.helloworld.data.ProfilePictureManager
import es.upm.etsisi.mad.helloworld.R
import es.upm.etsisi.mad.helloworld.data.User

class RankingAdapter(private val userList: List<User>) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    // This class finds the views inside your CardView layout
    class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.rankingImage)
        val alias: TextView = view.findViewById(R.id.rankingAlias)
        val score: TextView = view.findViewById(R.id.rankingScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.row_user_ranking, parent, false)
        return RankingViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val user = userList[position]
        holder.alias.text = user.alias
        val context = holder.itemView.context
        holder.score.text = context.getString(R.string.score, user.score)

        val imageName = user.profile_picture!!
        val bitmap = ProfilePictureManager.getImage(context, imageName)
        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap.toBitmap())
        }
    }

    override fun getItemCount(): Int = userList.size
}