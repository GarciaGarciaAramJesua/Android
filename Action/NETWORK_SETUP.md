# Configuración de IP para Conexión API

## 📡 Configurar la Conexión Correctamente

La configuración más importante es asegurarte de que la app Android pueda conectarse a tu API Flask.

### Archivo a Modificar

`app/src/main/java/com/example/action/data/remote/RetrofitClient.kt`

### Opciones Según tu Escenario

#### Opción 1: Usando Emulador de Android Studio (RECOMENDADO)

```kotlin
private const val BASE_URL = "http://10.0.2.2:5000/"
```

**Explicación:** `10.0.2.2` es una IP especial del emulador que apunta al `localhost` de tu computadora.

✅ **Esta es la configuración por defecto del proyecto**

---

#### Opción 2: Usando Dispositivo Físico (Mismo WiFi)

**Paso 1:** Encuentra tu IP local

**En Windows:**
```bash
ipconfig
```
Busca "Dirección IPv4" en tu adaptador de red WiFi
Ejemplo: `192.168.1.105`

**En Mac/Linux:**
```bash
ifconfig
# o
ip addr show
```
Busca la IP en tu interfaz de red WiFi

**Paso 2:** Actualiza el código

```kotlin
// Reemplaza XXX.XXX.XXX.XXX con tu IP
private const val BASE_URL = "http://192.168.1.105:5000/"
```

**Paso 3:** Asegúrate de que tu teléfono y computadora estén en la misma red WiFi

---

#### Opción 3: Usando ngrok (Para Probar Desde Cualquier Lugar)

Si quieres probar desde un dispositivo que no está en tu red local:

**Paso 1:** Instala ngrok
- Descarga de: https://ngrok.com/download
- Descomprime el archivo

**Paso 2:** Expón tu servidor Flask
```bash
ngrok http 5000
```

**Paso 3:** Copia la URL que te da ngrok
Ejemplo: `https://xxxx-xxx-xxx-xxx.ngrok.io`

**Paso 4:** Actualiza el código
```kotlin
private const val BASE_URL = "https://xxxx-xxx-xxx-xxx.ngrok.io/"
```

⚠️ **Nota:** La URL de ngrok cambia cada vez que lo reinicias (versión gratuita)

---

## 🔧 Configuración Completa del Servidor Flask

### Permitir Conexiones Externas

Si Flask solo acepta conexiones locales, modifica `app.py`:

```python
# Al final del archivo
if __name__ == '__main__':
    with app.app_context():
        db.create_all()
        
        admin = User.query.filter_by(username='admin').first()
        if not admin:
            hashed_password = bcrypt.generate_password_hash('admin123').decode('utf-8')
            admin = User(username='admin', password=hashed_password, is_admin=True)
            db.session.add(admin)
            db.session.commit()
            print("Usuario admin creado - username: admin, password: admin123")
    
    # Escucha en todas las interfaces (0.0.0.0)
    app.run(host='0.0.0.0', port=5000, debug=True)
```

✅ **Esta configuración ya está incluida en el archivo app.py actualizado**

---

## 🔥 Firewall de Windows

Si usas Windows y un dispositivo físico, puede que el firewall bloquee las conexiones:

**Opción A: Permitir temporalmente**
```bash
# Ejecutar como Administrador en PowerShell
New-NetFirewallRule -DisplayName "Flask Dev Server" -Direction Inbound -LocalPort 5000 -Protocol TCP -Action Allow
```

**Opción B: Manual**
1. Panel de Control > Sistema y Seguridad > Firewall de Windows
2. Configuración avanzada
3. Reglas de entrada > Nueva regla
4. Puerto > TCP > Puerto específico: 5000
5. Permitir la conexión
6. Aplicar a todos los perfiles
7. Nombre: "Flask Server"

---

## ✅ Verificar la Conexión

### Desde tu Computadora

**Probar en el navegador:**
```
http://localhost:5000
```

Deberías ver:
```json
{"message": "API Funcionando"}
```

**Probar con curl:**
```bash
curl http://localhost:5000
```

### Desde tu Dispositivo Móvil

**Probar en el navegador del teléfono:**

Si usas la IP local (ej: 192.168.1.105):
```
http://192.168.1.105:5000
```

Deberías ver el mensaje JSON. Si no funciona:
- Verifica que estés en la misma red WiFi
- Verifica el firewall
- Verifica que la IP sea correcta

---

## 📱 Configuración del Emulador Android

### Crear un Emulador

1. En Android Studio: Tools > Device Manager
2. Create Device
3. Selecciona un dispositivo (ej: Pixel 5)
4. Selecciona una imagen del sistema (API 30+ recomendado)
5. Finish

### Configuraciones Recomendadas del Emulador

- **RAM:** Mínimo 2GB, recomendado 4GB
- **Almacenamiento:** Mínimo 2GB
- **Graphics:** Automatic o Hardware

---

## 🐛 Troubleshooting

### Error: "Failed to connect to /10.0.2.2:5000"

**Causa:** El servidor Flask no está corriendo
**Solución:** Inicia el servidor Flask

```bash
cd Login-Flask
python app.py
```

---

### Error: "Unable to resolve host"

**Causa:** La URL está mal configurada o no hay conexión
**Solución:** 
1. Verifica la URL en `RetrofitClient.kt`
2. Prueba la URL en el navegador primero
3. Verifica tu conexión a internet

---

### Error: "Network security policy"

**Causa:** Android no permite HTTP en producción por defecto
**Solución:** Ya está configurado en el AndroidManifest.xml

```xml
android:usesCleartextTraffic="true"
```

✅ **Ya incluido en el proyecto**

---

### La app se cierra al buscar libros

**Causa:** Problema con Open Library API o sin internet
**Solución:**
1. Verifica tu conexión a internet
2. Revisa los logs en Logcat
3. Open Library API puede estar lenta, espera y reintenta

---

### No aparecen las imágenes

**Causa:** URLs de imágenes inválidas o lentas
**Solución:** Es normal, Glide muestra un placeholder. No todas las imágenes están disponibles en Open Library.

---

## 📋 Checklist de Conexión

Antes de ejecutar la app, verifica:

- [ ] Servidor Flask está corriendo
- [ ] Puedes acceder a `http://localhost:5000` en tu navegador
- [ ] La URL en `RetrofitClient.kt` es correcta para tu escenario
- [ ] Si usas dispositivo físico, estás en la misma red WiFi
- [ ] El firewall permite conexiones al puerto 5000
- [ ] `usesCleartextTraffic="true"` está en el AndroidManifest

---

## 🚀 Configuración Rápida Recomendada

Para la forma más sencilla de probar:

1. **Usa el emulador de Android Studio**
2. **Deja la configuración por defecto:**
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:5000/"
   ```
3. **Inicia Flask:**
   ```bash
   python app.py
   ```
4. **Run en Android Studio ▶️**

¡Listo! 🎉

---

## 📞 Resumen de URLs

| Escenario | URL en RetrofitClient.kt |
|-----------|--------------------------|
| Emulador Android Studio | `http://10.0.2.2:5000/` ✅ Por defecto |
| Dispositivo físico (misma red) | `http://TU_IP_LOCAL:5000/` |
| Con ngrok | `https://xxxx.ngrok.io/` |
| Producción futura | `https://tu-dominio.com/api/` |

---

¿Aún tienes problemas? Revisa los logs:
- Android: Logcat en Android Studio
- Flask: Consola donde ejecutaste `python app.py`
