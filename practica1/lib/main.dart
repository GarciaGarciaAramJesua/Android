import 'package:flutter/material.dart';
import 'screen1.dart';
import 'screen2.dart';
import 'screen3.dart';
import 'screen4.dart';
import 'screen5.dart';

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
      home: const MyHomePage(title: 'App Principal'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  // Índice del fragmento/pantalla actual seleccionada
  int _currentIndex = 0;

  // Lista de widgets/pantallas disponibles
  final List<Widget> _screens = [
    Screen1(), // Home
    Screen2(), // Search
    Screen3(), // Notifications
    Screen4(), // Profile
    Screen5(), // Settings
  ];

  // Lista de títulos para cada pantalla
  final List<String> _screenTitles = [
    'Inicio',
    'Búsqueda',
    'Notificaciones',
    'Perfil',
    'Configuración',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(_screenTitles[_currentIndex]),
        centerTitle: true,
      ),
      body: IndexedStack(index: _currentIndex, children: _screens),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: Color(0xFFF5F5F5), // Fondo gris claro como en Android
        ),
        padding: EdgeInsets.all(8.0),
        child: Row(
          children: List.generate(5, (index) {
            return Expanded(
              child: Container(
                margin: EdgeInsets.all(2.0), // Márgenes de 2dp como en Android
                child: TextButton(
                  onPressed: () => _showFragment(index),
                  style: TextButton.styleFrom(
                    backgroundColor: _currentIndex == index
                        ? Colors
                              .deepPurple // purple_500 equivalente
                        : Colors.deepPurple[200], // purple_200 equivalente
                    foregroundColor: Colors.white,
                    padding: EdgeInsets.symmetric(vertical: 12, horizontal: 4),
                    minimumSize: Size(0, 48), // Altura mínima para touch target
                  ),
                  child: Text(
                    _getButtonText(index),
                    style: TextStyle(
                      fontSize: 12, // 12sp como en Android
                      fontWeight: FontWeight.normal,
                    ),
                    textAlign: TextAlign.center,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ),
            );
          }),
        ),
      ),
    );
  }

  String _getButtonText(int index) {
    switch (index) {
      case 0:
        return 'Home';
      case 1:
        return 'Buscar';
      case 2:
        return 'Notif.';
      case 3:
        return 'Perfil';
      case 4:
        return 'Config';
      default:
        return '';
    }
  }

  void _showFragment(int index) {
    setState(() {
      _currentIndex = index;
    });
  }
}
