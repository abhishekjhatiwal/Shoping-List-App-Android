package com.example.shopinglistapp

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
    var address: String = ""
)

@Composable
fun ShopingListApp(
    locationUtiles: LocationUtiles,
    viewModel: LocationViewModel,
    navController: NavController,
    context: Context,
    address: String
) {
    var sItem by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuentity by remember { mutableStateOf("") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                // Permission granted
                locationUtiles.requestLocationUpdate(viewModel = viewModel)
            } else {
                // Permission Asked
                val rationalRequest = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (rationalRequest) {
                    Toast.makeText(context, "Location Permission Required", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location Permission Denied, Please enable from settings",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add Item")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItem) { item ->
//                ShopingListItem(it, {}, {})
                if (item.isEditing) {
                    // This is the correct composable to show when the item is being edited.
                    ShopingItemEditor(
                        item = item,
                        onEditComplete = { editedName, editedQuantity ->
                            // Create a new list with the edited item
                            sItem = sItem.map {
                                if (it.id == item.id) {
                                    it.copy(
                                        name = editedName,
                                        quantity = editedQuantity,
                                        isEditing = false
                                    )
                                } else {
                                    it
                                }
                            }
                        }
                    )
                } else {
                    ShopingListItem(
                        item = item,
                        onEditClick = {  // finding which item we are editing and changing isEding boolean to true
                            sItem = sItem.map { it.copy(isEditing = it.id == item.id) }
                        },
                        onDeleteClick = {
                            sItem = sItem - item
                        })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (itemName.isNotBlank() && itemQuentity.isNotBlank()) {
                            val newitem = ShoppingItem(
                                id = sItem.size + 1,
                                name = itemName,
                                quantity = itemQuentity.toInt(),
                                address = address
                            )
                            sItem = sItem + newitem
                            showDialog = false
                            itemName = ""
                            itemQuentity = ""
                        }
                    }) {
                        Text("Add")
                    }
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            },
            title = { Text("Add Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName, onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = itemQuentity, onValueChange = { itemQuentity = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    Button(onClick = {
                        if(locationUtiles.hasLocationPermission(context)){
                            locationUtiles.requestLocationUpdate(viewModel)
                            navController.navigate("locationscreen"){
                                this.launchSingleTop
                            }
                        }else{
                            requestPermissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ))
                        }
                    }){
                        Text("Select Address")
                    }
                }
            }
        )

    }
}

@Composable
fun ShopingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp)
            )
        }
        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            }
        ) {
            Text("Save")
        }
    }
}

@Composable
fun ShopingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
//    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(8.dp)) {
            Row {
                Text(text = " ${item.name}", modifier = Modifier.padding(16.dp))
                Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(16.dp))
            }
            Row(modifier = Modifier.fillMaxWidth()){
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Text(text = item.address)
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}