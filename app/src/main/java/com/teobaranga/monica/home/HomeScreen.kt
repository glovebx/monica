package com.teobaranga.monica.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ContactsDestination
import com.ramcosta.composedestinations.generated.destinations.DashboardDestination
import com.ramcosta.composedestinations.generated.destinations.HomeDestination
import com.ramcosta.composedestinations.generated.destinations.JournalEntryListDestination
import com.ramcosta.composedestinations.generated.destinations.SetupDestination
import com.ramcosta.composedestinations.generated.navgraphs.HomeGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.teobaranga.kotlin.inject.viewmodel.runtime.compose.injectedViewModel
import com.teobaranga.monica.ui.PreviewPixel4
import com.teobaranga.monica.ui.Zero
import com.teobaranga.monica.ui.theme.MonicaTheme
import com.teobaranga.monica.util.compose.thenIf

@Destination<RootGraph>(start = true)
@Composable
internal fun Home(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = injectedViewModel(),
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navigator.navigate(SetupDestination) {
                popUpTo(HomeDestination) {
                    inclusive = true
                }
            }
        }
    }

    if (isLoggedIn == true) {
        HomeScreen(
            modifier = modifier,
        )
    } else {
        // TODO Replace with Loading screen
        Box(
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            val currentDestination by navController.currentDestinationAsState()
            val isVisible by remember {
                derivedStateOf {
                    currentDestination in listOf(DashboardDestination, ContactsDestination, JournalEntryListDestination)
                }
            }
            HomeNavigationBar(
                modifier = Modifier
                    .animateContentSize()
                    .thenIf(!isVisible) {
                        height(0.dp)
                    },
                navController = navController,
            )
        },
        contentWindowInsets = WindowInsets.Zero,
    ) { contentPadding ->
        if (!LocalInspectionMode.current) {
            DestinationsNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
                navGraph = HomeGraph,
                engine = engine,
                navController = navController,
            )
        }
    }
}

@PreviewPixel4
@Composable
private fun PreviewHomeScreen() {
    MonicaTheme {
        HomeScreen()
    }
}
