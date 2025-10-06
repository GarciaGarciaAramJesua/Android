# Second Screen - Configuración de Usuario

## Implementación de SecondActivity de Android en Flutter

Esta implementación convierte la `SecondActivity` de Android a Flutter como `SecondScreen`, replicando exactamente la funcionalidad y el diseño visual.

## ✅ Funcionalidades Implementadas

### 🎨 **Diseño Visual**
- **Fondo púrpura claro** (`#F3E5F5`) idéntico al XML
- **Colores exactos** del diseño Android:
  - Título principal: `#4A148C` (púrpura oscuro)
  - Subtítulo: `#7B1FA2` (púrpura medio)
  - Secciones: `#6A1B9A` (púrpura)
  - Botones con colores específicos (verde, naranja, púrpura)

### 📝 **Formulario de Configuración**
- **Campo de nombre de usuario** con validación
- **Campo de email** con tipo de teclado específico
- **Spinner/Dropdown** con 5 opciones de tema:
  - Claro (por defecto)
  - Oscuro
  - Automático
  - Azul
  - Verde
- **Switch** para activar/desactivar notificaciones (activado por defecto)

### 🔧 **Funcionalidades**
1. **Validación de formulario**: Verifica que los campos no estén vacíos
2. **Guardado de configuración**: Muestra resumen de la configuración guardada
3. **Navegación a ThirdScreen**: Pasa el nombre de usuario como parámetro
4. **Navegación de vuelta**: Regresa a la pantalla anterior
5. **Mensajes de feedback**: SnackBar para confirmaciones y errores

### 🚀 **Navegación**
- **Desde Screen1**: Botón "⚙️ Ir a Configuración"
- **A ThirdScreen**: Botón "🖼️ Galería" (incluye ThirdScreen básica)
- **Regreso**: Botón "⬅️ Volver al Inicio"

## 📱 **Estructura de Archivos**

```
lib/
├── main.dart                 # Navegación principal
├── screen1.dart             # Pantalla de inicio (con botón a SecondScreen)
├── screen2.dart             # Pantalla de botones
├── screen3.dart             # Pantalla de elementos de selección
├── screen4.dart             # Pantalla de listas
├── screen5.dart             # Pantalla de información
└── second_screen.dart       # Nueva pantalla de configuración ⭐
```

## 🔄 **Equivalencias Android ↔ Flutter**

| Android (Kotlin) | Flutter (Dart) |
|------------------|----------------|
| `EditText` | `TextField` |
| `Spinner` | `DropdownButton` |
| `Switch` | `Switch` |
| `Button` | `ElevatedButton` |
| `Toast` | `SnackBar` |
| `Intent.putExtra()` | `Navigator.push()` con parámetros |
| `findViewById()` | Variables de estado |
| `ArrayAdapter` | Lista de strings con `DropdownMenuItem` |

## 💻 **Código Principal**

### Controladores de Texto
```dart
final TextEditingController _userNameController = TextEditingController();
final TextEditingController _emailController = TextEditingController();
```

### Variables de Estado
```dart
String _selectedTheme = 'Claro';
bool _notificationsEnabled = true;
bool _showResults = false;
```

### Validación y Guardado
```dart
void _saveConfiguration() {
  String userName = _userNameController.text.trim();
  String email = _emailController.text.trim();

  if (userName.isEmpty || email.isEmpty) {
    _showSnackBar('Por favor completa todos los campos');
    return;
  }

  // Mostrar configuración guardada...
}
```

### Navegación con Parámetros
```dart
void _goToThirdScreen() {
  String userName = _userNameController.text.toString();
  
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (context) => ThirdScreen(userName: userName),
    ),
  );
}
```

## 🎯 **Diferencias vs Android Original**

### ✅ **Ventajas de la Implementación Flutter**
- **Mejor gestión de estado** con setState()
- **Navegación más fluida** con Navigator
- **Validación en tiempo real** posible
- **Colores más consistentes** con sistema de temas
- **Responsive design** automático

### 📝 **Mejoras Implementadas**
- **Disposición automática** de controladores de texto
- **Área de resultados** con mejor formateo visual
- **Feedback visual** mejorado con SnackBar
- **Navegación más intuitiva** con botones claramente etiquetados

## 🚀 **Cómo Usar**

1. **Desde la pantalla principal**: Navega a Screen1 (Home)
2. **Accede a configuración**: Toca "⚙️ Ir a Configuración"
3. **Completa el formulario**: Ingresa nombre y email
4. **Selecciona preferencias**: Elige tema y configuración de notificaciones
5. **Guarda la configuración**: Toca "💾 Guardar"
6. **Navega a galería** (opcional): Toca "🖼️ Galería"
7. **Regresa**: Toca "⬅️ Volver al Inicio"

Esta implementación mantiene 100% de la funcionalidad del código Android original mientras aprovecha las ventajas del framework Flutter.