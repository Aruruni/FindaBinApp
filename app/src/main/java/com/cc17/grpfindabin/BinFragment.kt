package com.cc17.grpfindabin

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

class BinFragment : Fragment() {

    val userLocation = UserLocationManager.userLocation ?: LatLng(0.00,0.00)
    private val binsData = listOf(
        Bin("Luneta Hill Dr Road", calculateDistance(LatLng(16.410639, 120.599366)), R.drawable.bins5),
        Bin("69 Session Rd", calculateDistance(LatLng(16.412823, 120.597364)), R.drawable.bins4),
        Bin("Fr. Carlu St", calculateDistance(LatLng(16.412113, 120.599391)), R.drawable.bins1),
        Bin("30 E Gov.Pack Rd", calculateDistance(LatLng(16.410264, 120.598594)), R.drawable.bins7),
        Bin("28 E Gov.Pack Rd", calculateDistance(LatLng(16.410573, 120.598888)), R.drawable.bins6),
        Bin("38 Harrison Rd", calculateDistance(LatLng(16.412220, 120.596119)), R.drawable.bins3))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = BinAdapter(binsData)
    }

    private fun calculateDistance(binLocation: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            binLocation.latitude, binLocation.longitude,
            results
        )
        return results[0]
    }
}