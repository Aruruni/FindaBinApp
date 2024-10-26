package com.cc17.grpfindabin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
class BinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView1 = view.findViewById<CardView>(R.id.card1)
        cardView1.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }
        val cardView2 = view.findViewById<CardView>(R.id.card2)
        cardView2.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }
        val cardView3 = view.findViewById<CardView>(R.id.card3)
        cardView3.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }
        val cardView4 = view.findViewById<CardView>(R.id.card4)
        cardView4.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }
        val cardView5 = view.findViewById<CardView>(R.id.card5)
        cardView5.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }
        val cardView6 = view.findViewById<CardView>(R.id.card6)
        cardView6.setOnClickListener {
            val expandedLayout = it.findViewById<View>(R.id.expandedLayout)
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
            } else {
                expandedLayout.visibility = View.VISIBLE
            }
        }

    }
}