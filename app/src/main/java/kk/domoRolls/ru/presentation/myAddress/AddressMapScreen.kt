package kk.domoRolls.ru.presentation.myAddress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


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

        LaunchedEffect(Unit) {
            mapView.onStart()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { mapView },
            ) {
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
                            fillColor = android.graphics.Color.parseColor(currentPolygon.color)
                            strokeColor = android.graphics.Color.parseColor(currentPolygon.stroke)

                        }
                    }
                }


            }

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
                        modifier = Modifier.height(500.dp)
                    ) {

                    }
                }
            }

        }
}
