# 📡 Network Analyzer Pro
### A Professional Wireless Diagnostic Suite for Android

Developed by **Armin Largani** *Computer Science Master's Student | Berlin, Germany*

---

## 🚀 Overview
**Network Analyzer Pro** is a modern network utility built to provide deep insights into wireless environments. It combines high-performance scanning with a sleek, industrial dark-themed UI (Cyberpunk style).

This project demonstrates advanced proficiency in **Jetpack Compose**, **Android Core APIs**, and **Reactive UI State Management**.

---

## ✨ Key Features

### 🔍 WiFi Intelligence
* **Live Scanning:** Real-time discovery of Access Points with RSSI signal tracking.
* **Signal Metrics:** Advanced calculation of connection quality from dBm to percentages.
* **Network Details:** Displaying SSID, BSSID, and precise Frequency data.

### 📊 Congestion Mapping
* **Channel Analysis:** Visualizes 2.4GHz band traffic (Channels 1-13).
* **Interference Detection:** Helps users identify the best channel for router configuration using animated bar charts.

### 💻 Hardware Discovery
* **Subnet Scanning:** Identifies active devices within the local network (LAN).
* **Device Identification:** Lists connected hardware including Routers, Laptops, Smart TVs, and IoT devices.

---

## 🛠 Tech Stack
* **UI Framework:** Jetpack Compose (Material 3)
* **Language:** Kotlin 2.0 (The latest Compose Compiler plugin)
* **Async Logic:** Kotlin Coroutines
* **Build System:** Gradle with Version Catalog (TOML)
* **Design Pattern:** State-driven UI Architecture

---

## 🔧 Prerequisites
* **Physical Device:** Required for actual WiFi hardware scanning (Emulators do not support hardware WiFi scan).
* **Location Services:** GPS must be enabled to retrieve SSID/BSSID data due to Android security requirements.

---

## 📂 Project Structure
* `MainActivity.kt`: Contains the core logic and all Compose UI components.
* `build.gradle.kts`: Modern Gradle configuration using Kotlin DSL.
* `libs.versions.toml`: Centralized dependency management.

---

## 👤 Contact
* **Author:** Armin Largan
