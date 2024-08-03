package kk.domoRolls.ru.presentation.myAddress

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import kk.domoRolls.ru.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressMapScreen(
    addressMapViewModel: AddressMapViewModel = hiltViewModel(),
) {
    val mapData = addressMapViewModel.mapData.collectAsState()
    val context = LocalContext.current

    val mapView = remember { MapView(context) }
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentAddress by remember { mutableStateOf("") }
    var placemarkPosition by remember {
        mutableStateOf(
            Point(
                55.751244,
                37.618423
            )
        )
    } // Initial position


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = {
                mapView
            },
        ) {
            val placeMark = mapView.mapWindow.map.mapObjects.addPlacemark(placemarkPosition)

            mapView.mapWindow.map.addCameraListener { map, cameraPosition, cameraUpdateReason, b ->

//                placeMark.geometry =
//                    Point(cameraPosition.target.latitude, cameraPosition.target.longitude)


                performReverseGeocoding(
                    Point(
                        cameraPosition.target.latitude,
                        cameraPosition.target.longitude
                    ), cameraPosition.zoom.toInt()
                ) {
                  //  placeMark.setText(it)
                }

            }

            if (mapData.value.isNotEmpty()) {

                mapData.value.forEach { currentPolygon ->
                    val polylinePoints = currentPolygon.coordinates


                    mapView.mapWindow.map.move(
                        CameraPosition(
                            Point(
                                polylinePoints.last().last(),
                                polylinePoints.last().first(),

                                ), 10.0f, 0.0f, 0.0f
                        ),
                        Animation(Animation.Type.SMOOTH, 1F),
                        null

                    )

                    val polygon = Polygon(
                        LinearRing(polylinePoints.map { Point(it.last(), it.first()) }),
                        emptyList()
                    )

                    mapView.mapWindow.map.mapObjects.addPolygon(polygon).apply {
                        strokeWidth = 1f
                        fillColor = ColorUtils.setAlphaComponent(android.graphics.Color.parseColor(currentPolygon.color),50)
                        strokeColor = android.graphics.Color.parseColor(currentPolygon.stroke)

                    }

                }
            }


        }

        Image(
            painter = painterResource(id = R.drawable.ic_pointer),
            contentDescription = "Center Marker",
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                scrimColor = Color.Transparent,
                containerColor = Color.White,
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Box(
                    modifier = Modifier.height(400.dp)
                ) {

                }
            }
        }

    }
}

private fun performReverseGeocoding(point: Point, zoom: Int, onResult: (String) -> Unit) {
    val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    searchManager.submit(
        point,
        zoom,
        SearchOptions(),
        object : SearchListener {
            override fun onSearchResponse(p0: Response) {
                val searchResult = p0.collection.children.firstOrNull()?.obj
                val geoObjects = p0.collection.children.mapNotNull { it.obj }

                val address = searchResult?.name
                    ?: "No address found"
                onResult(address)
            }

            override fun onSearchError(p0: Error) {

            }

        }
    )

}



