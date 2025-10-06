import 'package:flutter/material.dart';
import 'screen1.dart';
import 'screen2.dart';
import 'screen3.dart';
import 'screen4.dart';
import 'screen5.dart';
import 'second_screen.dart';
import 'third_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MainActivity(),
    );
  }
}

class MainActivity extends StatefulWidget {
  const MainActivity({super.key});

  @override
  State<MainActivity> createState() => _MainActivityState();
}

class _MainActivityState extends State<MainActivity> {
  // Índice del fragmento/pantalla actual seleccionada (Home por defecto)
  int _currentIndex = 0;

  // Lista de widgets/pantallas disponibles (fragments equivalentes)
  final List<Widget> _screens = [
    Screen1(), // HomeFragment
    Screen2(), // SearchFragment
    Screen3(), // NotificationsFragment
    Screen4(), // ProfileFragment
    Screen5(), // SettingsFragment
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFE3F2FD), // Fondo azul claro como en Android
      body: Column(
        children: [
          // Título del MainActivity
          Container(
            width: double.infinity,
            padding: EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Color(0xFFBBDEFB), // Azul claro para header
            ),
            child: SafeArea(
              bottom: false,
              child: Text(
                '🏠 Activity Principal',
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF1565C0), // Azul oscuro
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ),

          // Botones para navegar a otros activities
          Container(
            width: double.infinity,
            padding: EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Color(0xFF90CAF9), // Azul medio para botones
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _goToSecondActivity,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF9C27B0), // Púrpura
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  ),
                  child: Text(
                    '⚙️ Configuración',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
                SizedBox(width: 16),
                ElevatedButton(
                  onPressed: _goToThirdActivity,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF4CAF50), // Verde
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  ),
                  child: Text(
                    '🖼️ Galería',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ],
            ),
          ),

          // Contenedor de fragments (pantallas)
          Expanded(
            child: IndexedStack(index: _currentIndex, children: _screens),
          ),

          // Barra de navegación inferior (como en Android)
          Container(
            width: double.infinity,
            padding: EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Color(0xFF42A5F5), // Azul para barra inferior
            ),
            child: SafeArea(
              top: false,
              child: Row(
                children: [
                  _buildNavButton(0, '🏠', 'Home'),
                  _buildNavButton(1, '🔍', 'Search'),
                  _buildNavButton(2, '🔔', 'Notifications'),
                  _buildNavButton(3, '👤', 'Profile'),
                  _buildNavButton(4, '⚙️', 'Settings'),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavButton(int index, String emoji, String label) {
    final isSelected = _currentIndex == index;

    return Expanded(
      child: Container(
        margin: EdgeInsets.all(2),
        child: ElevatedButton(
          onPressed: () => _showFragment(index),
          style: ElevatedButton.styleFrom(
            backgroundColor: isSelected
                ? Color(0xFF6A1B9A) // purple_500 equivalente para seleccionado
                : Colors.blue, // Azul para no seleccionado
            foregroundColor: Colors.white,
            padding: EdgeInsets.symmetric(vertical: 8),
            minimumSize: Size(0, 40),
          ),
          child: Text(
            emoji,
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.normal),
            textAlign: TextAlign.center,
          ),
        ),
      ),
    );
  }

  void _showFragment(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  void _goToSecondActivity() {
    // Navegar a SecondActivity (SecondScreen)
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => SecondScreen()),
    );
  }

  void _goToThirdActivity() {
    // Navegar a ThirdActivity (ThirdScreen) con userName como en Android
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ThirdScreen(userName: "Usuario Principal"),
      ),
    );
  }
}
