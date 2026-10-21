package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminUser
import com.example.model.ScreenNav
import com.example.ui.theme.BorderSlate
import com.example.ui.theme.DeepTeal
import com.example.ui.theme.DeepTealDark
import com.example.ui.theme.SidebarActiveContent
import com.example.ui.theme.SidebarActivePill
import com.example.ui.theme.SidebarBackground
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class NavItemData(
    val screen: ScreenNav,
    val icon: ImageVector,
    val label: String
)

private val primaryNavItems = listOf(
    NavItemData(ScreenNav.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
    NavItemData(ScreenNav.STUDENTS, Icons.Default.People, "Students List"),
    NavItemData(ScreenNav.RECORD_PAYMENT, Icons.Default.CreditCard, "Record Payment"),
    NavItemData(ScreenNav.FEE_STRUCTURE, Icons.Default.Tune, "Fee Structure"),
    NavItemData(ScreenNav.PAYMENT_HISTORY, Icons.Default.ReceiptLong, "Payment History"),
    NavItemData(ScreenNav.REPORTS, Icons.Default.BarChart, "Reports")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppResponsiveShell(
    currentScreen: ScreenNav,
    currentUser: AdminUser,
    onNavigate: (ScreenNav) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTabletOrDesktop = maxWidth >= 760.dp

        if (isTabletOrDesktop) {
            // Tablet-first permanent hybrid sidebar layout
            Row(modifier = Modifier.fillMaxSize()) {
                SidebarContent(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onNavigate = onNavigate,
                    onLogout = onLogout,
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                )

                // Main content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(com.example.ui.theme.BackgroundSlate)
                ) {
                    content()
                }
            }
        } else {
            // Compact mobile layout with drawer & bottom nav
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = SidebarBackground,
                        modifier = Modifier.width(280.dp)
                    ) {
                        SidebarContent(
                            currentScreen = currentScreen,
                            currentUser = currentUser,
                            onNavigate = { screen ->
                                onNavigate(screen)
                                scope.launch { drawerState.close() }
                            },
                            onLogout = {
                                onLogout()
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = DeepTeal,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentScreen.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = TextPrimary
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("nav_drawer_toggle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = DeepTeal
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = SurfaceCard,
                            tonalElevation = 6.dp
                        ) {
                            val bottomNavItems = listOf(
                                NavItemData(ScreenNav.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                                NavItemData(ScreenNav.STUDENTS, Icons.Default.People, "Students"),
                                NavItemData(ScreenNav.RECORD_PAYMENT, Icons.Default.CreditCard, "Pay"),
                                NavItemData(ScreenNav.PAYMENT_HISTORY, Icons.Default.ReceiptLong, "History"),
                                NavItemData(ScreenNav.REPORTS, Icons.Default.BarChart, "Reports")
                            )

                            bottomNavItems.forEach { item ->
                                val isSelected = currentScreen == item.screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { onNavigate(item.screen) },
                                    icon = {
                                        Icon(imageVector = item.icon, contentDescription = item.label)
                                    },
                                    label = { Text(item.label, fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DeepTeal,
                                        selectedTextColor = DeepTeal,
                                        indicatorColor = com.example.ui.theme.DeepTealContainer
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(com.example.ui.theme.BackgroundSlate)
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarContent(
    currentScreen: ScreenNav,
    currentUser: AdminUser,
    onNavigate: (ScreenNav) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SidebarBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // App Branding Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepTealDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "FeeTrack",
                        tint = SidebarActiveContent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "FeeTrack",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Bursary Administration",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Academic Year Badge
            Surface(
                color = DeepTealDark.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(com.example.ui.theme.SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2026/2027 • Term 1 Active",
                        color = Color(0xFFCCFBF1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MANAGEMENT MENU",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Navigation Items
            primaryNavItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                SidebarNavButton(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.screen) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom User & Logout Card
        Column {
            HorizontalDivider(color = Color(0xFF1E3A37))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F2F2C))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DeepTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Text(
                        text = currentUser.role,
                        color = Color(0xFF5EEAD4),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarNavButton(
    item: NavItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SidebarActivePill else Color.Transparent
    val contentColor = if (isSelected) SidebarActiveContent else Color(0xFFE2E8F0)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onClick() }
            .testTag("nav_tab_${item.screen.name}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.label,
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = fontWeight
            )
        }
    }
}
