# Guía de Despliegue y Pruebas

## Pasos para Ejecutar el Proyecto Completo

### Paso 1: Preparar el Backend (API Flask)

1. **Navegar a la carpeta del backend:**
```bash
cd d:\Android\AndroidStudioProjects\Login-Flask
```

2. **Instalar dependencias de Python:**
```bash
pip install flask flask-sqlalchemy flask-bcrypt
```

3. **Ejecutar el servidor:**
```bash
python app.py
```

Deberías ver:
```
* Running on http://0.0.0.0:5000
* Debug mode: on
Usuario admin creado - username: admin, password: admin123
```

4. **Verificar que funciona:**
Abre un navegador y ve a: `http://localhost:5000`
Deberías ver: `{"message": "API Funcionando"}`

### Paso 2: Configurar la Aplicación Android

1. **Abrir el proyecto en Android Studio:**
   - Abre Android Studio
   - File > Open
   - Selecciona la carpeta `d:\Android\AndroidStudioProjects\Action`

2. **Actualizar la URL de la API:**
   
   Abre el archivo:
   `app/src/main/java/com/example/action/data/remote/RetrofitClient.kt`
   
   **Si vas a usar el emulador de Android Studio:**
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:5000/"
   ```
   ✅ Esta configuración ya está por defecto
   
   **Si vas a usar un dispositivo físico:**
   ```kotlin
   // Reemplaza XXX.XXX.XXX.XXX con tu IP local
   private const val BASE_URL = "http://XXX.XXX.XXX.XXX:5000/"
   ```
   
   Para encontrar tu IP:
   - Windows: Abre cmd y ejecuta `ipconfig`, busca "IPv4 Address"
   - Mac/Linux: Abre terminal y ejecuta `ifconfig` o `ip addr`

3. **Sincronizar Gradle:**
   - Espera a que Android Studio sincronice automáticamente
   - O haz clic en el icono de elefante (Sync Project with Gradle Files)

4. **Compilar y Ejecutar:**
   - Asegúrate de tener un emulador configurado o conecta un dispositivo físico
   - Haz clic en el botón "Run" (▶️) o presiona Shift+F10
   - Selecciona tu dispositivo/emulador

### Paso 3: Probar la Aplicación

#### Test 1: Registro y Login

1. **Registro de nuevo usuario:**
   - Toca "¿No tienes cuenta? Regístrate"
   - Ingresa un nombre de usuario (ej: "testuser")
   - Ingresa una contraseña (mínimo 6 caracteres)
   - Confirma la contraseña
   - Toca "Registrarse"
   - Deberías ver: "Usuario creado exitosamente"

2. **Login:**
   - Vuelve a la pantalla de login
   - Ingresa el usuario y contraseña creados
   - Toca "Iniciar Sesión"
   - Deberías entrar a la pantalla principal

#### Test 2: Búsqueda de Libros

1. **Buscar por título:**
   - En la barra de búsqueda, escribe "Harry Potter"
   - Toca "Buscar Libro"
   - Deberías ver una lista de libros relacionados

2. **Buscar por autor:**
   - En la barra de búsqueda, escribe "Tolkien"
   - Toca "Buscar Autor"
   - Deberías ver libros de ese autor

#### Test 3: Gestión de Favoritos

1. **Agregar a favoritos:**
   - Busca cualquier libro
   - Toca el botón "♥ Favorito"
   - Deberías ver: "Agregado a favoritos"

2. **Ver favoritos:**
   - Toca el chip "Favoritos"
   - Deberías ver el libro que agregaste

3. **Quitar de favoritos:**
   - En la vista de favoritos, toca "Quitar"
   - El libro se eliminará de la lista

#### Test 4: Historial y Recomendaciones

1. **Ver historial:**
   - Realiza varias búsquedas
   - Toca el chip "Historial"
   - Deberías ver todas tus búsquedas con el tipo (book/author)

2. **Ver recomendaciones:**
   - Asegúrate de tener algunos favoritos
   - Toca el chip "Recomendaciones"
   - Deberías ver autores recomendados y búsquedas recientes

#### Test 5: Panel de Administrador

1. **Login como admin:**
   - Cierra sesión (menú > Cerrar Sesión)
   - Login con:
     - Usuario: `admin`
     - Contraseña: `admin123`

2. **Acceder al panel:**
   - Toca el icono de admin en la barra superior
   - Deberías entrar al panel de administración

3. **Explorar estadísticas:**
   - Por defecto verás estadísticas generales
   - Toca "Usuarios" para ver todlos los usuarios
   - Toca "Favoritos" para ver favoritos de todos
   - Toca "Historial" para ver búsquedas de todos

#### Test 6: Funcionalidad Offline

1. **Agregar favoritos con conexión:**
   - Busca y agrega algunos libros a favoritos
   
2. **Desactivar conexión:**
   - Desactiva WiFi y datos móviles
   - O detén el servidor Flask

3. **Probar offline:**
   - Intenta agregar más favoritos
   - Los favoritos se guardarán localmente
   - Ve a la sección de favoritos
   - Deberías ver todos los favoritos (los guardados con y sin conexión)

4. **Reactivar conexión:**
   - Activa WiFi o datos
   - Reinicia el servidor Flask si lo detuviste
   - Los datos se sincronizarán automáticamente

### Verificar en la Base de Datos del Servidor

Puedes verificar que los datos se guardaron en el servidor:

1. **Ver la base de datos:**
```bash
cd d:\Android\AndroidStudioProjects\Login-Flask
python
```

2. **En el intérprete de Python:**
```python
from app import app, db, User, Favorite, SearchHistory
with app.app_context():
    # Ver usuarios
    users = User.query.all()
    for u in users:
        print(f"Usuario: {u.username}, Admin: {u.is_admin}")
    
    # Ver favoritos
    favs = Favorite.query.all()
    for f in favs:
        print(f"Favorito: {f.title} - Usuario: {f.user_id}")
    
    # Ver historial
    history = SearchHistory.query.all()
    for h in history:
        print(f"Búsqueda: {h.query} - Usuario: {h.user_id}")
