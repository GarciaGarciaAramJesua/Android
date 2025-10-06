import 'package:flutter/material.dart';

class ThirdScreen extends StatefulWidget {
  final String userName;

  ThirdScreen({required this.userName});

  @override
  _ThirdScreenState createState() => _ThirdScreenState();
}

class _ThirdScreenState extends State<ThirdScreen> {
  // Variable para mostrar información de imagen seleccionada
  bool _showImageInfoArea = false;
  String _imageInfoText = '';

  // Array de recursos de imágenes con descripciones
  final List<Map<String, dynamic>> _imageResources = [
    {'icon': Icons.info, 'description': 'Información'},
    {'icon': Icons.warning, 'description': 'Alerta'},
    {'icon': Icons.camera_alt, 'description': 'Cámara'},
    {'icon': Icons.photo_library, 'description': 'Galería'},
    {'icon': Icons.settings, 'description': 'Preferencias'},
    {'icon': Icons.save, 'description': 'Guardar'},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFE8F5E8), // Fondo verde claro como en XML
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Título del Activity
            Text(
              '🖼️ Galería de Imágenes',
              style: TextStyle(
                fontSize: 28,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1B5E20), // Verde oscuro
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 12),

            // Mensaje de bienvenida personalizado
            Text(
              '🖼️ Bienvenido a la Galería, ${widget.userName.isEmpty ? "Usuario" : widget.userName}!',
              style: TextStyle(
                fontSize: 18,
                color: Color(0xFF2E7D32), // Verde medio
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 20),

            Text(
              'Toca cualquier imagen para ver más información',
              style: TextStyle(
                fontSize: 14,
                color: Color(0xFF388E3C), // Verde
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 24),

            // Título de la sección
            Text(
              '📸 Colección de Iconos',
              style: TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1B5E20), // Verde oscuro
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 16),

            // Grid de imágenes
            _buildImageGrid(),
            SizedBox(height: 20),

            // Información de imagen seleccionada
            if (_showImageInfoArea)
              Container(
                width: double.infinity,
                padding: EdgeInsets.all(16),
                margin: EdgeInsets.only(bottom: 20),
                decoration: BoxDecoration(
                  color: Color(0xFFC8E6C9), // Verde claro para info
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  _imageInfoText,
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xFF1B5E20), // Verde oscuro
                    height: 1.3, // lineSpacingExtra equivalente
                  ),
                ),
              ),

            // Botones de navegación
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _goBack,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF4CAF50), // Verde
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '⬅️ Atrás',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
                SizedBox(width: 24),
                ElevatedButton(
                  onPressed: _goToMain,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF2E7D32), // Verde oscuro
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '🏠 Inicio',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ],
            ),

            // Información del activity
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(12),
              margin: EdgeInsets.only(top: 20),
              decoration: BoxDecoration(
                color: Color(0xFFF1F8E9), // Verde muy claro
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                '💡 Este es el ThirdActivity - Galería\nUn activity independiente con su propio diseño y funcionalidad',
                style: TextStyle(
                  fontSize: 12,
                  color: Color(0xFF666666), // Gris
                  height: 1.2, // lineSpacingExtra equivalente
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageGrid() {
    return GridView.builder(
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3, // 3 columnas como en Android
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
        childAspectRatio: 1.0, // Cuadrado
      ),
      itemCount: _imageResources.length,
      itemBuilder: (context, index) {
        final imageData = _imageResources[index];
        return _buildImageItem(
          imageData['icon'] as IconData,
          imageData['description'] as String,
          index + 1,
        );
      },
    );
  }

  Widget _buildImageItem(IconData icon, String description, int position) {
    return GestureDetector(
      onTap: () => _showImageInfo(description, position),
      child: Container(
        width: 120,
        height: 120,
        padding: EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: Colors.grey[200],
          border: Border.all(color: Colors.grey[400]!),
          borderRadius: BorderRadius.circular(8),
          boxShadow: [
            BoxShadow(
              color: Colors.grey.withOpacity(0.2),
              spreadRadius: 1,
              blurRadius: 2,
              offset: Offset(0, 1),
            ),
          ],
        ),
        child: Icon(
          icon,
          size: 48,
          color: Color(0xFF2E7D32), // Verde medio
        ),
      ),
    );
  }

  void _showImageInfo(String description, int position) {
    final now = DateTime.now();
    final timeString =
        '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}:${now.second.toString().padLeft(2, '0')}';

    final info =
        '''🔍 Imagen Seleccionada:

📋 Descripción: $description
🔢 Posición: $position de ${_imageResources.length}
⏰ Hora de selección: $timeString''';

    setState(() {
      _imageInfoText = info;
      _showImageInfoArea = true;
    });

    // Mostrar SnackBar como Toast en Android
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Imagen \'$description\' seleccionada'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  void _goBack() {
    Navigator.pop(context); // Volver a la activity anterior
  }

  void _goToMain() {
    // Navegar al inicio (main) con clear stack como en Android
    Navigator.of(context).popUntil((route) => route.isFirst);
  }
}
