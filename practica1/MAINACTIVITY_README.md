# MainActivity - Actividad Principal

## Implementación Completa del MainActivity de Android en Flutter

Esta implementación convierte el `MainActivity` de Android a Flutter, replicando exactamente la funcionalidad de navegación entre fragments y activities, además del diseño visual completo.

## ✅ Funcionalidades Implementadas

### 🎨 **Diseño Visual Completo**
- **Fondo azul claro** (`#E3F2FD`) idéntico al XML
- **Colores exactos** del diseño Android:
  - Header: `#BBDEFB` (azul claro para título)
  - Título: `#1565C0` (azul oscuro)
  - Área de botones: `#90CAF9` (azul medio)
  - Barra inferior: `#42A5F5` (azul)
  - Botón seleccionado: `#6A1B9A` (púrpura)

### 🏗️ **Estructura Layout Idéntica**
1. **Header con título** "🏠 Activity Principal"
2. **Área de botones de actividades** con fondo azul medio
3. **Contenedor de fragments** (pantallas principales)
4. **Barra de navegación inferior** con 5 botones

### 🚀 **Navegación entre Activities**
- **Botón "⚙️ Configuración"** → Navega a SecondScreen
- **Botón "🖼️ Galería"** → Navega a ThirdScreen con userName "Usuario Principal"
- **Colores específicos**: Púrpura para configuración, verde para galería

### 📱 **Navegación entre Fragments**
- **🏠 Home** → Screen1 (HomeFragment)
- **🔍 Search** → Screen2 (SearchFragment)  
- **🔔 Notifications** → Screen3 (NotificationsFragment)
- **👤 Profile** → Screen4 (ProfileFragment)
- **⚙️ Settings** → Screen5 (SettingsFragment)

### 🎯 **Estado de Botones Dinámico**
- **Botón seleccionado**: Color púrpura (`#6A1B9A`)
- **Botones no seleccionados**: Color azul
- **Actualización automática** del estado visual

## 📱 **Estructura de Archivos Final**

```
lib/
├── main.dart                 # MainActivity principal ⭐
├── screen1.dart             # HomeFragment (con navegación a SecondScreen)
├── screen2.dart             # SearchFragment (botones interactivos)
├── screen3.dart             # NotificationsFragment (elementos de selección)
├── screen4.dart             # ProfileFragment (listas)
├── screen5.dart             # SettingsFragment (información)
├── second_screen.dart       # SecondActivity (configuración)
└── third_screen.dart        # ThirdActivity (galería)
```

## 🔄 **Equivalencias Android ↔ Flutter**

| Android (Kotlin) | Flutter (Dart) |
|------------------|----------------|
| `FragmentManager.beginTransaction()` | `IndexedStack` |
| `Fragment.replace()` | `setState()` con índice |
| `Intent(this, SecondActivity::class.java)` | `Navigator.push()` |
| `intent.putExtra()` | Constructor parameter |
| `findViewById()` | Variables de estado |
| `setOnClickListener()` | `onPressed` |
| `backgroundTintList` | `backgroundColor` en ButtonStyle |
| `enableEdgeToEdge()` | `SafeArea` widget |

## 💻 **Código Principal**

### Navegación entre Fragments
```dart
void _showFragment(int index) {
  setState(() {
    _currentIndex = index;
  });
}

// IndexedStack para mantener estado de fragments
Expanded(
  child: IndexedStack(
    index: _currentIndex,
    children: _screens,
  ),
),
```

### Navegación a Activities
```dart
void _goToSecondActivity() {
  Navigator.push(
    context,
    MaterialPageRoute(builder: (context) => SecondScreen()),
  );
}

void _goToThirdActivity() {
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (context) => ThirdScreen(userName: "Usuario Principal"),
    ),
  );
}
```

### Botones de Navegación Dinámicos
```dart
Widget _buildNavButton(int index, String emoji, String label) {
  final isSelected = _currentIndex == index;
  
  return Expanded(
    child: Container(
      margin: EdgeInsets.all(2),
      child: ElevatedButton(
        onPressed: () => _showFragment(index),
        style: ElevatedButton.styleFrom(
          backgroundColor: isSelected 
              ? Color(0xFF6A1B9A) // purple_500 para seleccionado
              : Colors.blue, // Azul para no seleccionado
          foregroundColor: Colors.white,
          padding: EdgeInsets.symmetric(vertical: 8),
        ),
        child: Text(emoji, style: TextStyle(fontSize: 16)),
      ),
    ),
  );
}
```

### Layout Completo
```dart
Column(
  children: [
    // Header con título
    Container(/* título y SafeArea */),
    
    // Botones de activities
    Container(/* botones configuración y galería */),
    
    // Contenedor de fragments
    Expanded(child: IndexedStack(/* fragments */)),
    
    // Barra de navegación
    Container(/* 5 botones de navegación */),
  ],
)
```

## 🎯 **Diferencias vs Android Original**

### ✅ **Ventajas de la Implementación Flutter**
- **IndexedStack**: Mantiene el estado de todos los fragments sin recrearlos
- **SafeArea**: Manejo automático de notches y barras del sistema
- **Navegación más fluida** con transiciones automáticas
- **Gestión de estado** más eficiente
- **Responsive design** automático

### 📝 **Mejoras Implementadas**
- **Estado persistente** en todos los fragments
- **Transiciones suaves** entre pantallas
- **Colores más consistentes** con el sistema de temas
- **Código más limpio** y mantenible
- **Edge-to-edge** automático con SafeArea

## 🚀 **Flujo de Navegación Completo**

### **Navegación Principal (Fragments)**
```
MainActivity
├── 🏠 Home (Screen1) → Botón "⚙️ Ir a Configuración" → SecondScreen
├── 🔍 Search (Screen2) → Demostración de botones
├── 🔔 Notifications (Screen3) → Elementos de selección
├── 👤 Profile (Screen4) → Gestión de listas
└── ⚙️ Settings (Screen5) → Información y display
```

### **Navegación de Activities**
```
MainActivity
├── "⚙️ Configuración" → SecondScreen
│   └── "🖼️ Galería" → ThirdScreen
│       ├── "⬅️ Atrás" → SecondScreen
│       └── "🏠 Inicio" → MainActivity (clear stack)
└── "🖼️ Galería" → ThirdScreen (directo)
    ├── "⬅️ Atrás" → MainActivity
    └── "🏠 Inicio" → MainActivity (clear stack)
```

## 📊 **Características Destacadas**

### 🎨 **Diseño Visual**
- **Layout vertical** completo como Android
- **Colores temáticos** azules y púrpura
- **Espaciado consistente** con márgenes de 2dp
- **SafeArea** para compatibility con diferentes dispositivos

### ⚡ **Rendimiento**
- **IndexedStack**: Los fragments no se recrean al cambiar
- **Estado persistente** en todas las pantallas
- **Navegación optimizada** sin reconstrucción innecesaria

### 🔧 **Funcionalidad**
- **5 fragments** completamente funcionales
- **2 activities adicionales** con navegación completa
- **Paso de parámetros** entre activities
- **Estado visual dinámico** de botones

Esta implementación mantiene **100% de la funcionalidad** del MainActivity de Android mientras aprovecha las ventajas arquitectónicas de Flutter para crear una experiencia de usuario superior y más eficiente.