package com.cc17.grpfindabin
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapFragment: SupportMapFragment? = null
    private var binFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.map_fragment, mapFragment!!, "MAP_FRAGMENT")
                .commit()
        }
        mapFragment?.getMapAsync(this)

        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.maps -> showMapFragment()
                R.id.bins -> showBinFragment()
            }
            true
        }
    }

    private fun showMapFragment() {
        supportFragmentManager.beginTransaction().apply {
            mapFragment?.let { show(it) }
            binFragment?.let { hide(it) }
            commit()
        }
    }

    private fun showBinFragment() {
        if (binFragment == null) {
            binFragment = BinFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.map_fragment, binFragment!!, "BIN_FRAGMENT")
                .commit()
        } else {
            supportFragmentManager.beginTransaction().apply {
                binFragment?.let { show(it) }
                mapFragment?.let { hide(it) }
                commit()
            }
        }
    }


    override fun onMapReady(map: GoogleMap?) {


        map?.apply {

            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.map_style))
            val userLocation = LatLng(16.408565, 120.597990)
            moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))

            val userMarker = MarkerOptions()
            userMarker.position(userLocation)
            userMarker.title("You")
            addMarker(createMarker(userLocation, "You", R.drawable.location))
            userMarker.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.location)))

            map.setInfoWindowAdapter(CustomInfoWindowAdapter(this@MainActivity, userLocation))


            val binLocations = listOf(
                Triple(LatLng(16.410638992141795, 120.59936636067498), "Luneta Hill Dr Road", R.drawable.bin),
                Triple(LatLng(16.4128232824794, 120.59736378059569), "69 Session Rd", R.drawable.bin),
                Triple(LatLng(16.41211314233684, 120.59939071798077), "Fr. Carlu St", R.drawable.bin),
                Triple(LatLng(16.410264428234818, 120.5985941527818), "30 E Gov.Pack Rd", R.drawable.bin),
                Triple(LatLng(16.410572619698453, 120.59888826627547), "28 E Gov.Pack Rd", R.drawable.bin),
                Triple(LatLng(16.41222011440656, 120.59611901633659), "38 Harrison Rd", R.drawable.bin)
            )
            binLocations.forEach { (location, title, iconRes) ->
                addMarker(createMarker(location, title, iconRes))
            }
        }
    }
    private fun createMarker(position: LatLng, title: String, iconRes: Int): MarkerOptions {
        return MarkerOptions()
            .position(position)
            .title(title)
            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(iconRes)))
    }

    private fun getBitmapFromDrawable(resId:  Int): Bitmap?{
        var bitmap: Bitmap? = null
        val drawable = ResourcesCompat.getDrawable(resources, resId, null)
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(150, 150 , Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0,0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap

    }

}

class CustomInfoWindowAdapter(private val context: Context, private val userLocation: LatLng) : GoogleMap.InfoWindowAdapter {
    private val binTypes = mapOf(
        "Luneta Dr. Hill Road" to "Bio/Non-Bio/Recycle",
        "69 Session Rd" to "Bio/Non-Bio/Recycle",
        "Fr. Carlu St" to "Bio/Non-Bio/Recycle",
        "30 E Gov.Pack Rd" to "Bio/Recycle/Others",
        "28 E Gov.Pack Rd" to "Bio/Non-Bio/Recycle",
        "38 Harrison Rd" to "Non-Bio/Bio"
    )

    fun calculateDistance(start: LatLng, end: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        return results[0]
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View? {

        if (marker.title == "You") {
            return null
        }
        val view = LayoutInflater.from(context).inflate(R.layout.info_window, null)

        val imageView = view.findViewById<ImageView>(R.id.image)
        val titleView = view.findViewById<TextView>(R.id.info_title)
        val distanceView = view.findViewById<TextView>(R.id.info_dist)
        val binTypeView = view.findViewById<TextView>(R.id.bin_type)

        titleView.text = marker.title

        val binType = binTypes[marker.title] ?: "Unknown"

        binTypeView.text = "Type: $binType"

        when (marker.title) {
            "Luneta Hill Dr Road" -> imageView.setImageResource(R.drawable.bins5)
            "69 Session Rd" -> imageView.setImageResource(R.drawable.bins4)
            "Fr. Carlu St" -> imageView.setImageResource(R.drawable.bins1)
            "30 E Gov.Pack Rd" -> imageView.setImageResource(R.drawable.bins7)
            "28 E Gov.Pack Rd" -> imageView.setImageResource(R.drawable.bins6)
            "38 Harrison Rd" -> imageView.setImageResource(R.drawable.bins3)
            else -> imageView.setImageResource(R.drawable.trash_bin_3)
        }

        val markerLocation = marker.position
        val distance = calculateDistance(userLocation, markerLocation)
        distanceView.text = String.format("Distance: %.2f meters", distance)

        return view
    }
}

