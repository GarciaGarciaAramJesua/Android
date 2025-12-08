# Salud Activa - Aplicación de Bienestar para Wear OS y Android

## Descripción

**Salud Activa** es una aplicación integral de salud y bienestar diseñada para dispositivos Wear OS y smartphones Android. La aplicación ayuda a los usuarios a mantener hábitos saludables mediante recordatorios de hidratación y pausas activas con ejercicios guiados.

## Características Principales

### 💧 Recordatorios de Hidratación
- **Seguimiento de consumo de agua**: Registra tu ingesta diaria de agua
- **Meta personalizable**: Configura tu objetivo diario (por defecto 2000ml)
- **Recordatorios programables**: Notificaciones periódicas para mantenerte hidratado
- **Registro rápido**: Botones de cantidad rápida (100ml, 250ml, 500ml, 1L)
- **Progreso visual**: Indicador circular del progreso hacia tu meta diaria

### 🏃 Pausas Activas
- **Ejercicios predefinidos**: 4 tipos de pausas activas
  - Estiramiento Rápido (3 min)
  - Descanso Visual (2 min)
  - Respiración Profunda (2 min)
  - Ejercicios de Escritorio (5 min)
- **Guía paso a paso**: Instrucciones detalladas para cada ejercicio
- **Recordatorios personalizables**: Programa pausas durante tu jornada laboral
- **Seguimiento de tiempo activo**: Registra minutos de actividad diaria

### 📊 Estadísticas
- **Panel de resumen diario**: Vista consolidada de tu progreso
- **Histórico de actividades**: Registros de agua consumida y pausas completadas
- **Indicadores visuales**: Gráficos de progreso y porcentajes

### ⚙️ Configuración
- **Intervalos personalizables**: Ajusta la frecuencia de recordatorios
- **Horarios flexibles**: Define horarios de inicio y fin
- **Activación/desactivación**: Control individual de recordatorios
- **Meta diaria ajustable**: Personaliza tu objetivo de hidratación

## Requisitos Técnicos

### Para Wear OS (Smartwatch)
- **SDK Mínimo**: Android API 30 (Android 11)
- **SDK Objetivo**: Android API 36
- **Tipo de dispositivo**: Wear OS 3.0+
- **Características**: Pantallas circulares y rectangulares

### Para Android Mobile
- **SDK Mínimo**: Android API 24 (Android 7.0)
- **SDK Objetivo**: Android API 36
- **Características**: Smartphones y tablets

## Tecnologías Utilizadas

### Arquitectura y Diseño
- **MVVM (Model-View-ViewModel)**: Separación clara de responsabilidades
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Para la versión móvil
- **Wear Compose Material**: Para la versión wearable
- **Navigation Compose**: Navegación entre pantallas

### Gestión de Datos
- **DataStore Preferences**: Almacenamiento local eficiente
- **Kotlin Flow**: Gestión reactiva de datos
- **Repository Pattern**: Abstracción de fuentes de datos

### Funcionalidades de Background
- **WorkManager**: Tareas programadas y recordatorios periódicos
- **Notification Channels**: Notificaciones categorizadas
- **AlarmManager**: Alarmas exactas para recordatorios

### Librerías Principales
```kotlin
- Jetpack Compose (UI)
- Lifecycle ViewModel & Runtime Compose
- WorkManager
- DataStore Preferences
- Navigation Compose
- Material 3 / Wear Compose Material
```

## Estructura del Proyecto

```
Wearable/
├── app/                                    # Módulo Wear OS
│   └── src/main/
│       ├── java/com/example/wearable/
│       │   ├── data/
│       │   │   ├── model/                  # Modelos de datos
│       │   │   │   ├── WaterIntake.kt
│       │   │   │   ├── ActiveBreak.kt
│       │   │   │   └── ReminderSettings.kt
│       │   │   └── repository/             # Repositorios
│       │   │       └── HealthRepository.kt
│       │   ├── notifications/              # Sistema de notificaciones
│       │   │   └── NotificationHelper.kt
│       │   ├── presentation/
│       │   │   ├── screens/                # Pantallas Wear OS
│       │   │   │   ├── HomeScreen.kt
│       │   │   │   ├── WaterScreen.kt
│       │   │   │   ├── ActiveBreakListScreen.kt
│       │   │   │   ├── ActiveBreakExerciseScreen.kt
│       │   │   │   ├── StatsScreen.kt
│       │   │   │   └── SettingsScreen.kt
│       │   │   ├── viewmodel/              # ViewModels
│       │   │   │   ├── WaterViewModel.kt
│       │   │   │   └── ActiveBreakViewModel.kt
│       │   │   ├── theme/
│       │   │   │   └── Theme.kt
│       │   │   └── MainActivity.kt
│       │   └── workers/                    # Workers para tareas background
│       │       ├── WaterReminderWorker.kt
│       │       ├── ActiveBreakReminderWorker.kt
│       │       └── ReminderScheduler.kt
│       └── AndroidManifest.xml
│
└── mobile/                                 # Módulo Android Mobile
    └── src/main/
        ├── java/com/example/wearable/mobile/
        │   ├── data/                       # (Copiado de app)
        │   ├── notifications/
        │   ├── workers/
        │   ├── viewmodel/                  # ViewModels para móvil
        │   │   ├── WaterViewModel.kt
        │   │   └── ActiveBreakViewModel.kt
        │   ├── ui/
        │   │   ├── screens/                # Pantallas móvil
        │   │   │   ├── MobileHomeScreen.kt
        │   │   │   ├── MobileWaterScreen.kt
        │   │   │   ├── MobileActiveBreakScreen.kt
        │   │   │   ├── MobileStatsScreen.kt
        │   │   │   └── MobileSettingsScreen.kt
        │   │   └── theme/
        │   │       └── Theme.kt
        │   └── MainActivity.kt
        └── AndroidManifest.xml
```

