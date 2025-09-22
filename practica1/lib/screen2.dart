import 'package:flutter/material.dart';

class Screen2 extends StatefulWidget {
  @override
  _Screen2State createState() => _Screen2State();
}

class _Screen2State extends State<Screen2> {
  // Variables para la demostración
  int counter = 0;
  int totalClicks = 0;
  String resultMessage = '¡Toca cualquier botón para ver la interacción!';

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
                  '🔘 Demostración de Botones',
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
                  'Los botones permiten a los usuarios realizar acciones mediante toques. Button es el botón estándar con texto, mientras que ImageButton muestra solo iconos y es ideal para acciones rápidas y interfaces compactas.',
                  style: TextStyle(fontSize: 14, color: Color(0xFF333333)),
                  textAlign: TextAlign.center,
                ),
              ),
              SizedBox(height: 24),

              // Sección de Botones Estándar
              Text(
                '🔘 Botones Estándar (Button):',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 16),

              // 1. Botón Básico
              Text(
                '1. Botón Básico:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              ElevatedButton(
                onPressed: () => _handleButtonPress('Botón Básico presionado'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Presiona - Botón Básico',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              SizedBox(height: 16),

              // 2. Botón con Estilo
              Text(
                '2. Botón con Estilo:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              ElevatedButton(
                onPressed: () =>
                    _handleButtonPress('Botón Estilizado presionado'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.deepPurple, // purple_500
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  'Botón Estilizado',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              SizedBox(height: 16),

              // 3. Botón Solo Texto
              Text(
                '3. Botón Solo Texto:',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 8),
              TextButton(
                onPressed: () =>
                    _handleButtonPress('Botón de Texto presionado'),
                style: TextButton.styleFrom(
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text('Botón de Texto', style: TextStyle(fontSize: 16)),
              ),
              SizedBox(height: 24),

              // Sección de ImageButtons
              Text(
                '🎨 Botones de Imagen (ImageButton):',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 16),

              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  _buildImageButton(
                    icon: Icons.favorite,
                    color: Colors.red,
                    onPressed: () =>
                        _handleButtonPress('❤️ ImageButton Corazón presionado'),
                  ),
                  SizedBox(width: 16),
                  _buildImageButton(
                    icon: Icons.star,
                    color: Colors.amber,
                    onPressed: () =>
                        _handleButtonPress('⭐ ImageButton Estrella presionado'),
                  ),
                  SizedBox(width: 16),
                  _buildImageButton(
                    icon: Icons.share,
                    color: Colors.blue,
                    onPressed: () => _handleButtonPress(
                      '📤 ImageButton Compartir presionado',
                    ),
                  ),
                ],
              ),
              SizedBox(height: 16),

              // Demo Interactiva
              Text(
                '🚀 Demo Interactiva:',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 16),

              // Contador de clics
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: _incrementCounter,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple, // purple_500
                        padding: EdgeInsets.symmetric(vertical: 12),
                      ),
                      child: Text('+ 1', style: TextStyle(color: Colors.white)),
                    ),
                  ),
                  SizedBox(width: 8),
                  Expanded(
                    child: Container(
                      padding: EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: Color(0xFFE3F2FD), // Fondo azul claro
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        counter.toString(),
                        style: TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                        ),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ),
                  SizedBox(width: 8),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: _decrementCounter,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple[200], // purple_200
                        padding: EdgeInsets.symmetric(vertical: 12),
                      ),
                      child: Text('- 1', style: TextStyle(color: Colors.white)),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 16),

              // Botón reset
              ElevatedButton(
                onPressed: _resetCounter,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  padding: EdgeInsets.symmetric(vertical: 12),
                ),
                child: Text(
                  '🔄 Resetear Contador',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              SizedBox(height: 16),

              // Área de resultados
              Container(
                width: double.infinity,
                padding: EdgeInsets.all(16),
                margin: EdgeInsets.only(top: 16),
                decoration: BoxDecoration(
                  color: Color(0xFFE8F5E8), // Fondo verde como en Android
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  resultMessage,
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xFF2E7D32), // Color verde oscuro
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildImageButton({
    required IconData icon,
    required Color color,
    required VoidCallback onPressed,
  }) {
    return Container(
      width: 60,
      height: 60,
      margin: EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(8),
      ),
      child: IconButton(
        onPressed: onPressed,
        icon: Icon(icon, color: color, size: 30),
      ),
    );
  }

  void _handleButtonPress(String message) {
    setState(() {
      totalClicks++;
      _showResult(message);
    });
  }

  void _incrementCounter() {
    setState(() {
      counter++;
      totalClicks++;
      _showResult('Contador incrementado a $counter');
    });
  }

  void _decrementCounter() {
    setState(() {
      counter--;
      totalClicks++;
      _showResult('Contador decrementado a $counter');
    });
  }

  void _resetCounter() {
    setState(() {
      counter = 0;
      totalClicks++;
      _showResult('🔄 Contador reseteado');
    });
  }

  void _showResult(String message) {
    setState(() {
      resultMessage =
          '$message\n\n📊 Estadísticas:\n• Contador actual: $counter\n• Total de clics: $totalClicks';
    });
  }
}
