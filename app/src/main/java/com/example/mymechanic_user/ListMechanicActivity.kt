package com.example.mymechanic_user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.LinearLayout
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng


class ListMechanicActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var arr = ArrayList<MechanicData>()
    var srcDist = 0
    var myDialog: Dialog? = null

    var i=0

    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0
    var currentLatLng: LatLng = LatLng(currentLatitude, currentLongitude)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_mechanic)

        currentLatitude = intent.getDoubleExtra("CurrentLatitude", 0.0)
        currentLongitude = intent.getDoubleExtra("CurrentLongitude", 0.0)
        srcDist = intent.getIntExtra("SearchDistance", 0)
        Toast.makeText(this, srcDist.toString(), Toast.LENGTH_SHORT).show()

        currentLatLng = LatLng(currentLatitude, currentLongitude)

        val mLinearListView = findViewById<LinearLayout>(R.id.linear_listview)
        myDialog = Dialog(this)

        db.collection("Profile").get()
            .addOnSuccessListener {
                for (dc in it) {

                    val name = dc.data["Name"].toString()
                    val email = dc.data["Email"].toString()
                    val description = dc.data["Description"].toString()
                    val phone = dc.data["Phone Number"].toString()

                    val obj = MechanicData()
                    obj.name = name
                    obj.email = email
                    obj.description = description
                    obj.phone = phone

                    val lat = dc.data["Latitude"].toString().toDouble()
                    val lng = dc.data["Longitude"].toString().toDouble()
                    val mechanicLocation = LatLng(lat, lng)

                    obj.distance = calcDistance(currentLatLng, mechanicLocation)

                    if (obj.distance <= srcDist) {
                        arr.add(obj)
                    }

                    i++
                }

                val siz = arr.size - 1

                for (x in 0..siz){

                    val inflater: LayoutInflater = applicationContext
                        .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

                    val mLinearView = inflater.inflate(R.layout.row, null)

                    val mName = mLinearView
                        .findViewById(R.id.textViewName) as TextView
                    val mPhone = mLinearView
                        .findViewById(R.id.phonenumber) as TextView
                    val mDistance = mLinearView
                        .findViewById(R.id.distance) as TextView



                    mName.text = arr[x].name
                    mPhone.text = arr[x].phone
                    mDistance.text = arr[x].distance.toString()

                    mLinearListView.addView(mLinearView)
                }


            }
        mLinearListView.setOnClickListener{
            openMapsActivity()
        }
    }


    private fun openMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("Distance", srcDist)
        startActivity(intent)
    }

}

