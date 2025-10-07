

# 🚇 PMV App
<div align="left">
  <img src="assets/PMV-icon.png" width="360"/>
</div>

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

A simple Android app built with **Jetpack Compose** that shows real-time information for **Valencia’s metro and tram stops**.  
You can check upcoming departures, see details like line and destination, reload data, and mark stops as favorites.

---

## ✨ Features
- 🕒 View real-time arrival times  
- 🚉 Select stops from a complete list  
- ⭐ Save favorite stops (stored with DataStore)  
- 🔄 Reload updated information  
- 🎨 Built with a clean and modern Compose UI  

---

## 🧰 Tech Stack
- **Kotlin**  
- **Jetpack Compose (Material 3)**  
- **OkHttp** for network requests  
- **Moshi** for JSON parsing  
- **DataStore Preferences** for persistence  

---

## 🚀 Installation
### 1️⃣ Clone this repository:

`git clone https://github.com/Juguitoo/PMV.git`

### 2️⃣ Open in Android Studio
### 3️⃣ Run on an emulator or a connected Android device

## 🌐 API
This app uses live data from the [Metro API by Alex Badi ↗](https://docs.metroapi.alexbadi.es/)

Example endpoint:

    GET https://metroapi.alexbadi.es/prevision/{stopId}/parse

## 📌 Note
This app is **for educational and personal use only**.  
Data is provided by [Metro API by Alex Badi ↗](https://docs.metroapi.alexbadi.es/) and ultimately comes from Metrovalencia's public data.  
This project is **not monetized**.

## 🙌 Credits

- **Developer:** Juguito

- **API Provider:** Alex Badi (Metro API)

- **Public Transit Data:** Metrovalencia

## 🪪 License

This project is licensed under the MIT License — feel free to use and modify it.

## 🔗 Useful Links
- [Kotlin Documentation↗](https://kotlinlang.org/docs/home.html)  
- [Jetpack Compose Documentation↗](https://developer.android.com/jetpack/compose)  
- [Metro API Docs↗](https://docs.metroapi.alexbadi.es/)  
- [Metrovalencia Official Website↗](https://www.metrovalencia.es/)  
- [Android Studio↗](https://developer.android.com/studio)  
- [OkHttp Library↗](https://square.github.io/okhttp/)  
- [Moshi JSON Library↗](https://github.com/square/moshi)  
- [DataStore Preferences↗](https://developer.android.com/topic/libraries/architecture/datastore)

