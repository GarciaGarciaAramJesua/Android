@echo off
echo ====================================
echo Obteniendo SHA-1 para Google Maps
echo ====================================
echo.

set KEYSTORE=%USERPROFILE%\.android\debug.keystore

if not exist "%KEYSTORE%" (
    echo ERROR: No se encontro el keystore de debug en:
    echo %KEYSTORE%
    echo.
    echo Ejecuta tu app al menos una vez para que Android Studio genere el keystore.
    pause
    exit /b 1
)

echo Keystore encontrado: %KEYSTORE%
echo.
echo Ejecutando keytool...
echo.

keytool -list -v -keystore "%KEYSTORE%" -alias androiddebugkey -storepass android -keypass android

echo.
echo ====================================
echo INSTRUCCIONES:
echo ====================================
echo.
echo 1. Copia el valor SHA1 que aparece arriba
echo 2. Ve a: https://console.cloud.google.com/
echo 3. Selecciona tu proyecto
echo 4. Ve a "APIs y servicios" - "Credenciales"
echo 5. Haz clic en tu API Key
echo 6. En "Restricciones de aplicacion":
echo    - Selecciona "Aplicaciones de Android"
echo    - Agrega:
echo      * Nombre del paquete: com.example.examen
echo      * SHA-1: [el valor que copiaste]
echo 7. En "Restricciones de API":
echo    - Marca "Maps SDK for Android"
echo 8. Guarda y espera 5 minutos
echo.
pause
