package com.tinfive.nearbyplace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinfive.nearbyplace.Model.DataMasjid
import com.tinfive.nearbyplace.Model.Results
import kotlinx.android.synthetic.main.row.view.*

class AdapterMasjid(
    private val modelList: ArrayList<Results>,
    private val listener: MapsActivity
) : RecyclerView.Adapter<AdapterMasjid.ViewHolder>() {
    interface Listener {
        fun onItemClick(model: Results)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modelList[position], listener)

    }
//Check how many items you have to display//

    override fun getItemCount(): Int = modelList.count()

//Create a ViewHolder class for your RecyclerView items//

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            model: Results,
            listener: Listener
        ) {

//Listen for user input events//

            itemView.setOnClickListener { listener.onItemClick(model) }
            itemView.titleTv.text = model.nama_masjid
            itemView.descTv.text = model.alamat

        }

    }

}
