import 'package:flutter/material.dart';
import 'dart:async';

class Screen5 extends StatefulWidget {
  @override
  _Screen5State createState() => _Screen5State();
}

class _Screen5State extends State<Screen5> with TickerProviderStateMixin {
  // Variables para la funcionalidad
  int currentImageSet = 0;
  bool isProgressRunning = false;
  int dynamicTextIndex = 0;
  double progressValue = 0.3; // Progreso inicial al 30%
  String statusMessage = "📋 Estado: Listo para demostración";

  // Timers para animaciones
  Timer? dynamicTextTimer;
  Timer? progressTimer;
  AnimationController? progressAnimationController;

  // Arrays de datos para la demostración
  final List<List<IconData>> imageIcons = [
    [Icons.info, Icons.warning, Icons.camera_alt],
    [Icons.photo_library, Icons.settings, Icons.save],
    [Icons.search, Icons.share, Icons.delete],
  ];

  final List<Color> imageColors = [
    Color(0xFFE3F2FD), // Azul claro
    Color(0xFFFFF3E0), // Naranja claro
    Color(0xFFE8F5E8), // Verde claro
  ];

  final List<String> dynamicTexts = [
    "🔄 Texto actualizado: {time}",
    "✨ Contenido dinámico en acción",
    "📱 Text puede cambiar automáticamente",
    "🎯 Perfecto para actualizaciones en tiempo real",
    "💫 Ideal para mostrar información dinámica",
  ];

  @override
  void initState() {
    super.initState();
    progressAnimationController = AnimationController(
      vsync: this,
      duration: Duration(milliseconds: 50),
    );
    _startDynamicTextAnimation();
  }

  @override
  void dispose() {
    dynamicTextTimer?.cancel();
    progressTimer?.cancel();
    progressAnimationController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Configuración',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        backgroundColor: Colors.blue,
      ),
      body: Container(
        color: Color(0xFFFAFAFA),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Título principal
                Text(
                  '📊 Elementos de Información',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: Color(0xFF2E7D32),
                  ),
                  textAlign: TextAlign.center,
                ),
                SizedBox(height: 8),

                // Explicación
                Text(
                  'Los elementos de información muestran datos al usuario: Text para texto, Image para imágenes, y indicadores de progreso para mostrar el estado de procesos.',
                  style: TextStyle(fontSize: 14, color: Color(0xFF424242)),
                  textAlign: TextAlign.center,
                ),
                SizedBox(height: 24),

                // Sección: TextViews
                _buildSectionTitle('📝 Text - Mostrar Texto'),
                SizedBox(height: 12),
                Text(
                  'Ejemplos de Text con diferentes estilos y propiedades:',
                  style: TextStyle(fontSize: 14),
                ),
                SizedBox(height: 8),

                // TextView básico
                GestureDetector(
                  onTap: () => _showSnackBar('Text básico presionado'),
                  child: Container(
                    width: double.infinity,
                    padding: EdgeInsets.all(8),
                    margin: EdgeInsets.only(bottom: 4),
                    child: Text(
                      '📌 Text básico con texto normal',
                      style: TextStyle(fontSize: 16),
                    ),
                  ),
                ),

