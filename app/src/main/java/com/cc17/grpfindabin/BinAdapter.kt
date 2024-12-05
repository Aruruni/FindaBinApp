package com.cc17.grpfindabin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

data class Bin(val streetName: String, val distance: Float, val imageResId: Int)


class BinAdapter(private val bins: List<Bin>) : RecyclerView.Adapter<BinAdapter.BinViewHolder>() {

    inner class BinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binImage: ImageView = view.findViewById(R.id.binImage)
        val binInfo: TextView = view.findViewById(R.id.binInfo)
        val binInfo2: TextView = view.findViewById(R.id.binInfo2)
        val card: CardView = view.findViewById(R.id.binCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bin_item, parent, false)
        return BinViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        val bin = bins[position]
        holder.binInfo.text = "${bin.streetName}"
        holder.binInfo2.text = "${bin.distance.toInt()} meters away"
        holder.binImage.setImageResource(bin.imageResId)
        holder.card.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = bins.size
}
