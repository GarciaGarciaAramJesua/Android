import 'package:flutter/material.dart';
import 'third_screen.dart';

class SecondScreen extends StatefulWidget {
  @override
  _SecondScreenState createState() => _SecondScreenState();
}

class _SecondScreenState extends State<SecondScreen> {
  // Controladores para los campos de texto
  final TextEditingController _userNameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();

  // Variables para el Spinner y Switch
  String _selectedTheme = 'Claro';
  bool _notificationsEnabled = true;

  // Variable para mostrar/ocultar resultados
  bool _showResults = false;
  String _resultText = '';

  // Lista de temas para el Spinner
  final List<String> _themes = [
    'Claro',
    'Oscuro',
    'Automático',
    'Azul',
    'Verde',
  ];

  @override
  void dispose() {
    _userNameController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFF3E5F5), // Fondo púrpura claro como en XML
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Título del Activity
            Text(
              '⚙️ Configuración de Usuario',
              style: TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: Color(0xFF4A148C), // Púrpura oscuro
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 20),

            Text(
              'Configura tu perfil y preferencias de la aplicación',
              style: TextStyle(
                fontSize: 16,
                color: Color(0xFF7B1FA2), // Púrpura medio
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 30),

            // Sección: Información Personal
            Text(
              '👤 Información Personal',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Color(0xFF6A1B9A), // Púrpura
              ),
            ),
            SizedBox(height: 12),

            // Campo: Nombre de usuario
            TextField(
              controller: _userNameController,
              style: TextStyle(fontSize: 16),
              decoration: InputDecoration(
                hintText: 'Nombre de usuario',
                contentPadding: EdgeInsets.all(12),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(4),
                  borderSide: BorderSide(color: Colors.grey),
                ),
                filled: true,
                fillColor: Colors.white,
              ),
            ),
            SizedBox(height: 16),

            // Campo: Email
            TextField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              style: TextStyle(fontSize: 16),
              decoration: InputDecoration(
                hintText: 'Correo electrónico',
                contentPadding: EdgeInsets.all(12),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(4),
                  borderSide: BorderSide(color: Colors.grey),
                ),
                filled: true,
                fillColor: Colors.white,
              ),
            ),
            SizedBox(height: 20),

            // Sección: Preferencias de Apariencia
            Text(
              '🎨 Preferencias de Apariencia',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Color(0xFF6A1B9A), // Púrpura
              ),
            ),
            SizedBox(height: 12),

            Text(
              'Selecciona tu tema preferido:',
              style: TextStyle(fontSize: 14),
            ),
            SizedBox(height: 8),

            // Spinner (DropdownButton)
            Container(
              width: double.infinity,
              padding: EdgeInsets.symmetric(horizontal: 12),
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
                borderRadius: BorderRadius.circular(4),
                color: Colors.white,
              ),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<String>(
                  value: _selectedTheme,
                  isExpanded: true,
                  items: _themes.map((String theme) {
                    return DropdownMenuItem<String>(
                      value: theme,
                      child: Text(theme, style: TextStyle(fontSize: 16)),
                    );
                  }).toList(),
                  onChanged: (String? newValue) {
                    setState(() {
                      _selectedTheme = newValue ?? 'Claro';
                    });
                  },
                ),
              ),
            ),
            SizedBox(height: 20),

            // Switch para notificaciones
            Row(
              children: [
                Switch(
                  value: _notificationsEnabled,
                  onChanged: (bool value) {
                    setState(() {
                      _notificationsEnabled = value;
                    });
                  },
                ),
                SizedBox(width: 8),
                Text(
                  '🔔 Recibir notificaciones',
                  style: TextStyle(fontSize: 16),
                ),
              ],
            ),
            SizedBox(height: 30),

            // Botones de acción
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _saveConfiguration,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF4CAF50), // Verde
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '💾 Guardar',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
                SizedBox(width: 24),
                ElevatedButton(
                  onPressed: _goToThirdScreen,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFFFF9800), // Naranja
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '🖼️ Galería',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ],
            ),
            SizedBox(height: 20),

            // Botón volver
            ElevatedButton(
              onPressed: _goBack,
              style: ElevatedButton.styleFrom(
                backgroundColor: Color(0xFF9C27B0), // Púrpura
                padding: EdgeInsets.symmetric(vertical: 12),
              ),
              child: Text(
                '⬅️ Volver al Inicio',
                style: TextStyle(color: Colors.white),
              ),
            ),
            SizedBox(height: 20),

            // Área de resultados
            if (_showResults)
              Container(
                width: double.infinity,
                padding: EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Color(0xFFE8F5E8), // Fondo verde claro
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  _resultText,
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xFF2E7D32), // Verde oscuro
                    height: 1.3, // lineSpacingExtra equivalente
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  void _saveConfiguration() {
    String userName = _userNameController.text.trim();
    String email = _emailController.text.trim();

    if (userName.isEmpty || email.isEmpty) {
      _showSnackBar('Por favor completa todos los campos');
      return;
    }

    String result =
        '''✅ Configuración Guardada:

👤 Usuario: $userName
📧 Email: $email
🎨 Tema: $_selectedTheme
🔔 Notificaciones: ${_notificationsEnabled ? "Activadas" : "Desactivadas"}''';

    setState(() {
      _resultText = result;
      _showResults = true;
    });

    _showSnackBar('¡Configuración guardada exitosamente!');
  }

  void _goToThirdScreen() {
    String userName = _userNameController.text.toString();

    // Navegación a la tercera pantalla pasando el userName
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => ThirdScreen(userName: userName)),
    );
  }

  void _goBack() {
    Navigator.pop(context); // Volver a la pantalla anterior
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: Duration(seconds: 2)),
    );
  }
}
