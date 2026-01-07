# Solución: Google Maps no muestra el mapa

## Problema
El mapa muestra solo la marca de agua de Google pero no el contenido del mapa.

## Causas comunes
1. API Key no tiene habilitada la API correcta
2. Falta configurar las restricciones de la aplicación
3. La API Key está restringida incorrectamente

## Solución Paso a Paso

### 1. Verificar que Maps SDK for Android esté habilitado

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Selecciona tu proyecto
3. Ve a "APIs y servicios" → "Biblioteca"
4. Busca "Maps SDK for Android"
5. Asegúrate de que esté HABILITADO (botón verde)
6. Si no está habilitado, haz clic en "HABILITAR"

### 2. Obtener el SHA-1 de tu certificado de debug

Abre una terminal y ejecuta:

**En Windows (PowerShell o CMD):**
```bash
cd C:\Users\TU_USUARIO\.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**En Linux/Mac/WSL:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copia el valor de **SHA1** (se verá algo como: `A1:B2:C3:D4:E5:F6:...`)

### 3. Configurar restricciones en la API Key

1. En Google Cloud Console, ve a "APIs y servicios" → "Credenciales"
2. Haz clic en tu API Key (AIzaSyCIYL4LCJjaMP9dPpn5njrxhCp8BdjSM-Q)
3. En "Restricciones de aplicación":
   - Selecciona "Aplicaciones de Android"
   - Haz clic en "Agregar un nombre de paquete y huella digital"
   - Nombre del paquete: `com.example.examen`
   - Huella digital del certificado SHA-1: pega el SHA-1 que obtuviste
   - Haz clic en "Listo"
4. En "Restricciones de API":
   - Selecciona "Restringir clave"
   - Marca "Maps SDK for Android"
5. Haz clic en "Guardar"

### 4. Espera unos minutos
Los cambios en Google Cloud pueden tardar 5-10 minutos en propagarse.

### 5. Reinstala la aplicación
```bash
# Desinstala la app del dispositivo/emulador
adb uninstall com.example.examen

# Vuelve a instalar
./gradlew installDebug
```

## Solución Alternativa: Crear una nueva API Key

Si los pasos anteriores no funcionan:

1. En Google Cloud Console → "Credenciales"
2. Haz clic en "+ CREAR CREDENCIALES" → "Clave de API"
3. Copia la nueva API Key
4. Haz clic en "RESTRINGIR CLAVE"
5. Configura las restricciones como se indicó arriba
6. Reemplaza la API Key en AndroidManifest.xml

## Verificar en Logcat

Ejecuta la app y revisa el Logcat en Android Studio:
- Busca mensajes de error con "Google Maps" o "API"
- Filtra por "Error" o "Authorization"

Los errores comunes que verás:
- "INVALID_KEY" → La API key no es válida
- "API_NOT_ACTIVATED" → Maps SDK for Android no está habilitado
- "PERMISSION_DENIED" → Problema con las restricciones SHA-1

## Comando para ver Logcat desde terminal

```bash
adb logcat | grep -i "maps\|google\|api"
```

## Nota Importante

Si estás usando un emulador, asegúrate de que:
1. Tenga Google Play Services instalado (usa un AVD con Play Store)
2. Esté conectado a Internet
3. La versión de Google Play Services esté actualizada
