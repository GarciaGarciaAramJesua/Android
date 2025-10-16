# Comandos Útiles - Gestor de Archivos

## Gradle

### Limpiar proyecto
```bash
./gradlew clean
```

### Compilar APK Debug
```bash
./gradlew assembleDebug
```

### Compilar APK Release
```bash
./gradlew assembleRelease
```

### Instalar en dispositivo conectado
```bash
./gradlew installDebug
```

### Ejecutar tests
```bash
./gradlew test
```

### Ver dependencias
```bash
./gradlew app:dependencies
```

## Android Debug Bridge (ADB)

### Ver dispositivos conectados
```bash
adb devices
```

### Instalar APK
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Ver logs en tiempo real
```bash
adb logcat
```

### Filtrar logs de la app
```bash
adb logcat | findstr "GestorDeArchivos"
```

### Limpiar datos de la app
```bash
adb shell pm clear com.escom.gestordearchivos
```

### Otorgar permisos manualmente
```bash
adb shell pm grant com.escom.gestordearchivos android.permission.READ_EXTERNAL_STORAGE
adb shell pm grant com.escom.gestordearchivos android.permission.WRITE_EXTERNAL_STORAGE
```

### Tomar screenshot
```bash
adb shell screencap /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

## Git

### Inicializar repositorio
```bash
git init
```

### Agregar archivos
```bash
git add .
```

### Commit
```bash
git commit -m "Implementación inicial del Gestor de Archivos"
```

### Push a GitHub
```bash
git push origin main
```

### Ver estado
```bash
git status
```

### Ver historial
```bash
git log --oneline
```

## Ubicación del APK

### Debug APK
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```
app/build/outputs/apk/release/app-release.apk
```

## Tareas Comunes

### 1. Compilar y probar en emulador
```bash
./gradlew assembleDebug
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. Generar APK firmado para distribución
1. Build → Generate Signed Bundle/APK
2. Seleccionar APK
3. Crear o usar keystore existente
4. Seleccionar Release
5. APK generado en: app/release/app-release.apk

### 3. Tomar capturas de pantalla para documentación
```bash
# En emulador o dispositivo, navegar a la pantalla deseada
adb shell screencap /sdcard/screenshot.png
adb pull /sdcard/screenshot.png screenshots/nombre_descriptivo.png
```

### 4. Ver errores de compilación
```bash
./gradlew assembleDebug --stacktrace
```

### 5. Sincronizar proyecto con Gradle
En Android Studio:
- File → Sync Project with Gradle Files
- O presionar Ctrl+Shift+O (Windows/Linux) / Cmd+Shift+O (Mac)

## Solución de Problemas Comunes

### Error: "SDK location not found"
```bash
# Crear archivo local.properties con:
sdk.dir=C\:\\Users\\[TU_USUARIO]\\AppData\\Local\\Android\\Sdk
```

### Error: "Kotlin plugin version mismatch"
```bash
./gradlew clean
# Luego sincronizar proyecto en Android Studio
```

### Error: "AAPT2 error"
```bash
# En gradle.properties agregar:
android.enableAapt2=false
```

### App se cierra al abrir
```bash
# Ver logs para identificar el error:
adb logcat | findstr "AndroidRuntime"
```

## Shortcuts de Android Studio

| Acción | Windows/Linux | Mac |
|--------|--------------|-----|
| Sync Gradle | Ctrl+Shift+O | Cmd+Shift+O |
| Build APK | Ctrl+F9 | Cmd+F9 |
| Run App | Shift+F10 | Ctrl+R |
| Debug App | Shift+F9 | Ctrl+D |
| Buscar archivo | Ctrl+Shift+N | Cmd+Shift+O |
| Buscar en archivos | Ctrl+Shift+F | Cmd+Shift+F |
| Refactorizar | Ctrl+Alt+Shift+T | Ctrl+T |

## Testing

### Ejecutar pruebas unitarias
```bash
./gradlew test
```

### Ejecutar pruebas de instrumentación
```bash
./gradlew connectedAndroidTest
```

### Ver reporte de cobertura
```bash
./gradlew jacocoTestReport
# Abrir: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Análisis de Código

### Lint Check
```bash
./gradlew lint
# Abrir: app/build/reports/lint-results.html
```

### Detekt (análisis estático Kotlin)
```bash
./gradlew detekt
```

## Documentación

### Generar KDoc
```bash
./gradlew dokkaHtml
# Abrir: app/build/dokka/html/index.html
```

## Notas Importantes

1. **Siempre hacer clean antes de compilar release**:
   ```bash
   ./gradlew clean assembleRelease
   ```

2. **Verificar permisos en AndroidManifest.xml** antes de distribuir

3. **Probar en múltiples dispositivos/emuladores** con diferentes versiones de Android

4. **Guardar keystore en lugar seguro** - Es necesario para futuras actualizaciones

5. **Mantener build.gradle actualizado** con las últimas versiones de dependencias (pero probando compatibilidad)
