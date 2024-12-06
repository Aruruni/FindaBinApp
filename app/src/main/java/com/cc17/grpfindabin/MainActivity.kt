package com.cc17.grpfindabin
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.provider.Settings
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null
    private var binFragment: Fragment? = null
    private var currentUserLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.map_fragment, mapFragment!!, "MAP_FRAGMENT")
                .commit()
        }
        mapFragment?.getMapAsync(this)
        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).apply {
            itemIconTintList = null // Disable icon tinting to show original colors
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.maps -> showMapFragment()
                    R.id.bins -> showBinFragment()
                }
                true
            }
        }
        requestLocationPermission()
    }

    private fun fetchCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentUserLocation = LatLng(location.latitude, location.longitude)
                UserLocationManager.userLocation = LatLng(location.latitude, location.longitude)
                mapFragment?.getMapAsync(this)
            } else {
                Toast.makeText(this, "Failed to get current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {

            onPermissionGranted()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    private fun onPermissionGranted() {
        // Provide feedback to the user
        Toast.makeText(this, "Permission granted. Accessing location...", Toast.LENGTH_SHORT).show()
        fetchCurrentLocation()
    }

    private fun onPermissionDenied() {
        // Provide clear feedback and guide the user
        Toast.makeText(
            this,
            "Permission denied. Location features won't be available.",
            Toast.LENGTH_LONG
        ).show()

        // Optionally show a dialog explaining why the permission is important
        showPermissionDeniedDialog()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app requires location access to show nearby bins. Please grant the permission in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                // Open app settings to allow the user to grant the permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showMapFragment() {
        supportFragmentManager.beginTransaction().apply {
            mapFragment?.let { show(it) }
            binFragment?.let { hide(it) }
            commit()
        }
    }

    private fun showBinFragment() {
            binFragment = BinFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.map_fragment, binFragment!!, "BIN_FRAGMENT")
                .commit()
            supportFragmentManager.beginTransaction().apply {
                binFragment?.let { show(it) }
                mapFragment?.let { hide(it) }
                commit()

        }
    }


    override fun onMapReady(map: GoogleMap?) {


        map?.apply {

            setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MainActivity, R.raw.map_style))
            val userLocation = currentUserLocation ?: LatLng(0.0,0.0)
            moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))

            val userMarker = MarkerOptions()
                .position(userLocation)
                .title("You")
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(R.drawable.location)))
            addMarker(userMarker)

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
        "Luneta Hill Dr Road" to "Bio/Non-Bio/Recycle",
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
        distanceView.text = "Distance: ${distance.toInt()} meters"

        return view
    }

}

