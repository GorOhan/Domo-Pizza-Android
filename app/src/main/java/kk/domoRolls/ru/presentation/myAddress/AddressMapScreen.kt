package kk.domoRolls.ru.presentation.myAddress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kk.domoRolls.ru.presentation.components.DeliveryZonePointer
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.util.isPointInPolygon
import kk.domoRolls.ru.util.performReverseGeocoding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressMapScreen(
    addressMapViewModel: AddressMapViewModel = hiltViewModel()
) {
    val sheetState = rememberBottomSheetScaffoldState()
    val mapData = addressMapViewModel.mapData.collectAsState()
    val context = LocalContext.current
    val currentAddress = remember { mutableStateOf("") }
    var inDeliveryZone by remember {
        mutableStateOf(true)
    }
    val yandexCameraListener = CameraListener { _, cameraPosition, _, finished ->
        val coordinates =
            mapData.value.flatMap { it.coordinates }.map { Point(it.last(), it.first()) }
        val polygon = Polygon(
            LinearRing(coordinates),
            emptyList()
        )

        if (finished) {
            inDeliveryZone = isPointInPolygon(
                Point(cameraPosition.target.latitude, cameraPosition.target.longitude),
                polygon
            )

            performReverseGeocoding(
                Point(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                ), cameraPosition.zoom.toInt()
            ) {
                currentAddress.value = it
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            mapWindow.map.addCameraListener(yandexCameraListener)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        mapView.mapWindow.map.addCameraListener(yandexCameraListener)

    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        containerColor = Color.White,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(
                    text = currentAddress.value,
                    style = MaterialTheme.typography.titleSmall,
                    color = DomoBlue
                )
            }
        },
        sheetPeekHeight = 200.dp
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    mapView.mapWindow.map.addCameraListener(yandexCameraListener)


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
                                fillColor = ColorUtils.setAlphaComponent(
                                    android.graphics.Color.parseColor(currentPolygon.color), 50
                                )
                                strokeColor =
                                    android.graphics.Color.parseColor(currentPolygon.stroke)

                            }

                        }
                    }
                    mapView
                },
            ) {
                it.mapWindow.map.addCameraListener(yandexCameraListener)
            }



           DeliveryZonePointer(
               modifier = Modifier.align(Alignment.Center) ,
               inDeliveryZone = inDeliveryZone)
        }
    }
}
