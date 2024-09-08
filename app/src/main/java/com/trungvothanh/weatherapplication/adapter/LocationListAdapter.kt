package com.trungvothanh.weatherapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.trungvothanh.weatherapplication.activity.ForecastActivity
import com.trungvothanh.weatherapplication.alert.AlertManager
import com.trungvothanh.weatherapplication.databinding.RowLocationListBinding
import com.trungvothanh.weatherapplication.helpers.Constants
import com.trungvothanh.weatherapplication.helpers.ForecastHelper
import com.trungvothanh.weatherapplication.models.Location
import io.realm.OrderedRealmCollection
import io.realm.Realm


class LocationListAdapter(
    val context: Context,
    val realm: Realm,
    val locations: OrderedRealmCollection<Location>
) :
    RecyclerView.Adapter<LocationListViewHolder>() {

    val alertManager = AlertManager()

    //number of items expected to be inside recyclerview
    override fun getItemCount(): Int {
        return locations.size
    }

    //prepare custom row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationListViewHolder {
        val itemBinding =
            RowLocationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationListViewHolder(itemBinding)
    }

    //populate row view with data
    override fun onBindViewHolder(holder: LocationListViewHolder, position: Int) {
        val location = locations[position]!!
        val forecastHelper = ForecastHelper(context)
        holder.location = location
        holder.bind(location, forecastHelper)
    }


    fun removeItem(viewHolder: LocationListViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) { // Check if the position is valid
            val item = locations[position]
            if (item != null) {
                realm.executeTransaction { realmTransaction ->
                    // Remove item from Realm
                    val result =
                        realm.where(Location::class.java).equalTo("id", item.id).findAll()
                    result.deleteAllFromRealm()
                    notifyItemRemoved(viewHolder.bindingAdapterPosition)
                    // Optionally notify user
                    Toast.makeText(
                        context,
                        "Location has been successfully deleted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                // Notify user about an error
                alertManager.showToHide(
                    context,
                    "Alert",
                    "Location cannot be deleted because it does not exist or has already been deleted. Please try to restart the application.",
                    "Hide"
                )
            }
        } else {
            // Handle case where position is invalid
            Toast.makeText(context, "Unable to delete item. Please try again.", Toast.LENGTH_LONG)
                .show()
        }
    }
}

class LocationListViewHolder(
    private val itemBinding: RowLocationListBinding,
    var location: Location? = null
) : RecyclerView.ViewHolder(itemBinding.root) {
    init {
        itemBinding.root.setOnClickListener {
            val intent = Intent(itemBinding.root.context, ForecastActivity::class.java)
            intent.putExtra(Constants.CITY_NAME, location?.name)
            itemBinding.root.context.startActivity(intent)
        }
    }

    fun bind(location: Location, forecastHelper: ForecastHelper) {
        itemBinding.locationNameTxtView.text = location.name
        itemBinding.locationDegreeTextView.text = "${location.temp}°"
        itemBinding.root.background =
            forecastHelper.getBackground(location.description)
    }
}