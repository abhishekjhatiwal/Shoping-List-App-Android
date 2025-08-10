package com.example.shopinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.example.shopinglistapp.ui.theme.ShopingListAppTheme
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopingListAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}



@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: LocationViewModel = viewModel()
    val context = LocalContext.current
    val locationUtils = LocationUtiles(context)
    val coroutineScope = rememberCoroutineScope() //

    NavHost(navController, startDestination = "shopinglistscreen") {
        composable("shopinglistscreen") {
            ShopingListApp(
                locationUtiles = locationUtils,
                viewModel = viewModel,
                navController = navController,
                context = context,
                address = viewModel.adderss.value.firstOrNull()?.formatted_address ?: "No Address"
            )
        }

//        dialog("locationscreen"){backstack->
//            viewModel.location.value?.let{it1 ->
//
//                LocationSelectionScreen(location = it1, onLocationSelected = {locationdata->
//                    viewModel.fetchAddress("${locationdata.latitude},${locationdata.longitude}")
//                    navController.popBackStack()
//                })
//            }
//        }

        dialog("locationscreen"){backstack->
            viewModel.location.value?.let{it1 ->
                LocationSelectionScreen(
                    location = it1,
                    onLocationSelected = {
//                        viewModel.updateLocation(it)
                        coroutineScope.launch {
                            viewModel.fetchAddress("${it.latitude},${it.longitude}")
                        }
                        navController.popBackStack()
                    },
                    locations = viewModel.adderss.value
                )
            }
        }

    }
}







//@Composable
//fun Navigation() {
//    val navController = rememberNavController()
//    val viewModel: LocationViewModel = viewModel()
//    val context = LocalContext.current
//    val locationUtils = LocationUtiles(context)
//
//    NavHost(navController, startDestination = "shopinglistscreen") {
//        composable("shopinglistscreen") {
//            ShopingListApp(
//                locationUtiles = locationUtils,
//                viewModel = viewModel,
//                navController = navController,
//                context = context,
//                address = viewModel.adderss.value.firstOrNull()?.formatted_address ?: "No Address"
//            )
//        }
//
////        dialog("locationscreen"){backstack->
////            viewModel.location.value?.let{it1 ->
////                LocationSelectionScreen(
////                    location = it1,
////                    onLocationSelected = {
//////                        viewModel.updateLocation(it)
////                        viewModel.fetchAddress("${it.latitude}","${it.longitude}")
////                        navController.popBackStack()
////                    },
////                    locations = viewModel.adderss.value
////                )
////            }
////        }
//
//        dialog("locationscreen") { _ ->
//            viewModel.location.value?.let { location ->
//                LocationSelectionScreen(
//                    location = location,
//                    onLocationSelected = { selectedLocation ->
//                        coroutineScope.launch {
//                            viewModel.fetchAddress(selectedLocation.latitude, selectedLocation.longitude)
//                        }
//                        navController.popBackStack()
//                    },
//                    locations = viewModel.adderss.value
//                )
//            }
//        }
//    }
//}
//
