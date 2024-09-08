package com.trungvothanh.weatherapplication.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trungvothanh.weatherapplication.R
import com.trungvothanh.weatherapplication.adapter.LocationListAdapter
import com.trungvothanh.weatherapplication.adapter.LocationListViewHolder
import com.trungvothanh.weatherapplication.alert.AlertManager
import com.trungvothanh.weatherapplication.databinding.LocationListActivityBinding
import com.trungvothanh.weatherapplication.fragment.AddLocationDialogFragment
import com.trungvothanh.weatherapplication.helpers.StatusBarHelper
import com.trungvothanh.weatherapplication.models.City
import com.trungvothanh.weatherapplication.models.Location
import com.trungvothanh.weatherapplication.network.GoogleMaps
import com.trungvothanh.weatherapplication.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationListActivity : AppCompatActivity(), AddLocationDialogFragment.AddDialogListener {
    private val context: Context = this
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#C34D4D"))
    private lateinit var deleteIcon: Drawable
    private lateinit var binding: LocationListActivityBinding
    private val alertManager = AlertManager()
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //change statusbar color
        val StatusBar = StatusBarHelper()
        StatusBar.makeTransparent(window)

        //recyclerview -> realm version
        binding.locationRecyclerView.layoutManager = LinearLayoutManager(this)

        //set delete icon
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete)!!

        lifecycleScope.launch {
            weatherViewModel.weatherResponse.collect { response ->
                if (response.coord.lat != 0.0 && response.coord.lon != 0.0) {
                    insertLocationIntoRealmDatabase(response)
                }
            }
        }
    }

    fun refreshList() {
        val realm = Realm.getDefaultInstance();
        val locations = realm.where(Location::class.java).findAll()
        binding.locationRecyclerView.adapter =
            LocationListAdapter(context, realm, locations as OrderedRealmCollection<Location>)

        //swipe implementation
        swipeToDelete(realm, locations)

//        /* BUTTONS */
        binding.fabAddButton.setOnClickListener {
            val dialog = AddLocationDialogFragment()
            dialog.setListener(this)
            dialog.show(supportFragmentManager, "AddDialog")
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    //implementing swipe to recyclerview with red background and icon for delete action
    private fun swipeToDelete(realm: Realm, locations: RealmResults<Location>) {

        //swipe manager
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(holder: RecyclerView.ViewHolder, position: Int) {
                    LocationListAdapter(
                        context,
                        realm,
                        locations
                    ).removeItem((holder as LocationListViewHolder))
                    refreshList()
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                    drawSwipeDeleteBackgroundAndIcon(dX, itemView, iconMargin, c)

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                private fun drawSwipeDeleteBackgroundAndIcon(
                    dX: Float,
                    itemView: View,
                    iconMargin: Int,
                    c: Canvas
                ) {
                    //swipe left
                    if (dX < 0) {
                        swipeBackground.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(
                            itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                            itemView.top + iconMargin,
                            itemView.right - iconMargin,
                            itemView.bottom - iconMargin
                        )
                    }
                    swipeBackground.draw(c)
                    deleteIcon.draw(c)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.locationRecyclerView)
    }

    override fun onAddButtonClick(inputText: String) {
        searchLocation(inputText)
    }

    private fun searchLocation(inputText: String) {
        val googleMaps = GoogleMaps(this)
        //get location(lat, long, locality) from passed address
        val googleLocation = googleMaps.geocode(inputText)

        //check if something has been found
        if (googleLocation != null) {

            val locality: String = googleLocation[2] as String // example: Sydney

            try {
                lifecycleScope.launch {
                    locality.let { weatherViewModel.setSearchQuery(it) }
                }


            } catch (e: Exception) {
                //notify user about error(something with real db)
                alertManager.showToHide(
                    this,
                    "Error",
                    "We could not add this location. :(",
                    "Hide"
                )
            }

        } else {
            //notify user about an error (no addresses found)
            alertManager.showToHide(
                this,
                "Alert",
                "Location has not been found. Please check your location name.",
                "OK"
            )
        }


    }

    private fun insertLocationIntoRealmDatabase(city: City) {
        val realm = Realm.getDefaultInstance();
        //check if location already exist in database
        val locations =
            realm.where(Location::class.java).contains("name", city.name).findFirst()
        if (locations == null || locations.latitude != city.coord.lat || locations.longitude != city.coord.lon) {
            //insert into realm db
            Realm.getDefaultInstance().use { r ->
                r.executeTransaction { transactionRealm ->

                    //create new row/index/etc...
                    val location = r.createObject(
                        Location::class.java,
                        autoIncrement(r.where(Location::class.java).max("id"))
                    )

                    //set row/index data
                    location.name = city.name
                    location.longitude = city.coord.lon
                    location.latitude = city.coord.lat
                    location.description = city.weather[0].description
                    location.temp = city.main.temp

                    transactionRealm.insertOrUpdate(location)
                    refreshList()
                }
            }
        } else {
            //notify user about error
            alertManager.showToHide(
                this,
                "Alert",
                "Location already exists in your database",
                "OK"
            )
        }
    }

    private fun autoIncrement(id: Number?): Int {
        return if (id == null) 0 else id.toInt() + 1
    }

}