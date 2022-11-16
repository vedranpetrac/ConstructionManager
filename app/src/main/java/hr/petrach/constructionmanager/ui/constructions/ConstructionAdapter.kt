package hr.petrach.constructionmanager.ui.constructions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.petrach.constructionmanager.App
import hr.petrach.constructionmanager.R
import hr.petrach.constructionmanager.dao.Construction
import hr.petrach.constructionmanager.dao.ConstructionAndContractor
import hr.petrach.constructionmanager.ui.NavigableFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ConstructionAdapter (private val context: Context, private val constructionsAndContractor: MutableList<ConstructionAndContractor>, private val navigableFragment: NavigableFragment)
    : RecyclerView.Adapter<ConstructionAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        val ivDelete: ImageView = itemView.findViewById<ImageView>(R.id.ivDelete)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        private val tvContractor = itemView.findViewById<TextView>(R.id.tvCotractor)
        fun bind(constructionAndContractor: ConstructionAndContractor) {
            tvTitle.text = constructionAndContractor.construction.toString()
            tvLocation.text = "${constructionAndContractor.construction.city} - ${constructionAndContractor.construction.address}"
            if(constructionAndContractor.contractor == null)tvContractor.text = "No contractor selected"
            else tvContractor.text = constructionAndContractor.contractor.toString()
            Picasso.get()
                .load(File(constructionAndContractor.construction.picturePath))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ConstructionAdapter.ViewHolder(
        itemView = LayoutInflater.from(context).inflate(R.layout.list_constructions, parent, false)
    )

    override fun onBindViewHolder(holder: ConstructionAdapter.ViewHolder, position: Int) {
        holder.ivDelete.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
                withContext(Dispatchers.IO) { // work in IO thread
                    (context?.applicationContext as App).getConstructionDao().delete(constructionsAndContractor[position].construction)
                    File(constructionsAndContractor[position].construction.picturePath).delete()
                }
                constructionsAndContractor.removeAt(position)
                notifyDataSetChanged()
            }
        }
        holder.itemView.setOnLongClickListener {
            navigableFragment.navigate(Bundle().apply {
                putLong(CONSTRUCTION_ID, constructionsAndContractor[position].construction.constructionId!!)
            })
            true
        }
        holder.bind(constructionsAndContractor[position])
    }

    override fun getItemCount() = constructionsAndContractor.size
}