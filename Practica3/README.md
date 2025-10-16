# Gestor de Archivos - Aplicación Nativa Android

## 📱 Descripción

Gestor de Archivos es una aplicación nativa para Android que permite explorar, visualizar y gestionar archivos del dispositivo. Desarrollada específicamente para el curso de Desarrollo de Aplicaciones Móviles Nativas de ESCOM-IPN.

## ✨ Características Principales

### Exploración de Archivos
- ✅ Navegación por directorios del almacenamiento interno y externo
- ✅ Estructura jerárquica con breadcrumbs de navegación
- ✅ Vista de lista y cuadrícula intercambiables
- ✅ Información detallada de archivos (nombre, tamaño, fecha de modificación)

### Visualización de Archivos
- ✅ Visor de archivos de texto (.txt, .md, .log, .json, .xml)
- ✅ Visor de imágenes con zoom, desplazamiento y rotación
- ✅ Apertura de archivos no soportados con aplicaciones externas mediante Intents

### Gestión de Archivos
- ✅ Crear carpetas
- ✅ Renombrar archivos y carpetas
- ✅ Copiar archivos
- ✅ Mover archivos
- ✅ Eliminar archivos y carpetas

### Funcionalidades Adicionales
- ✅ Sistema de favoritos con almacenamiento persistente (Room Database)
- ✅ Historial de archivos recientes
- ✅ Búsqueda de archivos por nombre
- ✅ Caché de miniaturas para mejor rendimiento

### Interfaz de Usuario
- ✅ Temas personalizables:
  - Tema Guinda IPN (#6B2E5F)
  - Tema Azul ESCOM (#003D6D)
- ✅ Adaptación automática a modo claro/oscuro del sistema
- ✅ Diseño responsivo compatible con diferentes tamaños de pantalla
- ✅ Iconos diferenciados por tipo de archivo
- ✅ Navegación intuitiva con Material Design 3

### Seguridad y Permisos
- ✅ Implementación de Scoped Storage para Android 10+
- ✅ Gestión correcta de permisos según versión de Android
- ✅ Manejo de excepciones para rutas inaccesibles
- ✅ Respeto a restricciones de seguridad de Android

## 🔧 Requisitos del Sistema

### Requisitos de Desarrollo
- **Android Studio**: Ladybug | 2024.2.1 o superior
- **Gradle**: 8.7.3
- **Kotlin**: 2.0.21
- **JDK**: 11 o superior

### Requisitos de Ejecución
- **API Mínima**: Android 7.0 (API 24)
- **API Objetivo**: Android 14 (API 35)
- **Espacio de almacenamiento**: ~20 MB

## 📦 Dependencias Principales

```kotlin
// Core Android & Compose
androidx.core:core-ktx:1.15.0
androidx.compose.material3:material3
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7

// Navigation
androidx.navigation:navigation-compose:2.8.4

// Room Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1

// DataStore
androidx.datastore:datastore-preferences:1.1.1

// Image Loading
io.coil-kt:coil-compose:2.7.0
me.saket.telephoto:zoomable-image-coil:0.12.0

// Permissions
com.google.accompanist:accompanist-permissions:0.36.0
```

## 🚀 Instalación

### Desde Android Studio

1. Clona el repositorio:
```bash
git clone https://github.com/PabloGranados/Moviles.git
cd Moviles/App-Nativa
```

2. Abre el proyecto en Android Studio

3. Sincroniza el proyecto con Gradle:
   - File → Sync Project with Gradle Files

4. Conecta un dispositivo Android o inicia un emulador

5. Ejecuta la aplicación:
   - Run → Run 'app'

### Desde APK

1. Descarga el archivo APK desde [Releases](https://github.com/PabloGranados/Moviles/releases)

2. Habilita la instalación desde fuentes desconocidas:
   - Configuración → Seguridad → Instalar aplicaciones desconocidas

3. Instala el APK

## 🔐 Permisos Requeridos

### Android 13+ (API 33+)
- `READ_MEDIA_IMAGES` - Permite visualizar archivos de imagen
- `READ_MEDIA_VIDEO` - Permite visualizar archivos de video
- `READ_MEDIA_AUDIO` - Permite visualizar archivos de audio

### Android 11-12 (API 30-32)
- `MANAGE_EXTERNAL_STORAGE` - Permite acceso completo a todos los archivos

### Android 7-10 (API 24-29)
- `READ_EXTERNAL_STORAGE` - Permite leer archivos del almacenamiento
- `WRITE_EXTERNAL_STORAGE` - Permite crear, modificar y eliminar archivos

### Justificación
Estos permisos son necesarios para que la aplicación pueda cumplir su función principal: explorar, visualizar y gestionar archivos del dispositivo. La aplicación implementa Scoped Storage y respeta las mejores prácticas de seguridad de Android.

## 🏗️ Arquitectura

### Patrón MVVM (Model-View-ViewModel)

```
app/
├── data/
│   ├── database/        # Room Database (Entities, DAOs, Database)
│   └── repository/      # Repositorios de datos
├── domain/              # Modelos de dominio
├── ui/
│   ├── components/      # Componentes reutilizables
│   ├── screens/         # Pantallas principales
│   └── theme/           # Temas y estilos
├── utils/               # Utilidades y helpers
├── viewmodel/           # ViewModels
└── MainActivity.kt      # Actividad principal
```

### Tecnologías Utilizadas
- **Jetpack Compose**: UI moderna declarativa
- **Room Database**: Almacenamiento persistente local
- **Kotlin Coroutines**: Operaciones asíncronas
- **StateFlow**: Gestión de estado reactiva
- **Navigation Compose**: Navegación entre pantallas
- **Material Design 3**: Diseño moderno y accesible

## 📸 Capturas de Pantalla

### Tema Guinda IPN
| Modo Claro | Modo Oscuro |
|------------|-------------|
| ![Guinda Claro](screenshots/guinda_light.png) | ![Guinda Oscuro](screenshots/guinda_dark.png) |

### Tema Azul ESCOM
| Modo Claro | Modo Oscuro |
|------------|-------------|
| ![Azul Claro](screenshots/azul_light.png) | ![Azul Oscuro](screenshots/azul_dark.png) |

## 🧪 Pruebas

### Dispositivos Probados
- Emulador Pixel 6 - Android 14 (API 34)
- Emulador Pixel 4 - Android 11 (API 30)
- Dispositivo físico (especificar modelo)

### Casos de Prueba
- ✅ Navegación entre directorios
- ✅ Visualización de archivos de texto
- ✅ Visualización de imágenes con zoom
- ✅ Creación de carpetas
- ✅ Renombrado de archivos
- ✅ Eliminación de archivos
- ✅ Sistema de favoritos
- ✅ Historial de archivos recientes
- ✅ Búsqueda de archivos
- ✅ Cambio de temas
- ✅ Rotación de pantalla
- ✅ Gestión de permisos

## 👨‍💻 Autor

**Pablo Granados**
- GitHub: [@PabloGranados](https://github.com/PabloGranados)
- Escuela: ESCOM - IPN

## 📄 Licencia

Este proyecto fue desarrollado con fines educativos para el curso de Desarrollo de Aplicaciones Móviles Nativas.

## 🙏 Agradecimientos

- ESCOM - IPN
- Instituto Politécnico Nacional
- Comunidad de desarrolladores de Android

## 📚 Referencias

- [Android Developers Documentation](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Scoped Storage Guide](https://developer.android.com/training/data-storage)

---

**Fecha de desarrollo**: Octubre 2025  
**Versión**: 1.0.0  
**Curso**: Desarrollo de Aplicaciones Móviles Nativas
