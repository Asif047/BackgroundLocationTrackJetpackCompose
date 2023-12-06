package com.example.backgroundtasksperiodical

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.backgroundtasksperiodical.ui.theme.BackgroundTasksPeriodicalTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager




        setContent {
            BackgroundTasksPeriodicalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   // Greeting("Android")

                    Button(onClick = {

                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            enableLocationSettings()
                        } else {
                            requestAppPermissions()
                        }

                    }) {
                        Text(text = "Start Service")
                    }
                }
            }
        }
    }


    private fun requestAppPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        startService(Intent(this@MainActivity, ForegroundService::class.java))
                    }

                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    protected fun enableLocationSettings() {
        val locationRequest = LocationRequest.create()
            .setInterval(1000)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(this) {
                // startUpdatingLocation(...)
            }
            .addOnFailureListener(this) { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        val resolvable = ex
                        resolvable.startResolutionForResult(this@MainActivity, 123)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Button(onClick = {

    }) {
        Text(text = "Start Service")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BackgroundTasksPeriodicalTheme {
        Greeting("Android")
    }
}