                // TextView estilizado
                GestureDetector(
                  onTap: () => _showSnackBar('Text estilizado presionado'),
                  child: Container(
                    width: double.infinity,
                    padding: EdgeInsets.all(12),
                    margin: EdgeInsets.only(bottom: 4),
                    decoration: BoxDecoration(
                      color: Color(0xFFFFE1F3),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Text(
                      '🎨 Text estilizado con colores y formato',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFFE91E63),
                      ),
                    ),
                  ),
                ),

                // TextView dinámico
                Container(
                  width: double.infinity,
                  padding: EdgeInsets.all(8),
                  margin: EdgeInsets.only(bottom: 16),
                  child: Text(
                    _getCurrentDynamicText(),
                    style: TextStyle(
                      fontSize: 16,
                      color: Color(0xFFFF5722),
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ),

                // Sección: ImageViews
                _buildSectionTitle('🖼️ Image - Mostrar Imágenes'),
                SizedBox(height: 12),
                Text(
                  'Ejemplos de Icons con diferentes tipos de contenido:',
                  style: TextStyle(fontSize: 14),
                ),
                SizedBox(height: 8),

                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: List.generate(3, (index) {
                    return GestureDetector(
                      onTap: () =>
                          _showSnackBar('Imagen ${index + 1} presionada'),
                      child: Container(
                        width: 60,
                        height: 60,
                        margin: EdgeInsets.all(4),
                        decoration: BoxDecoration(
                          color: imageColors[currentImageSet],
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Icon(
                          imageIcons[currentImageSet][index],
                          size: 30,
                          color: Colors.grey[700],
                        ),
                      ),
                    );
                  }),
                ),
                SizedBox(height: 8),

                Center(
                  child: ElevatedButton(
                    onPressed: _changeImageSet,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Color(0xFF4CAF50),
                      padding: EdgeInsets.symmetric(
                        horizontal: 20,
                        vertical: 12,
                      ),
                    ),
                    child: Text(
                      '🔄 Cambiar Imágenes',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
                SizedBox(height: 16),

                // Sección: ProgressBars
                _buildSectionTitle('⏳ Indicadores de Progreso'),
                SizedBox(height: 12),
                Text(
                  'Ejemplos de indicadores de progreso con diferentes configuraciones:',
                  style: TextStyle(fontSize: 14),
                ),
                SizedBox(height: 8),

                // ProgressBar horizontal
                Text(
                  '📊 Progreso Horizontal:',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 4),

                LinearProgressIndicator(
                  value: progressValue,
                  backgroundColor: Colors.grey[300],
                  valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
                ),
                SizedBox(height: 4),

                Text(
                  '${(progressValue * 100).round()}%',
                  style: TextStyle(fontSize: 14),
                  textAlign: TextAlign.center,
                ),
                SizedBox(height: 12),

                // ProgressBar circular
                Text(
                  '🔄 Progreso Circular (Carga):',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: 8),

                if (isProgressRunning)
                  Center(child: CircularProgressIndicator())
                else
                  SizedBox(height: 40), // Espacio cuando no está visible

                SizedBox(height: 16),

                // Controles interactivos
                _buildSectionTitle('🎮 Controles Interactivos'),
                SizedBox(height: 12),

                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: isProgressRunning
                          ? null
                          : _startProgressAnimation,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFF4CAF50),
                        padding: EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                      ),
                      child: Text(
                        '▶️ Iniciar',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                    SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: _resetProgress,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFFFF9800),
                        padding: EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                      ),
                      child: Text(
                        '🔄 Resetear',
                        style: TextStyle(color: Colors.white),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 16),

                // Estado
                Container(
                  width: double.infinity,
                  padding: EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Color(0xFFE8F5E8),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    statusMessage,
                    style: TextStyle(fontSize: 14, color: Color(0xFF2E7D32)),
                    textAlign: TextAlign.center,
                  ),
                ),
                SizedBox(height: 16),

                // Instrucciones
                Container(
                  width: double.infinity,
                  padding: EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Color(0xFFF5F5F5),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    '💡 Instrucciones:\n'
                    '• Text dinámico cambia automáticamente cada 2 segundos\n'
                    '• Presiona "Cambiar Imágenes" para rotar los iconos\n'
                    '• Usa "Iniciar" para animar el progreso hasta 100%\n'
                    '• "Resetear" vuelve el progreso a 30%',
                    style: TextStyle(
                      fontSize: 12,
                      color: Color(0xFF666666),
                      height: 1.4,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: TextStyle(
        fontSize: 18,
        fontWeight: FontWeight.bold,
        color: Color(0xFF1976D2),
      ),
    );
  }

  String _getCurrentDynamicText() {
    if (dynamicTextIndex == 0) {
      return "🔄 Texto actualizado: ${_getCurrentTime()}";
    } else {
      return dynamicTexts[dynamicTextIndex];
    }
  }

  String _getCurrentTime() {
    final now = DateTime.now();
    return '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}:${now.second.toString().padLeft(2, '0')}';
  }

  void _startDynamicTextAnimation() {
    dynamicTextTimer = Timer.periodic(Duration(seconds: 2), (timer) {
      setState(() {
        _updateDynamicText();
      });
    });
  }

  void _updateDynamicText() {
    dynamicTextIndex = (dynamicTextIndex + 1) % dynamicTexts.length;
  }

  void _changeImageSet() {
    setState(() {
      currentImageSet = (currentImageSet + 1) % imageIcons.length;
      _updateStatus(
        '🖼️ Conjunto de imágenes cambiado (Set ${currentImageSet + 1})',
      );
    });
    _showSnackBar('Imágenes cambiadas al conjunto ${currentImageSet + 1}');
  }

  void _startProgressAnimation() {
    if (isProgressRunning) {
      _showSnackBar('El progreso ya está en ejecución');
      return;
    }

    setState(() {
      isProgressRunning = true;
      _updateStatus('▶️ Iniciando animación de progreso...');
    });

    progressTimer = Timer.periodic(Duration(milliseconds: 50), (timer) {
      setState(() {
        if (progressValue < 1.0) {
          progressValue += 0.02; // Incrementar 2% cada vez
          _updateStatus(
            '⏳ Progreso en curso: ${(progressValue * 100).round()}%',
          );
        } else {
          // Progreso completado
          timer.cancel();
          isProgressRunning = false;
          _updateStatus('✅ Progreso completado al 100%');
          _showSnackBar('¡Progreso completado!');
        }
      });
    });
  }

  void _resetProgress() {
    progressTimer?.cancel();

    setState(() {
      isProgressRunning = false;
      progressValue = 0.3; // Resetear al 30%
      _updateStatus('🔄 Progreso reseteado a 30%');
    });

    _showSnackBar('Progreso reseteado');
  }

  void _updateStatus(String message) {
    setState(() {
      statusMessage = '📋 Estado: $message';
    });
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: Duration(seconds: 2)),
    );
  }
}
