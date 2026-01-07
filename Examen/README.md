# GPS Tracker - Aplicación de Rastreo en Tiempo Real

Aplicación móvil nativa en Android que rastrea y muestra la ubicación del usuario en tiempo real con soporte para rastreo en segundo plano.

## Características Implementadas

### 1. Rastreo de Ubicación
- ✅ Obtención de coordenadas GPS (latitud, longitud)
- ✅ Intervalos configurables: 10s, 60s, 5 minutos
- ✅ Funcionamiento en segundo plano mediante servicio
- ✅ Notificación persistente configurable (ON/OFF)

### 2. Visualización en Mapa
- ✅ Integración con Google Maps
- ✅ Marcador de posición actual
- ✅ Ruta del recorrido dibujada
- ✅ Actualización en tiempo real

### 3. Almacenamiento
- ✅ Base de datos Room (SQLite)
- ✅ Datos guardados: latitud, longitud, timestamp, precisión

### 4. Interfaz de Usuario
- ✅ Mapa con ubicación actual y ruta
- ✅ Coordenadas GPS actuales
- ✅ Selector de intervalo (10s, 60s, 5min)
- ✅ Botones iniciar/detener rastreo
- ✅ Pantalla de historial completo
- ✅ Indicador visual del estado

### 5. Temas Personalizables
- ✅ Tema Guinda (IPN)
- ✅ Tema Azul (ESCOM)
- ✅ Soporte modo claro/oscuro automático

### 6. Funcionalidades Adicionales
- ✅ Precisión de ubicación mostrada
- ✅ Opción para limpiar historial
- ✅ Manejo de permisos en tiempo de ejecución
- ✅ Permiso de ubicación en segundo plano
- ✅ Permiso de notificaciones (Android 13+)

## Configuración Requerida

### IMPORTANTE: API Key de Google Maps

Antes de ejecutar la aplicación, debes configurar tu API Key de Google Maps:

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Habilita las siguientes APIs:
   - Maps SDK for Android
   - Places API (opcional)

4. Crea una API Key:
   - Ve a "Credenciales"
   - Crea credenciales → API Key
   - Restringe la key (recomendado):
     - Aplicaciones Android
     - Agrega el SHA-1 de tu keystore
     - Agrega el nombre del paquete: `com.example.examen`

5. Reemplaza `YOUR_API_KEY_HERE` en el archivo:
   ```
   app/src/main/AndroidManifest.xml
   ```
   
   Busca:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```
   
   Y reemplaza con tu API Key real:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="AIzaSy..." />
   ```

### Obtener SHA-1 para restricciones (opcional pero recomendado)

Para Debug:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Para Release:
```bash
keytool -list -v -keystore /path/to/your/keystore -alias your-key-alias
```

## Estructura del Proyecto

```
app/src/main/java/com/example/examen/
├── data/
│   ├── LocationEntity.kt          # Modelo de datos
│   ├── LocationDao.kt              # DAO para operaciones de BD
│   ├── LocationDatabase.kt         # Configuración Room
│   └── LocationRepository.kt       # Repositorio
├── service/
│   └── LocationTrackingService.kt  # Servicio en segundo plano
├── utils/
│   └── ThemeManager.kt             # Gestor de temas
├── MainActivity.kt                 # Actividad principal
├── HistoryActivity.kt              # Actividad de historial
└── LocationAdapter.kt              # Adaptador RecyclerView
```

## Permisos Solicitados

- `ACCESS_FINE_LOCATION` - Ubicación precisa
- `ACCESS_COARSE_LOCATION` - Ubicación aproximada
- `ACCESS_BACKGROUND_LOCATION` - Ubicación en segundo plano (Android 10+)
- `FOREGROUND_SERVICE` - Servicio en primer plano
- `FOREGROUND_SERVICE_LOCATION` - Servicio de ubicación
- `POST_NOTIFICATIONS` - Notificaciones (Android 13+)

## Requisitos del Sistema

- Android SDK 24+ (Android 7.0 Nougat)
- Target SDK 36
- Kotlin 2.0.21
- Google Play Services

## Dependencias Principales

- Room Database (2.6.1)
- Google Play Services Location (21.3.0)
- Google Play Services Maps (19.0.0)
- Lifecycle Components (2.8.7)
- Kotlin Coroutines (1.9.0)

## Cómo Usar la Aplicación

1. **Primera Ejecución**: La app solicitará permisos de ubicación
2. **Rastreo**:
   - Selecciona el intervalo de actualización
   - Activa/desactiva la notificación
   - Presiona "Iniciar Rastreo"
3. **Visualización**: El mapa mostrará tu ubicación actual y la ruta recorrida
4. **Historial**: Presiona "Ver Historial" para ver todas las ubicaciones guardadas
5. **Temas**: Usa el menú (⋮) para cambiar entre tema IPN (Guinda) y ESCOM (Azul)

## Cambiar Tema

1. Toca el menú de opciones (tres puntos verticales)
2. Selecciona "Seleccionar Tema"
3. Elige entre:
   - Tema IPN (Guinda)
   - Tema ESCOM (Azul)
4. El tema se adapta automáticamente al modo claro/oscuro del sistema

## Notas de Desarrollo

- La aplicación prioriza **funcionalidad sobre diseño** según especificaciones
- No incluye emojis en la interfaz
- Utiliza Material Design 3 para componentes UI
- Soporta modo claro/oscuro del sistema automáticamente
- El servicio en segundo plano usa notificación persistente
- Los datos se guardan localmente con Room/SQLite

## Solución de Problemas

### El mapa no se muestra
- Verifica que hayas configurado correctamente la API Key
- Asegúrate de que las APIs estén habilitadas en Google Cloud Console
- Revisa que el SHA-1 coincida si restringiste la API Key

### No se obtiene ubicación
- Verifica que los permisos estén otorgados
- Activa el GPS en el dispositivo
- Prueba en un dispositivo físico (el emulador puede tener problemas)

### El rastreo en segundo plano no funciona
- Verifica el permiso de ubicación en segundo plano
- Desactiva optimización de batería para la app
- Algunos fabricantes requieren configuración adicional

## Compilar y Ejecutar

```bash
# Limpiar y compilar
./gradlew clean build

# Instalar en dispositivo
./gradlew installDebug

# Ejecutar
# O usa Android Studio: Run > Run 'app'
```

## Autor

Aplicación desarrollada como examen técnico de desarrollo Android.
