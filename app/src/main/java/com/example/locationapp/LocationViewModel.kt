package com.example.locationapp


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


/*Below is how you would set up a mutable state to keep track
 of the state of the users current location.
* */
class LocationViewModel : ViewModel() {

    private  val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    fun updateLocation(newLocation: LocationData){
        _location.value = newLocation
    }

}