import 'package:flutter/material.dart';

class Screen3 extends StatefulWidget {
  @override
  _Screen3State createState() => _Screen3State();
}

class _Screen3State extends State<Screen3> {
  // Variables para CheckBoxes
  bool cbEmail = false;
  bool cbPush = false;
  bool cbSms = false;

  // Variable para RadioGroup (frecuencia) - Diarias por defecto
  int rgFrequency = 3; // 1=Instantáneas, 2=Cada hora, 3=Diarias, 4=Semanales

  // Variables para Switches
  bool swNotifications = true;
  bool swVibration = false;
  bool swSound = false;

  // Variable para mostrar/ocultar resultados
  bool showResults = false;
  String resultsText = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color(0xFFFAFAFA), // Fondo gris claro como en Android
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Título principal
            Text(
              '✅ Elementos de Selección',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Color(0xFF2E7D32), // Verde como en XML
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 8),

            // Explicación
            Text(
              'Los elementos de selección permiten al usuario elegir opciones: CheckBox para múltiples selecciones, RadioButton para una sola opción de un grupo, y Switch para estados on/off simples.',
              style: TextStyle(fontSize: 14, color: Color(0xFF424242)),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 24),

            // Sección CheckBox
            Text(
              '📋 CheckBox - Selección Múltiple',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1976D2), // Azul como en XML
              ),
            ),
            SizedBox(height: 12),

            Text(
              'Selecciona tus preferencias de notificaciones:',
              style: TextStyle(fontSize: 14),
            ),
            SizedBox(height: 8),

            // CheckBoxes simples
            Padding(
              padding: EdgeInsets.only(left: 8),
              child: Column(
                children: [
                  Row(
                    children: [
                      Checkbox(
                        value: cbEmail,
                        onChanged: (bool? value) {
                          setState(() {
                            cbEmail = value ?? false;
                          });
                        },
                      ),
                      Text(
                        '📧 Notificaciones por Email',
                        style: TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      Checkbox(
                        value: cbPush,
                        onChanged: (bool? value) {
                          setState(() {
                            cbPush = value ?? false;
                          });
                        },
                      ),
                      Text(
                        '📱 Notificaciones Push',
                        style: TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      Checkbox(
                        value: cbSms,
                        onChanged: (bool? value) {
                          setState(() {
                            cbSms = value ?? false;
                          });
                        },
                      ),
                      Text(
                        '💬 Notificaciones SMS',
                        style: TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            SizedBox(height: 20),

            // Sección RadioButton
            Text(
              '📻 RadioButton - Selección Única',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1976D2), // Azul como en XML
              ),
            ),
            SizedBox(height: 12),

            Text(
              'Selecciona tu frecuencia de notificaciones preferida:',
              style: TextStyle(fontSize: 14),
            ),
            SizedBox(height: 8),

            // RadioButtons simples
            Padding(
              padding: EdgeInsets.only(left: 8),
              child: Column(
                children: [
                  Row(
                    children: [
                      Radio<int>(
                        value: 1,
                        groupValue: rgFrequency,
                        onChanged: (int? value) {
                          setState(() {
                            rgFrequency = value ?? 1;
                          });
                        },
                      ),
                      Text('⚡ Instantáneas', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                  Row(
                    children: [
                      Radio<int>(
                        value: 2,
                        groupValue: rgFrequency,
                        onChanged: (int? value) {
                          setState(() {
                            rgFrequency = value ?? 2;
                          });
                        },
                      ),
                      Text('🕐 Cada hora', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                  Row(
                    children: [
                      Radio<int>(
                        value: 3,
                        groupValue: rgFrequency,
                        onChanged: (int? value) {
                          setState(() {
                            rgFrequency = value ?? 3;
                          });
                        },
                      ),
                      Text('📅 Diarias', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                  Row(
                    children: [
                      Radio<int>(
                        value: 4,
                        groupValue: rgFrequency,
                        onChanged: (int? value) {
                          setState(() {
                            rgFrequency = value ?? 4;
                          });
                        },
                      ),
                      Text('📊 Semanales', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                ],
              ),
            ),

            // Sección Switch
            SizedBox(height: 20),
            Text(
              '🔄 Switch - Estados On/Off',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1976D2), // Azul como en XML
              ),
            ),
            SizedBox(height: 12),

            Text(
              'Activa o desactiva funciones generales:',
              style: TextStyle(fontSize: 14),
            ),
            SizedBox(height: 8),

            // Switches simples
            Padding(
              padding: EdgeInsets.only(left: 8),
              child: Column(
                children: [
                  Row(
                    children: [
                      Switch(
                        value: swNotifications,
                        onChanged: (bool value) {
                          setState(() {
                            swNotifications = value;
                          });
                        },
                      ),
                      SizedBox(width: 8),
                      Text(
                        '🔔 Notificaciones Generales',
                        style: TextStyle(fontSize: 16),
                      ),
                    ],
                  ),
                  Row(
                    children: [
                      Switch(
                        value: swVibration,
                        onChanged: (bool value) {
                          setState(() {
                            swVibration = value;
                          });
                        },
                      ),
                      SizedBox(width: 8),
                      Text('� Vibración', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                  Row(
                    children: [
                      Switch(
                        value: swSound,
                        onChanged: (bool value) {
                          setState(() {
                            swSound = value;
                          });
                        },
                      ),
                      SizedBox(width: 8),
                      Text('🔊 Sonido', style: TextStyle(fontSize: 16)),
                    ],
                  ),
                ],
              ),
            ),
            SizedBox(height: 20),

            // Botones de acción
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: _showPreferences,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFF4CAF50), // Verde como en XML
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '📋 Ver Preferencias',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
                SizedBox(width: 16),
                ElevatedButton(
                  onPressed: _resetAll,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFFFF5722), // Rojo como en XML
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  child: Text(
                    '🔄 Resetear Todo',
                    style: TextStyle(color: Colors.white),
                  ),
                ),
              ],
            ),
            SizedBox(height: 16),

            // Área de resultados
            if (showResults)
              Container(
                width: double.infinity,
                padding: EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Color(0xFFE8F5E8), // Fondo verde como en XML
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  resultsText,
                  style: TextStyle(
                    fontSize: 14,
                    color: Color(0xFF2E7D32), // Texto verde oscuro
                    height: 1.3, // lineSpacingExtra equivalente
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  void _showPreferences() {
    final results = StringBuffer();
    results.write('🔧 Configuración Actual:\n\n');

    // CheckBoxes
    results.write('� Notificaciones Seleccionadas:\n');
    if (cbEmail) results.write('• 📧 Email\n');
    if (cbPush) results.write('• 📱 Push\n');
    if (cbSms) results.write('• � SMS\n');
    if (!cbEmail && !cbPush && !cbSms) {
      results.write('• Ninguna seleccionada\n');
    }
    results.write('\n');

    // RadioButtons
    results.write('📻 Frecuencia:\n');
    switch (rgFrequency) {
      case 1:
        results.write('• ⚡ Instantáneas\n');
        break;
      case 2:
        results.write('• 🕐 Cada hora\n');
        break;
      case 3:
        results.write('• 📅 Diarias\n');
        break;
      case 4:
        results.write('• 📊 Semanales\n');
        break;
    }
    results.write('\n');

    // Switches
    results.write('🔄 Estados:\n');
    results.write(
      '• 🔔 Notificaciones: ${swNotifications ? "Activadas" : "Desactivadas"}\n',
    );
    results.write(
      '• 📳 Vibración: ${swVibration ? "Activada" : "Desactivada"}\n',
    );
    results.write('• � Sonido: ${swSound ? "Activado" : "Desactivado"}');

    setState(() {
      resultsText = results.toString();
      showResults = true;
    });
  }

  void _resetAll() {
    setState(() {
      // Resetear CheckBoxes
      cbEmail = false;
      cbPush = false;
      cbSms = false;

      // Resetear RadioGroup a opción por defecto (Diarias)
      rgFrequency = 3;

      // Resetear Switches
      swNotifications = true;
      swVibration = false;
      swSound = false;

      // Ocultar resultados
      showResults = false;
      resultsText = '';
    });
  }
}