```

## Endpoints de la API

Puedes probar los endpoints con herramientas como Postman o curl:

### Registro
```bash
curl -X POST http://localhost:5000/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"test123"}'
```

### Login
```bash
curl -X POST http://localhost:5000/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser2","password":"test123"}'
```

### Obtener Favoritos
```bash
curl http://localhost:5000/favorites/1
```

### Obtener Recomendaciones
```bash
curl http://localhost:5000/recommendations/1
```

### Admin - Estadísticas
```bash
curl http://localhost:5000/admin/stats
```

## Solución de Problemas Comunes

### Error: "Unable to resolve host"
- **Causa**: La app no puede conectarse a la API
- **Solución**: 
  - Verifica que el servidor Flask esté corriendo
  - Confirma la URL en `RetrofitClient.kt`
  - Si usas dispositivo físico, verifica que esté en la misma red

### Error: "Failed to resolve: libs"
- **Causa**: Gradle no pudo sincronizar
- **Solución**:
  - File > Invalidate Caches / Restart
  - Rebuild Project

### La app crashea al buscar libros
- **Causa**: Problema con la conexión a Open Library
- **Solución**: 
  - Verifica tu conexión a internet
  - Espera unos segundos, la API de Open Library puede ser lenta

### No se muestran imágenes de libros
- **Causa**: URLs de Open Library lentas o inválidas
- **Solución**: 
  - Es normal, algunas imágenes no están disponibles
  - Glide mostrará un placeholder

### "Credenciales inválidas" al hacer login
- **Causa**: Usuario/contraseña incorrectos o no registrado
- **Solución**:
  - Registra el usuario primero
  - Verifica que el servidor Flask esté corriendo
  - Revisa los logs del servidor

## Logs y Debugging

### Ver logs de Android:
En Android Studio, ve a la pestaña "Logcat" para ver los logs de la app.

Filtra por:
- `OkHttp` - Para ver las llamadas HTTP
- `Room` - Para ver las operaciones de base de datos
- Tag de tu app para ver logs generales

### Ver logs del servidor Flask:
El servidor imprime en consola todas las peticiones recibidas.

## Checklist de Verificación Final

Antes de entregar, verifica que:

- [ ] El servidor Flask inicia correctamente
- [ ] Se crea el usuario admin automáticamente
- [ ] La app se conecta a la API (login funciona)
- [ ] Puedes registrar nuevos usuarios
- [ ] La búsqueda de libros funciona (Open Library API)
- [ ] Puedes agregar libros a favoritos
- [ ] Los favoritos se muestran correctamente
- [ ] El historial guarda las búsquedas
- [ ] Las recomendaciones se generan
- [ ] El panel de admin muestra datos
- [ ] La persistencia de sesión funciona (al cerrar y abrir la app sigues logueado)
- [ ] Los datos se guardan localmente (funciona offline)
- [ ] Los datos se sincronizan al volver online

## Mejoras Opcionales

Si quieres mejorar la app, considera:

1. **Autenticación con Google**: Integrar Google Sign-In
2. **Notificaciones**: Notificar nuevas recomendaciones
3. **Compartir**: Compartir libros favoritos con otros usuarios
4. **Calificaciones**: Sistema de calificación de libros
5. **Comentarios**: Permitir comentarios en libros
6. **Temas**: Tema claro/oscuro
7. **Paginación**: Cargar más resultados al hacer scroll
8. **Filtros**: Filtrar por género, año, etc.

¡Buena suerte con tu proyecto! 🚀
