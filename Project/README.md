# Plataforma de Participación Ciudadana

Aplicación Android completa para reportes ciudadanos con integración Firebase y Google Maps.

## Características Implementadas

### 1. Módulo de Reporte Ciudadano
- 6 tipos de reportes con formularios diferenciados:
  - Servicios Públicos (Baches, luminarias, fugas)
  - Robo o Asalto
  - Corrupción u Omisión
  - Violencia de Género
  - Narcomenudeo
  - Reporte General
- Captura automática de GPS
- Carga de fotografías desde cámara o galería
- Sistema de alias para anonimato

### 2. Módulo de Mapa de Incidencias
- Mapa interactivo con Google Maps
- Marcadores de reportes con colores por categoría
- Vista detallada al tocar un marcador
- Sistema de semáforo (heatmap) por densidad de reportes:
  - Rojo: Alto (15+ reportes)
  - Amarillo: Medio (5-14 reportes)
  - Verde: Bajo (0-4 reportes)

### 3. Módulo de Directorio de Instituciones
- 30 instituciones preconfiguradas de CDMX
- Buscador de instituciones
- Filtros por categoría (seguridad, agua, género, salud, educación, general)
- Funciones interactivas:
  - Llamada telefónica directa
  - Apertura en Google Maps
  - Acceso al sitio web

### 4. Arquitectura MVVM
- Models: Report, Institution, ReportType, ZoneData
- ViewModels: ReportViewModel, MapViewModel, DirectoryViewModel
- Repositories: ReportRepository, InstitutionRepository
- Views: Fragments con ViewBinding

### 5. Servicios en la Nube
- Firebase Cloud Firestore para almacenamiento de reportes
- Firebase Cloud Storage para fotografías
- Sincronización en tiempo real

## Configuración Necesaria

### 1. Firebase Setup
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o usa uno existente
3. Agrega una aplicación Android con el package name: `com.example.project`
4. Descarga el archivo `google-services.json`
5. Coloca `google-services.json` en la carpeta `app/` del proyecto

### 2. Habilitar Servicios Firebase
En la consola de Firebase:
- Activa **Cloud Firestore** en modo de prueba
- Activa **Cloud Storage** en modo de prueba

### 3. Google Maps API Key
1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un proyecto o usa el mismo de Firebase
3. Habilita la API de Google Maps para Android
4. Crea una API Key
5. Edita el archivo `AndroidManifest.xml` y reemplaza `YOUR_GOOGLE_MAPS_API_KEY` con tu clave

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_CLAVE_API_AQUI"/>
```

### 4. Restricciones de la API Key (Recomendado)
En Google Cloud Console:
- Restringe la API Key a tu aplicación usando el SHA-1
- Para obtener el SHA-1, ejecuta:
```bash
./gradlew signingReport
```

## Permisos Implementados

La aplicación solicita los siguientes permisos:
- INTERNET: Conexión a Firebase
- ACCESS_FINE_LOCATION: GPS para ubicación precisa
- ACCESS_COARSE_LOCATION: GPS para ubicación aproximada
- CAMERA: Tomar fotografías
- READ_EXTERNAL_STORAGE: Leer imágenes (Android < 13)
- READ_MEDIA_IMAGES: Leer imágenes (Android 13+)
- CALL_PHONE: Realizar llamadas directas

## Estructura del Proyecto

```
app/src/main/
├── java/com/example/project/
│   ├── data/
│   │   ├── model/
│   │   │   ├── Report.kt
│   │   │   ├── ReportType.kt
│   │   │   ├── Institution.kt
│   │   │   └── ZoneData.kt
│   │   └── repository/
│   │       ├── ReportRepository.kt
│   │       └── InstitutionRepository.kt
│   ├── ui/
│   │   ├── report/
│   │   │   ├── NewReportFragment.kt
│   │   │   └── ReportViewModel.kt
│   │   ├── map/
│   │   │   ├── MapFragment.kt
│   │   │   └── MapViewModel.kt
│   │   └── directory/
│   │       ├── DirectoryFragment.kt
│   │       ├── DirectoryViewModel.kt
│   │       └── InstitutionAdapter.kt
│   └── MainActivity.kt
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── fragment_new_report.xml
    │   ├── fragment_map.xml
    │   ├── fragment_directory.xml
    │   └── item_institution.xml
    ├── navigation/
    │   └── nav_graph.xml
    ├── menu/
    │   └── bottom_nav_menu.xml
    └── xml/
        └── file_paths.xml
```

## Dependencias Principales

- Firebase BOM 33.7.0
  - Cloud Firestore
  - Cloud Storage
- Google Play Services Maps 21.3.0
- Google Play Services Location 21.3.0
- Android Lifecycle & ViewModel 2.9.1
- Navigation Component 2.8.7
- Coroutines 1.10.1
- Glide 4.16.0

## Cómo Ejecutar

1. Abre el proyecto en Android Studio
2. Configura Firebase (google-services.json)
3. Configura Google Maps API Key
4. Sincroniza Gradle
5. Ejecuta en un dispositivo físico o emulador con Google Play Services

## Notas Importantes

- La aplicación requiere Google Play Services para funcionar correctamente
- Se recomienda probar en un dispositivo físico para las funciones de GPS y cámara
- El mapa mostrará CDMX por defecto (coordenadas: 19.4326, -99.1332)
- Las instituciones están preconfiguradas en el repositorio
- Los reportes se almacenan en tiempo real en Firestore

## Flujo de Uso

1. **Inicio**: La app abre en el Mapa de Incidencias
2. **Crear Reporte**: 
   - Toca "Reportar" en el menú inferior
   - Selecciona tipo de reporte
   - Completa formulario específico
   - Agrega fotografía (opcional)
   - Confirma ubicación GPS
   - Envía reporte
3. **Ver Reportes**: 
   - Regresa al Mapa
   - Toca marcadores para ver detalles
   - Observa el código de colores por severidad
4. **Buscar Ayuda**:
   - Toca "Directorio" en el menú inferior
   - Filtra por categoría
   - Usa el buscador
   - Llama, abre mapa o visita sitio web

## Solución de Problemas

### Firebase no conecta
- Verifica que `google-services.json` esté en la carpeta `app/`
- Confirma que el package name coincida
- Revisa que los servicios estén habilitados en Firebase Console

### Google Maps no carga
- Verifica que la API Key sea válida
- Confirma que la API de Maps esté habilitada
- Revisa las restricciones de la API Key

### GPS no funciona
- Activa el GPS en el dispositivo
- Acepta los permisos de ubicación
- Prueba en un dispositivo físico (los emuladores pueden tener problemas)

## Mejoras Futuras Sugeridas

- Sistema de autenticación de usuarios
- Panel de administración para gestionar reportes
- Notificaciones push para actualizaciones de reportes
- Estadísticas y análisis de incidencias
- Exportación de datos
- Soporte multiidioma
- Modo oscuro
- Filtros avanzados en el mapa

## Licencia

Proyecto educativo - Uso libre
