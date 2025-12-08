# Aplicación Biblioteca Digital - Proyecto Completo

Esta aplicación Android cumple con todos los requisitos especificados en los ejercicios 1, 2 y 3.

## Características Implementadas

### ✅ Ejercicio 1: Integración con API REST
- **Conexión a la API REST**: La app se conecta a la API Flask (`app.py`) para autenticación, favoritos e historial
- **Persistencia de sesión**: Se mantiene la sesión del usuario mediante SharedPreferences
- **Base de datos local**: Usa Room (SQLite) para almacenar datos offline
- **Sincronización**: Los datos se sincronizan automáticamente entre la BD local y las APIs remotas
- **Cliente HTTP**: Implementado con Retrofit para todas las conexiones

### ✅ Ejercicio 2: Consumo de APIs Públicas
- **Open Library API**: Integración completa para buscar libros y autores
- **Búsquedas**: Los usuarios pueden buscar por título o autor
- **Recomendaciones**: Sistema basado en historial y favoritos
- **Almacenamiento offline**: Los libros buscados se cachean localmente
- **Registro y Login**: Sistema completo de autenticación

### ✅ Ejercicio 3: Funcionalidades Adicionales
- **Historial de búsquedas**: Registro completo de todas las búsquedas del usuario
- **Favoritos**: Agregar/eliminar libros a favoritos con sincronización
- **Recomendaciones personalizadas**: Basadas en autores favoritos y búsquedas recientes
- **Panel de administrador**: Vista completa de usuarios, favoritos, historial y estadísticas

## Estructura del Proyecto

```
app/
├── data/
│   ├── local/
│   │   ├── entity/          # Entidades Room (UserEntity, BookEntity, etc.)
│   │   ├── dao/             # DAOs para acceso a BD
│   │   └── AppDatabase.kt   # Configuración de Room
│   ├── remote/
│   │   ├── api/             # Interfaces Retrofit (ApiService, OpenLibraryService)
│   │   ├── model/           # Modelos de respuesta API
│   │   └── RetrofitClient.kt
│   ├── repository/          # Repositorios (AuthRepository, BookRepository, AdminRepository)
│   └── Resource.kt          # Clase wrapper para respuestas
├── ui/
│   ├── auth/                # LoginActivity, RegisterActivity
│   ├── home/                # HomeActivity (pantalla principal)
│   ├── admin/               # AdminActivity (panel de administrador)
│   └── adapter/             # Adapters para RecyclerViews
└── util/
    └── SessionManager.kt    # Gestión de sesión
```

## Configuración y Ejecución

### 1. Configurar la API REST (Flask)

El archivo `app.py` ya está actualizado con todos los endpoints necesarios:
- `/register` - Registro de usuarios
- `/login` - Inicio de sesión
- `/favorites` - Gestión de favoritos (GET, POST, DELETE)
- `/search-history` - Gestión de historial
- `/recommendations` - Obtener recomendaciones
- `/admin/*` - Endpoints de administrador

**Ejecutar la API:**
```bash
cd Login-Flask
pip install flask flask-sqlalchemy flask-bcrypt
python app.py
```

La API correrá en `http://localhost:5000`

**Usuario admin por defecto:**
- Usuario: `admin`
- Contraseña: `admin123`

### 2. Configurar la Aplicación Android

**Importante: Actualizar la URL de la API**

Edita el archivo `RetrofitClient.kt` y cambia la URL según tu configuración:

```kotlin
// Para emulador de Android Studio:
private const val BASE_URL = "http://10.0.2.2:5000/"

// Para dispositivo físico (reemplaza con tu IP local):
private const val BASE_URL = "http://192.168.1.XXX:5000/"
```

Para obtener tu IP local:
- Windows: `ipconfig` en cmd
- Linux/Mac: `ifconfig` en terminal

### 3. Sincronizar y Compilar

