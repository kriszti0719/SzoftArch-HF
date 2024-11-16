package hu.bme.aut.citysee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import hu.bme.aut.citysee.navigation.NavGraph
import hu.bme.aut.citysee.ui.theme.CitySeeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitySeeTheme() {
                NavGraph()
            }
        }
    }
}