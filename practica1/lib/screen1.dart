import 'package:flutter/material.dart';

class Screen1 extends StatefulWidget {
  @override
  _Screen1State createState() => _Screen1State();
}

class _Screen1State extends State<Screen1> {
  // Controladores para los campos de entrada
  final TextEditingController _basicController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _numberController = TextEditingController();

  // Variable para controlar la visibilidad de los resultados
  bool _showResults = false;
  String _results = 'Los valores ingresados aparecerán aquí...';

  @override
  void dispose() {
    // Liberar recursos de los controladores
    _basicController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _numberController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Título principal
              Center(
                child: Text(
                  '📝 Demostración de TextFields',
                  style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                ),
              ),
              SizedBox(height: 16),

              // Descripción
              Container(
                padding: EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Color(0xFFF0F0F0),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  'Los TextFields (EditText) permiten al usuario ingresar y editar texto. Son elementos fundamentales para formularios, búsquedas y entrada de datos en aplicaciones Android.',
                  style: TextStyle(fontSize: 14, color: Color(0xFF333333)),
                  textAlign: TextAlign.center,
                ),
              ),
              SizedBox(height: 24),

              // Sección de ejemplos
              Text(
                '🎯 Ejemplos Interactivos:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 16),

              // 1. TextField básico
              Text(
                '1. TextField Básico:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              TextField(
                controller: _basicController,
                decoration: InputDecoration(
                  hintText: 'Escribe cualquier texto aquí...',
                  border: OutlineInputBorder(),
                  contentPadding: EdgeInsets.all(12),
                ),
              ),
              SizedBox(height: 16),

              // 2. TextField para email
              Text(
                '2. TextField para Email:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              TextField(
                controller: _emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: InputDecoration(
                  hintText: 'ejemplo@correo.com',
                  border: OutlineInputBorder(),
                  contentPadding: EdgeInsets.all(12),
                ),
              ),
              SizedBox(height: 16),

              // 3. TextField para contraseña
              Text(
                '3. TextField para Contraseña:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              TextField(
                controller: _passwordController,
                obscureText: true,
                decoration: InputDecoration(
                  hintText: 'Ingresa tu contraseña',
                  border: OutlineInputBorder(),
                  contentPadding: EdgeInsets.all(12),
                ),
              ),
              SizedBox(height: 16),

              // 4. TextField numérico
              Text(
                '4. TextField Numérico:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              TextField(
                controller: _numberController,
                keyboardType: TextInputType.number,
                decoration: InputDecoration(
                  hintText: 'Solo números (ej: 123)',
                  border: OutlineInputBorder(),
                  contentPadding: EdgeInsets.all(12),
                ),
              ),
              SizedBox(height: 16),

              // Demo Interactiva
              Text(
                '🚀 Demo Interactiva:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 16),

              // Botón Mostrar Valores
              ElevatedButton(
                onPressed: _showCurrentValues,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple, // purple_500 equivalente
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Mostrar Valores Ingresados',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              SizedBox(height: 16),

              // Botón Limpiar
              ElevatedButton(
                onPressed: _clearAllFields,
                style: ElevatedButton.styleFrom(
                  backgroundColor:
                      Colors.deepPurple[200], // purple_200 equivalente
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Limpiar Todos los Campos',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              SizedBox(height: 16),

              // Área de resultados
              if (_showResults)
                Container(
                  width: double.infinity,
                  padding: EdgeInsets.all(16),
                  margin: EdgeInsets.only(top: 16),
                  decoration: BoxDecoration(
                    color: Color(0xFFE8F5E8), // Fondo verde como en Android
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    _results,
                    style: TextStyle(
                      fontSize: 14,
                      color: Color(0xFF2E7D32), // Color verde oscuro
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  void _showCurrentValues() {
    final basicText = _basicController.text.trim();
    final emailText = _emailController.text.trim();
    final passwordText = _passwordController.text.trim();
    final numberText = _numberController.text.trim();

    final results = StringBuffer();
    results.write("📋 Valores Ingresados:\n\n");

    results.write("• Texto Básico: ");
    results.write(basicText.isEmpty ? "(vacío)" : "\"$basicText\"");
    results.write("\n\n");

    results.write("• Email: ");
    results.write(emailText.isEmpty ? "(vacío)" : emailText);
    results.write("\n\n");

    results.write("• Contraseña: ");
    results.write(
      passwordText.isEmpty
          ? "(vacía)"
          : "${"*" * passwordText.length} (${passwordText.length} caracteres)",
    );
    results.write("\n\n");

    results.write("• Número: ");
    results.write(numberText.isEmpty ? "(vacío)" : numberText);

    setState(() {
      _results = results.toString();
      _showResults = true;
    });
  }

  void _clearAllFields() {
    _basicController.clear();
    _emailController.clear();
    _passwordController.clear();
    _numberController.clear();

    setState(() {
      _showResults = false;
      _results = 'Los valores ingresados aparecerán aquí...';
    });
  }
}
