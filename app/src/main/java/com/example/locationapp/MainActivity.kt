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
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp(){
    val context = LocalContext.current //getting the current context of the activity that we are on
    val locationUtils = LocationUtils(context)
    locationDisplay(locationUtils = locationUtils, context = context)
}

/*The composable below itself is used to set up the user interface for location display
and permissions display handling. This button is set up to create a user interface for obtaining
and updating the user's location.
* */
@Composable
fun locationDisplay(
    locationUtils: LocationUtils,
    context: Context
    ){

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                //I have access to your location

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
        Text(text = "Location not available")
        
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
                //Permission already granted update the location

            }else{
                //Request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
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

