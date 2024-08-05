package kk.domoRolls.ru.presentation.myAddress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import kk.domoRolls.ru.domain.model.address.Coordinate
import kk.domoRolls.ru.presentation.components.BaseButton
import kk.domoRolls.ru.presentation.components.DeliveryZonePointer
import kk.domoRolls.ru.presentation.components.isKeyboardVisible
import kk.domoRolls.ru.presentation.personaldata.PersonalDataItem
import kk.domoRolls.ru.presentation.theme.DomoBlue
import kk.domoRolls.ru.presentation.theme.DomoBorder
import kk.domoRolls.ru.util.isPointInPolygon
import kk.domoRolls.ru.util.performGeocoding
import kk.domoRolls.ru.util.performReverseGeocoding

const val RESTAURANT_ADDRESS = "Артиллерийская улица, 10А, Саратов."
val RESTAURANT_COORDINATES = Pair(51.568985, 46.008870)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressMapScreen(
    addressMapViewModel: AddressMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardVisible = isKeyboardVisible()

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded
        )

    )
    val mapData = addressMapViewModel.mapData.collectAsState()
    var inDeliveryZone by remember { mutableStateOf(true) }
    var isMoveFinished by remember { mutableStateOf(true) }
    var currentPrice by remember { mutableIntStateOf(0) }

    val inOrderMode by addressMapViewModel.inOrderMode.collectAsState()
    val focusManager = LocalFocusManager.current

    val currentAddress by addressMapViewModel.currentAddressModel.collectAsState()

    val yandexCameraListener = CameraListener { _, cameraPosition, _, finished ->

        val polygons = mapData.value.map {
            Polygon(
                LinearRing(it.coordinates.map { Point(it.last(), it.first()) }),
                emptyList()
            )
        }

        isMoveFinished = finished
        if (finished) {
            inDeliveryZone = polygons.any {
                isPointInPolygon(
                    point = Point(cameraPosition.target.latitude, cameraPosition.target.longitude),
                    polygon = it
                )
            }

            currentPrice = mapData.value.find {
                isPointInPolygon(
                    point = Point(cameraPosition.target.latitude, cameraPosition.target.longitude),
                    polygon = Polygon(
                        LinearRing(it.coordinates.map { Point(it.last(), it.first()) }),
                        emptyList()
                    )
                )
            }?.cost?:0

            performReverseGeocoding(
                Point(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                ), cameraPosition.zoom.toInt()
            ) {

                addressMapViewModel.setCurrentAddressModel(
                    currentAddress.copy(
                        street = it,
                        coordinate = Coordinate(
                            cameraPosition.target.latitude,
                            cameraPosition.target.longitude
                        )
                    )
                )
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            mapWindow.map.addCameraListener(yandexCameraListener)
        }
    }

    LaunchedEffect(keyboardVisible) {
        if (keyboardVisible.not()) focusManager.clearFocus()
        if (keyboardVisible) {
            mapView.mapWindow.map.removeCameraListener(yandexCameraListener)
        }
    }
    LaunchedEffect(inOrderMode) {
        with(mapView.mapWindow.map) {
            isRotateGesturesEnabled = inOrderMode
            isZoomGesturesEnabled = inOrderMode
            isScrollGesturesEnabled = inOrderMode
        }

        if (!inOrderMode) {
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(
                        RESTAURANT_COORDINATES.first,
                        RESTAURANT_COORDINATES.second,
                    ), 20.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null

            )
        }

    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        mapView.onStart()
        mapView.mapWindow.map.addCameraListener(yandexCameraListener)
    }
    BottomSheetScaffold(
        scaffoldState = sheetState,
        containerColor = Color.White,
        sheetContainerColor = Color.White,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    BaseButton(
                        buttonTitle = "Доставка",
                        backgroundColor = if (inOrderMode) DomoBlue else DomoBorder,
                        titleColor = if (inOrderMode) Color.White else Color.Black,
                        modifier = Modifier
                            .padding(start = 20.dp, end = 8.dp)
                            .fillMaxWidth(0.5f),
                        onClick = { addressMapViewModel.setOrderMode(true) }
                    )

                    BaseButton(
                        buttonTitle = "Самовывоз",
                        backgroundColor = if (inOrderMode) DomoBorder else DomoBlue,
                        titleColor = if (inOrderMode) Color.Black else Color.White,
                        modifier = Modifier
                            .padding(end = 20.dp, start = 8.dp)
                            .fillMaxWidth(),
                        onClick = { addressMapViewModel.setOrderMode(false) }
                    )

                }
                PersonalDataItem(
                    label = "Доставим на адрес:",
                    value = if (inOrderMode) currentAddress.street else RESTAURANT_ADDRESS,
                    placeHolder = "user name",
                    readOnly = !inOrderMode,
                    onDone = {
                        performGeocoding(
                            currentAddress.street,
                            mapView.mapWindow.map
                        ) {

                            it?.let {
                                mapView.mapWindow.map.move(
                                    CameraPosition(
                                        it,
                                        mapView.mapWindow.map.cameraPosition.zoom,
                                        0.0f,
                                        0.0f
                                    ),
                                    Animation(Animation.Type.SMOOTH, 1F),
                                    null
                                )
                                addressMapViewModel.setCurrentAddressModel(
                                    currentAddress.copy(
                                        coordinate = Coordinate(
                                            it.latitude,
                                            it.longitude
                                        )
                                    )
                                )
                            }


                        }
                    },
                    onValueChange = { input ->
                        addressMapViewModel.setCurrentAddressModel(
                            currentAddress.copy(street = input)
                        )
                    })

                Row(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(DomoBorder, RoundedCornerShape(20.dp))

                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            text = "Доставка 0 ₽"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .background(DomoBorder, RoundedCornerShape(20.dp))

                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                            text = "Заказ от $currentPrice ₽"
                        )
                    }
                }
                var checked by remember { mutableStateOf(true) }

                AnimatedVisibility(inOrderMode) {
                    Column {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 22.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Частный дом")

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    addressMapViewModel.setCurrentAddressModel(
                                        currentAddress.copy(
                                            isPrivateHouse = checked
                                        )
                                    )
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = DomoBlue,
                                    uncheckedThumbColor = DomoBlue,
                                    uncheckedTrackColor = Color.White,
                                )
                            )
                        }

                        AnimatedVisibility(visible = checked) {
                            Column {
                                Row {
                                    PersonalDataItem(
                                        modifier = Modifier.fillMaxWidth(0.5f),
                                        label = "Квартира",
                                        value = currentAddress.flat,
                                        placeHolder = "",
                                        onValueChange = {
                                            addressMapViewModel.setCurrentAddressModel(
                                                currentAddress.copy(flat = it)
                                            )
                                        })
                                    PersonalDataItem(
                                        label = "Подъезд",
                                        value = currentAddress.entrance,
                                        placeHolder = "",
                                        onValueChange = {
                                            addressMapViewModel.setCurrentAddressModel(
                                                currentAddress.copy(entrance = it)
                                            )
                                        })

                                }

                                Row {
                                    PersonalDataItem(
                                        modifier = Modifier.fillMaxWidth(0.5f),
                                        label = "Этаж",
                                        value = currentAddress.floor,
                                        placeHolder = "",
                                        onValueChange = {
                                            addressMapViewModel.setCurrentAddressModel(
                                                currentAddress.copy(floor = it)
                                            )
                                        })
                                    PersonalDataItem(
                                        label = "Домофон",
                                        value = currentAddress.intercom,
                                        placeHolder = "",
                                        onValueChange = {
                                            addressMapViewModel.setCurrentAddressModel(
                                                currentAddress.copy(intercom = it)
                                            )
                                        })
                                }
                            }
                        }
                    }
                }
                BaseButton(
                    buttonTitle = "Да, все верно",
                    backgroundColor = DomoBlue,
                    modifier = Modifier
                        .padding(horizontal = 22.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    onClick = {}
                )

            }

        },
    ) {
        val animateBottomPadding by animateDpAsState(
            targetValue = if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded) 250.dp else 50.dp,
            animationSpec = tween(durationMillis = 250), label = ""
        )

        Box(
            modifier = Modifier
                .padding(bottom = animateBottomPadding)
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
                modifier = Modifier.align(Alignment.Center),
                inDeliveryZone = inDeliveryZone,
                isDragFinished = isMoveFinished
            )
        }
    }
}
