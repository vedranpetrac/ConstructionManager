package hr.petrach.constructionmanager.ui.workers

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
import hr.petrach.constructionmanager.dao.Contractor
import hr.petrach.constructionmanager.ui.NavigableFragment
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class WorkerAdapter(private val context: Context, private val contractors: MutableList<Contractor>, private val navigableFragment: NavigableFragment)
    : RecyclerView.Adapter<WorkerAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ivImage = itemView.findViewById<ImageView>(R.id.ivImage)
        val ivDelete: ImageView = itemView.findViewById<ImageView>(R.id.ivDelete)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        fun bind(contractor: Contractor) {
            tvTitle.text = contractor.toString()
            Picasso.get()
                .load(File(contractor.picturePath))
                .error(R.mipmap.ic_launcher)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(itemView = LayoutInflater.from(context).inflate(R.layout.list_workers, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivDelete.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
                withContext(Dispatchers.IO) { // work in IO thread
                    (context?.applicationContext as App).getWorkerDao().delete(contractors[position])
                    File(contractors[position].picturePath).delete()
                }
                contractors.removeAt(position)
                notifyDataSetChanged()
            }
        }
        holder.itemView.setOnLongClickListener {
            navigableFragment.navigate(Bundle().apply {
                putLong(WORKER_ID, contractors[position].contractorId!!)
            })
            true
        }
        holder.bind(contractors[position])
    }

    override fun getItemCount() = contractors.size
}