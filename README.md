# Weatherawy - Android Weather Application

Weatherawy is an Android mobile application designed to provide users with real-time weather updates and temperature information. With Weatherawy, users can effortlessly check the current weather status and temperature at their current location. Additionally, they have the flexibility to explore weather conditions in specific locations by either selecting them on the map or using the auto-complete search feature.

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [How to Use](#how-to-use)
- [Android SDK Version](#android-sdk-version)

## Features

- **Current Weather Status**: Display the current weather status and temperature based on the user's location.
- **Map Integration**: Select specific locations on the map to view weather information.
- **Auto-complete Search**: Easily search for locations using the auto-complete feature in the search bar.
- **Favorite Locations**: Add selected locations to a list of favorite locations for quick access to weather updates.
- **Weather Alerts**: Set alerts for various weather conditions such as rain, wind, extreme temperatures, fog, snow, etc.

## Technologies Used

- **Android Studio**: The application is developed using Android Studio, the official IDE for Android development.
- **Kotlin**: The primary programming language used for developing the application logic.
- **MVVM Architecture**: Utilization of the Model-View-ViewModel architectural pattern for separating concerns and facilitating testability.
- **Unit Testing**: Implementation of unit tests to ensure the reliability and correctness of application logic.
- **Flows (Flow, StateFlow, SharedFlow)**: Integration of Kotlin Flows to handle asynchronous data streams efficiently.
- **Alarm Manager**: Use of Android's Alarm Manager for scheduling periodic tasks such as weather updates and notifications.
- **Room**: Integration of Room Persistence Library for local database management, enabling offline caching of weather data.
- **Retrofit**: Networking library for making HTTP requests to fetch weather data from external APIs.
- **Network Checking**: Implementation of network connectivity checks to ensure seamless data retrieval.
- **In-Memory Caching**: Utilization of in-memory caching mechanisms to optimize data retrieval performance.
- **Local Caching When Offline**: Implementation of local caching strategies to store weather data when the device is offline.
- **Background Animation**: Integration of animations to provide visual cues reflecting current weather conditions.
- **Notifications**: Utilization of Android's NotificationManager to deliver timely weather alerts and updates to users.

## How to Use

1. **Location Permission**: Grant location permission to allow the application to access your current location.
2. **Explore**: Explore the current weather status at your location or search for specific locations using the map or search bar.
3. **Favorite Locations**: Add your preferred locations to the list of favorites for quick access to weather updates.
4. **Set Alerts**: Set alerts for specific weather conditions to stay informed about changes in weather patterns.

## Android SDK Version

The Weatherawy application requires a minimum Android SDK version of API level 26 (Android 8.0, Oreo) and is optimized for API level 34.
