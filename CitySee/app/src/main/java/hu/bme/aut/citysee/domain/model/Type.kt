package hu.bme.aut.citysee.domain.model

enum class Type {
    NONE,
    MUSEUM,
    HISTORICAL_SITE,
    PARK,
    ART_GALLERY,
    THEATER,
    ZOO,
    MARKET,
    BRIDGE,
    OBSERVATORY,
    CASTLE,
    CHURCH,
    LIBRARY,
    RESTAURANT,
    BAR;

    companion object {
        val types = listOf(
            NONE,
            MUSEUM,
            HISTORICAL_SITE,
            PARK,
            ART_GALLERY,
            THEATER,
            ZOO,
            MARKET,
            BRIDGE,
            OBSERVATORY,
            CASTLE,
            CHURCH,
            LIBRARY,
            RESTAURANT,
            BAR
        )
    }
}