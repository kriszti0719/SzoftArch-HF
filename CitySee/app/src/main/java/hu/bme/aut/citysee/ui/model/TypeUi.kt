package hu.bme.aut.citysee.ui.model

import androidx.compose.ui.graphics.Color
import hu.bme.aut.citysee.R
import hu.bme.aut.citysee.domain.model.Type

sealed class TypeUi(
    val title: Int,
    val color: Color
) {
    object None : TypeUi(
        title = R.string.type_title_none,
        color = Color(0xFFE3E6E1) // Neutral grey
    )

    object Museum : TypeUi(
        title = R.string.type_title_museum,
        color = Color(0xFF8E44AD) // Purple
    )

    object HistoricalSite : TypeUi(
        title = R.string.type_title_historical_site,
        color = Color(0xFFD35400) // Burnt orange
    )

    object Park : TypeUi(
        title = R.string.type_title_park,
        color = Color(0xFF2ECC71) // Green
    )

    object ArtGallery : TypeUi(
        title = R.string.type_title_art_gallery,
        color = Color(0xFF3498DB) // Blue
    )

    object Theater : TypeUi(
        title = R.string.type_title_theater,
        color = Color(0xFF9B59B6) // Violet
    )

    object Zoo : TypeUi(
        title = R.string.type_title_zoo,
        color = Color(0xFFF1C40F) // Yellow
    )

    object Market : TypeUi(
        title = R.string.type_title_market,
        color = Color(0xFFE67E22) // Warm orange
    )

    object Bridge : TypeUi(
        title = R.string.type_title_bridge,
        color = Color(0xFF34495E) // Steel grey
    )

    object Observatory : TypeUi(
        title = R.string.type_title_observatory,
        color = Color(0xFF2980B9) // Deep blue
    )

    object Castle : TypeUi(
        title = R.string.type_title_castle,
        color = Color(0xFF7F8C8D) // Stone grey
    )

    object Church : TypeUi(
        title = R.string.type_title_church,
        color = Color(0xFFC0392B) // Red
    )

    object Library : TypeUi(
        title = R.string.type_title_library,
        color = Color(0xFF27AE60) // Green
    )

    object Restaurant : TypeUi(
        title = R.string.type_title_restaurant,
        color = Color(0xFFCD6155) // Warm red
    )

    object Bar : TypeUi(
        title = R.string.type_title_bar,
        color = Color(0xFF8D6E63) // Coffee brown
    )
}

fun TypeUi.asType(): Type {
    return when (this) {
        is TypeUi.None -> Type.NONE
        is TypeUi.Museum -> Type.MUSEUM
        is TypeUi.HistoricalSite -> Type.HISTORICAL_SITE
        is TypeUi.Park -> Type.PARK
        is TypeUi.ArtGallery -> Type.ART_GALLERY
        is TypeUi.Theater -> Type.THEATER
        is TypeUi.Zoo -> Type.ZOO
        is TypeUi.Market -> Type.MARKET
        is TypeUi.Bridge -> Type.BRIDGE
        is TypeUi.Observatory -> Type.OBSERVATORY
        is TypeUi.Castle -> Type.CASTLE
        is TypeUi.Church -> Type.CHURCH
        is TypeUi.Library -> Type.LIBRARY
        is TypeUi.Restaurant -> Type.RESTAURANT
        is TypeUi.Bar -> Type.BAR
    }
}

fun Type.asTypeUi(): TypeUi {
    return when (this) {
        Type.NONE -> TypeUi.None
        Type.MUSEUM -> TypeUi.Museum
        Type.HISTORICAL_SITE -> TypeUi.HistoricalSite
        Type.PARK -> TypeUi.Park
        Type.ART_GALLERY -> TypeUi.ArtGallery
        Type.THEATER -> TypeUi.Theater
        Type.ZOO -> TypeUi.Zoo
        Type.MARKET -> TypeUi.Market
        Type.BRIDGE -> TypeUi.Bridge
        Type.OBSERVATORY -> TypeUi.Observatory
        Type.CASTLE -> TypeUi.Castle
        Type.CHURCH -> TypeUi.Church
        Type.LIBRARY -> TypeUi.Library
        Type.RESTAURANT -> TypeUi.Restaurant
        Type.BAR -> TypeUi.Bar
    }
}