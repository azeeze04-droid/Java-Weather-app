# Java Weather App

A beginner-friendly Java desktop weather app using Swing, the OpenWeatherMap Current Weather API, and Gson for JSON parsing.

## Features

- Search for weather using a city name.
- Displays temperature in Celsius, feels-like temperature, weather description, humidity, wind speed, and pressure.
- Uses a Swing graphical user interface.
- Makes the HTTP request in a background worker so the window stays responsive.
- Handles blank input, unknown cities, invalid or inactive API keys, network failures, and API rate limits.

## What you need

- JDK 17 or newer recommended.
- An internet connection.
- A free OpenWeatherMap account and API key.

Get an API key at https://home.openweathermap.org/users/sign_up. In your OpenWeatherMap account, find the API key section.

## Step 1: Add your API key

Open `config.properties` in VS Code. Replace the placeholder with your own key:

```properties
api.key=PASTE_YOUR_API_KEY_HERE
```

Save the file. Keep your key private. Do not share a real key in screenshots, public repositories, or messages.

## Step 2: Run on Windows without installing Maven

Double-click `run.bat` or run it from a VS Code terminal:

```powershell
.\run.bat
```

The script compiles the Java source and starts the app. The first run downloads the Gson library from Maven Central, so an internet connection is needed for that step. After the JAR is downloaded, it is reused on later runs.

If Windows blocks the script, open Command Prompt or the VS Code terminal inside this project folder and run `run.bat` there.

## Alternative: Run with Maven

If Maven is installed, open a terminal in the project folder and run:

```bash
mvn compile exec:java
```

Maven downloads Gson automatically.

## How the program works

1. The user enters a city name.
2. Java URL-encodes the city and sends an HTTP GET request to OpenWeatherMap.
3. Gson parses the JSON response into JSON objects and arrays.
4. The app extracts the city, weather description, temperature, feels-like temperature, humidity, wind speed, and pressure.
5. The GUI updates with the results. If the request fails, the app displays a helpful error message.

## API used

`https://api.openweathermap.org/data/2.5/weather`

The request uses `units=metric`, which returns temperature in Celsius and wind speed in meters per second.
