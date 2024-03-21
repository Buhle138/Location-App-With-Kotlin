package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          val viewModel: LocationViewModel = viewModel()

            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current //getting the current context of the activity that we are on
    val locationUtils = LocationUtils(context) //returns true or false if

    locationDisplay(locationUtils = locationUtils, viewModel ,context = context)
}

/*The composable below itself is used to set up the user interface for location display
and permissions display handling. This button is set up to create a user interface for obtaining
and updating the user's location.
* */
@Composable
fun locationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context,
    ){

    val location = viewModel.location.value

    val address = location?.let{
        locationUtils.reverseGeocodeLocation(location)
    }
    //We will use this request for permission launcher only if the user has not granted permission
    //contract is what we are trying to get.
    //onResult is the result of getting what is in the contract.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                //I have access to your location

                locationUtils.requestLocationUpdates(viewModel = viewModel)
            }else{
                //Ask for permission
                val rationalRequired  = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity, //We want to open this rational in the main activity
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                //giving the user good reasons why we want their permission to access their location
                if(rationalRequired){
                    Toast.makeText(context, "Location Permission is required for this feature to work", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "Please enable it in the android settings", Toast.LENGTH_LONG).show()

                }

            }
        })


    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        if(location != null){
            Text("Address: ${location.latitude} ${location.longitude} \n $address")
        }else{
            Text(text = "Location not available")
        }

        
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
                //Permission already granted update the location
                locationUtils.requestLocationUpdates(viewModel)

            }else{
                //Request location permission by launching the rational
                requestPermissionLauncher.launch(
                    arrayOf(//Since we are requesting multiple permissions they will be in the form of an array.
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get location")
        }
    }

}