1. Abre el proyecto en Android Studio
2. Sync Gradle (el IDE lo hará automáticamente)
3. Espera a que se descarguen todas las dependencias
4. Ejecuta la aplicación en un emulador o dispositivo físico

## Uso de la Aplicación

### Flujo de Usuario Regular

1. **Registro/Login**: 
   - Al abrir la app, inicia sesión o regístrate
   - La sesión se mantiene persistente

2. **Búsqueda de Libros**:
   - En la pantalla principal, usa la barra de búsqueda
   - Busca por título o autor
   - Los resultados provienen de Open Library API

3. **Agregar a Favoritos**:
   - Toca el botón "♥ Favorito" en cualquier libro
   - Se sincroniza con el servidor automáticamente

4. **Ver Favoritos**:
   - Toca el chip "Favoritos"
   - Puedes quitar libros de favoritos

5. **Ver Historial**:
   - Toca el chip "Historial"
   - Verás todas tus búsquedas pasadas

6. **Ver Recomendaciones**:
   - Toca el chip "Recomendaciones"
   - Verás sugerencias basadas en tus favoritos y búsquedas

### Flujo de Administrador

1. **Login como admin** (usuario: admin, contraseña: admin123)
2. En el menú superior, toca el icono de administrador
3. En el panel de admin puedes ver:
   - **Estadísticas**: Totales y promedios
   - **Usuarios**: Lista de todos los usuarios
   - **Favoritos**: Favoritos de todos los usuarios
   - **Historial**: Búsquedas de todos los usuarios

## Tecnologías Utilizadas

### Backend (Flask)
- Flask
- Flask-SQLAlchemy (Room)
- Flask-Bcrypt (encriptación de contraseñas)
- SQLite

### Android
- Kotlin
- Room Database (persistencia local)
- Retrofit + OkHttp (consumo de APIs)
- Coroutines (programación asíncrona)
- ViewBinding
- Material Design 3
- Glide (carga de imágenes)
- Gson (parsing JSON)

## Sincronización de Datos

La aplicación implementa un sistema de sincronización bidireccional:

1. **Operaciones online**: Si hay conexión, se guardan tanto local como remotamente
2. **Operaciones offline**: Si no hay conexión, se guardan localmente con flag `synced=false`
3. **Sincronización automática**: Al recuperar la conexión, los datos pendientes se sincronizan
4. **Cache local**: Los libros buscados se cachean para acceso offline

## Funcionalidades Destacadas

- ✅ Persistencia de sesión (el usuario no necesita loguearse cada vez)
- ✅ Funcionamiento offline completo
- ✅ Sincronización automática cuando hay conexión
- ✅ Sistema de recomendaciones inteligente
- ✅ Panel completo de administración
- ✅ Interfaz Material Design moderna
- ✅ Búsqueda por título y autor
- ✅ Gestión completa de favoritos
- ✅ Historial detallado de búsquedas
- ✅ Manejo robusto de errores

## Notas Importantes

1. **Conexión de red**: Asegúrate de que tu dispositivo/emulador pueda acceder a la API Flask
2. **Primera ejecución**: La primera vez puede tardar en cargar las imágenes
3. **Permisos**: La app requiere permiso de INTERNET (ya configurado en el Manifest)
4. **Base de datos**: Se crea automáticamente al iniciar la app
5. **Usuario admin**: Creado automáticamente al iniciar el servidor Flask

## Solución de Problemas

**Error de conexión:**
- Verifica que la API Flask esté corriendo
- Confirma que la URL en `RetrofitClient.kt` sea correcta
- Si usas dispositivo físico, verifica que esté en la misma red WiFi

**La app crashea al iniciar:**
- Limpia el proyecto: Build > Clean Project
- Rebuild: Build > Rebuild Project
- Sincroniza Gradle

**No se muestran las imágenes:**
- Las URLs de Open Library a veces tardan
- Verifica tu conexión a internet

## Autor

Desarrollado como proyecto académico cumpliendo los requisitos de los ejercicios 1, 2 y 3.
