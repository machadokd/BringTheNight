package ipvc.estg.bringthenight

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ipvc.estg.bringthenight.EmpresaRecycler.PostAdapter
import ipvc.estg.bringthenight.EmpresaRecycler.PostDividerDecoration
import ipvc.estg.bringthenight.databinding.ActivityEventMapBinding
import ipvc.estg.bringthenight.models.Empresa
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_feed_empresas.*

class EventMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityEventMapBinding

    private lateinit var events : DatabaseReference
    private lateinit var userId : String

    private var org_events : MutableList<Evento> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)




    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        setUpMap()
//        setMapLongClick(map)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        events = FirebaseDatabase.getInstance().reference.child("events")

        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        userReference.get().addOnCompleteListener {
            val empresa = it.getResult().getValue(Empresa::class.java)
            val latLng = LatLng(empresa!!.latitude, empresa!!.longitude)

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

            events.get().addOnSuccessListener {
                it.children.forEach { child ->
                    val event = child.getValue(Evento::class.java)
                    if (event!!.estabelecimento == userId) {
                        org_events.add(event)
                    }
                }

                org_events.forEach {
                    val position = LatLng(it.latitude, it.longitude)

                    map.addMarker(MarkerOptions().position(position).title(it.titulo))
                }

            }.addOnFailureListener{
                Log.e("firebase_event", "Error getting data", it)
            }

        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val EXTRA_REPLY_LAT = "com.example.android.latitude"
        const val EXTRA_REPLY_LONG = "com.example.android.longitude"
    }
}