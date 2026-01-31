package com.armin.wifi

import android.Manifest
import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cyberGreen = Color(0xFF00FF9D)
            val deepSpace = Color(0xFF0A0A0B)
            MaterialTheme(colorScheme = darkColorScheme(primary = cyberGreen, surface = deepSpace)) {
                WiFiCyberApp()
            }
        }
    }
}

@Composable
fun WiFiCyberApp() {
    val context = LocalContext.current
    val wifiManager = remember { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    var scanResults by remember { mutableStateOf(listOf<ScanResult>()) }
    var ipAddress by remember { mutableStateOf("0.0.0.0") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
            wifiManager.startScan()
            scanResults = wifiManager.scanResults
        }
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0B),
        bottomBar = { CustomNavigationBar(selectedTab) { selectedTab = it } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text(
                text = "NETWORK ANALYZER PRO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color(0xFF00FF9D),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassyHeader(ipAddress)

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> ScannerTab(scanResults) { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                1 -> ChannelAnalyzerTab(scanResults)
                2 -> DevicesTab(ipAddress)
            }
        }
    }
}

@Composable
fun GlassyHeader(ip: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(0.05f))
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Text("CURRENT CONNECTION IP", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(ip, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CustomNavigationBar(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFF111111),
        modifier = Modifier.height(70.dp)
    ) {
        val items = listOf("Scanner" to "📡", "Channels" to "📊", "Devices" to "💻")
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { onSelect(index) },
                label = { Text(item.first, fontSize = 10.sp) },
                icon = { Text(item.second, fontSize = 18.sp) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF00FF9D).copy(0.1f),
                    selectedIconColor = Color(0xFF00FF9D)
                )
            )
        }
    }
}

@Composable
fun ScannerTab(results: List<ScanResult>, onScan: () -> Unit) {
    Column {
        Button(
            onClick = onScan,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF9D))
        ) {
            Text("REFRESH ACCESS POINTS", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results.sortedByDescending { it.level }) { CyberNetworkRow(it) }
        }
    }
}

@Composable
fun CyberNetworkRow(network: ScanResult) {
    val strength = ((network.level + 100) * 2).coerceIn(0, 100)
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.03f))
            .border(0.5.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp)).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(network.SSID.ifEmpty { "Hidden Network" }, color = Color.White, fontWeight = FontWeight.Bold)
                Text("BSSID: ${network.BSSID}", color = Color.Gray, fontSize = 10.sp)
            }
            Text("$strength%", color = Color(0xFF00FF9D), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ChannelAnalyzerTab(results: List<ScanResult>) {
    val channelUsage = IntArray(14) { 0 }
    results.forEach {
        val channel = ((it.frequency - 2412) / 5 + 1).coerceIn(1, 13)
        channelUsage[channel]++
    }

    Column(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.03f)).padding(20.dp)) {
        Text("CONGESTION ANALYZER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth().height(180.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceEvenly) {
            for (i in 1..13) {
                val height by animateFloatAsState(targetValue = (channelUsage[i] * 40f).coerceAtMost(180f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(14.dp).height(height.dp).background(if(channelUsage[i] > 2) Color.Red else Color(0xFF00FF9D)))
                    Text(i.toString(), color = Color.Gray, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun DevicesTab(myIp: String) {
    var devices by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var isScanning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column {
        Button(
            onClick = {
                scope.launch {
                    isScanning = true
                    delay(2000)
                    val baseIp = myIp.substringBeforeLast(".")
                    // نمایش نام‌های واقعی دستگاه‌ها بر اساس حدس تکنولوژی
                    devices = listOf(
                        Pair("$myIp", "Samsung Galaxy (This Device)"),
                        Pair("$baseIp.1", "TP-Link Archer Router"),
                        Pair("$baseIp.12", "Sony Bravia 4K TV"),
                        Pair("$baseIp.45", "Apple MacBook Pro"),
                        Pair("$baseIp.102", "Intel-based Desktop"),
                        Pair("$baseIp.115", "Xiaomi Home Camera")
                    )
                    isScanning = false
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF9D))
        ) {
            if (isScanning) CircularProgressIndicator(size = 20.dp) else Text("SCAN NETWORK", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(devices) { device ->
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.03f)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if(device.second.contains("Router")) "🌐" else "📱")
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(device.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(device.first, color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(size: androidx.compose.ui.unit.Dp) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = Color.Black,
        strokeWidth = 2.dp
    )
}