## Instalación y Configuración

### 1. Clonar o Abrir el Proyecto
```bash
# Abre el proyecto en Android Studio
```

### 2. Sincronizar Gradle
```bash
# Android Studio sincronizará automáticamente las dependencias
# O ejecuta: ./gradlew build
```

### 3. Configurar Dispositivos

#### Para Wear OS:
1. Habilita el modo desarrollador en tu smartwatch
2. Conecta el reloj mediante ADB over WiFi o USB
3. Selecciona el módulo `app` en Android Studio
4. Ejecuta en el dispositivo Wear OS

#### Para Android Mobile:
1. Conecta tu smartphone o usa un emulador
2. Selecciona el módulo `mobile` en Android Studio
3. Ejecuta en el dispositivo Android

### 4. Conceder Permisos
Al ejecutar la aplicación por primera vez, se solicitarán los siguientes permisos:
- **Notificaciones**: Para recordatorios
- **Alarmas exactas**: Para programación precisa
- **Sensores corporales** (Wear OS): Para futuras funcionalidades

## Uso de la Aplicación

### Configuración Inicial
1. **Abre la aplicación** en tu dispositivo
2. **Accede a Configuración** desde el menú principal
3. **Activa los recordatorios** que desees usar
4. **Ajusta intervalos y metas** según tus necesidades

### Registro de Hidratación
1. Toca el botón **"💧 Hidratación"**
2. Selecciona la cantidad de agua consumida
3. El progreso se actualizará automáticamente

### Pausas Activas
1. Toca el botón **"🏃 Pausas Activas"**
2. Elige el tipo de pausa que prefieras
3. Sigue las instrucciones de cada ejercicio
4. Completa la secuencia o salta si es necesario

### Ver Estadísticas
1. Toca el botón **"📊 Estadísticas"**
2. Revisa tu progreso diario
3. Verifica metas alcanzadas

## Implementación Técnica Destacada

### Gestión de Estado Reactivo
```kotlin
val dailyStats: StateFlow<DailyWaterStats> = repository.getDailyWaterStats()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyWaterStats(...)
    )
```

### Persistencia de Datos
```kotlin
// DataStore para configuraciones
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "health_settings")
```

### Tareas Programadas
```kotlin
// WorkManager para recordatorios periódicos
val waterReminderWork = PeriodicWorkRequestBuilder<WaterReminderWorker>(
    intervalMinutes.toLong(),
    TimeUnit.MINUTES
).build()
```

### Navegación con Gestos (Wear OS)
```kotlin
SwipeDismissableNavHost(
    navController = navController,
    startDestination = "home"
) { ... }
```

## Diseño de Interfaz

### Wear OS
- **Interfaz optimizada** para pantallas pequeñas
- **ScalingLazyColumn** para contenido desplazable
- **Chips y CompactChips** para acciones rápidas
- **CircularProgressIndicator** para progreso visual
- **Soporte para pantallas circulares y rectangulares**

### Android Mobile
- **Material Design 3** con esquemas de color adaptativos
- **Cards** para organización visual
- **TopAppBar** con navegación intuitiva
- **Linear y Circular Progress Indicators**
- **Dialogs** para interacciones complejas

## Características de Accesibilidad

- ✅ **Texto legible**: Tamaños de fuente apropiados
- ✅ **Contraste de colores**: Cumple con estándares WCAG
- ✅ **Feedback háptico**: Vibraciones para notificaciones
- ✅ **Iconos descriptivos**: Emojis y textos claros
- ✅ **Navegación por gestos**: Swipe para retroceder en Wear OS

## Optimizaciones

### Batería
- **Constraint de batería baja** en Workers
- **Intervalos flexibles** en notificaciones periódicas
- **Suspensión inteligente** de recordatorios fuera de horario

### Rendimiento
- **Lazy loading** de listas
- **StateFlow** para actualizaciones eficientes
- **Composición optimizada** con recomposición mínima

### Almacenamiento
- **Limpieza automática** de datos antiguos (>7 días)
- **Almacenamiento compacto** con serialización de strings

## Cumplimiento de Requisitos

### ✅ Diseño de Interfaz
- ✅ Material Design para Wear OS
- ✅ Compatibilidad con pantallas circulares y rectangulares
- ✅ Adaptación para visibilidad en exteriores

### ✅ Implementación Técnica
- ✅ Navegación mediante gestos estándar
- ✅ Uso eficiente de sensores (preparado para acelerómetro y ritmo cardíaco)
- ✅ Manejo optimizado de batería
- ✅ Almacenamiento local de datos críticos

### ✅ Aplicación Complementaria Android
- ✅ Versión funcional para dispositivo Android convencional
- ✅ Replica las principales funcionalidades
- ✅ Interfaz adaptada para pantalla grande

### ✅ Propuesta Implementada: Salud y Bienestar
- ✅ Recordatorios de hidratación
- ✅ Recordatorios de pausas activas
- ✅ Ejercicios guiados

## Futuras Mejoras

- 🔄 Sincronización entre dispositivos Wear OS y móvil
- 📈 Gráficos históricos semanales/mensuales
- 🏆 Sistema de logros y recompensas
- 💓 Integración con sensor de ritmo cardíaco
- 🏃‍♂️ Detección automática de actividad con acelerómetro
- ☁️ Backup en la nube
- 👥 Compartir progreso con amigos

## Contribución

Este proyecto fue desarrollado como parte de un ejercicio académico de desarrollo de aplicaciones para dispositivos vestibles.

## Licencia

Este proyecto es de código educativo y está disponible para fines de aprendizaje.

---

**Desarrollado con ❤️ usando Kotlin y Jetpack Compose**
