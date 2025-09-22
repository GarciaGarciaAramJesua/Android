import 'package:flutter/material.dart';

// Modelo de datos para ProfileItem (equivalente al Android)
class ProfileItem {
  final String title;
  final String description;
  final String timestamp;

  ProfileItem({
    required this.title,
    required this.description,
    required this.timestamp,
  });
}

class Screen4 extends StatefulWidget {
  @override
  _Screen4State createState() => _Screen4State();
}

class _Screen4State extends State<Screen4> {
  // Controlador para el campo de texto
  final TextEditingController _etNewItemController = TextEditingController();

  // Listas de datos
  final List<ProfileItem> _recyclerItems = [];
  final List<String> _listViewItems = [];

  @override
  void dispose() {
    _etNewItemController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          '📋 Perfil',
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
                  '📋 Listas Interactivas',
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
                  'Las listas permiten mostrar conjuntos de datos de forma organizada y scrolleable. ListView es perfecto para datos complejos, mientras que las listas simples son ideales para datos básicos.',
                  style: TextStyle(fontSize: 14, color: Color(0xFF424242)),
                  textAlign: TextAlign.center,
                ),
                SizedBox(height: 24),

                // Sección: Controles
                _buildSectionTitle('➕ Controles de Lista'),
                SizedBox(height: 12),

                // Campo de texto y botón agregar
                Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _etNewItemController,
                        decoration: InputDecoration(
                          hintText: 'Agregar nuevo elemento...',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                          ),
                          contentPadding: EdgeInsets.all(12),
                        ),
                        onSubmitted: (value) => _addItem(),
                      ),
                    ),
                    SizedBox(width: 8),
                    ElevatedButton(
                      onPressed: _addItem,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Color(0xFF4CAF50),
                        padding: EdgeInsets.all(16),
                      ),
                      child: Text(
                        '➕',
                        style: TextStyle(color: Colors.white, fontSize: 16),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 16),

                // Botones de acciones
                Row(
                  children: [
                    Expanded(
                      child: ElevatedButton(
                        onPressed: _addSampleData,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Color(0xFF2196F3),
                          padding: EdgeInsets.symmetric(vertical: 12),
                        ),
                        child: Text(
                          '📝 Datos de Ejemplo',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                    SizedBox(width: 16),
                    Expanded(
                      child: ElevatedButton(
                        onPressed: _clearAllLists,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Color(0xFFFF5722),
                          padding: EdgeInsets.symmetric(vertical: 12),
                        ),
                        child: Text(
                          '🗑️ Limpiar Todo',
                          style: TextStyle(color: Colors.white),
                        ),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 20),

                // Sección: RecyclerView (Lista Avanzada)
                _buildSectionTitle('🔄 ListView - Lista Avanzada'),
                SizedBox(height: 8),
                Text(
                  'ListView con elementos personalizados y deslizar para eliminar:',
                  style: TextStyle(fontSize: 14),
                ),
                SizedBox(height: 8),

                Container(
                  height: 200,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(8),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.2),
                        spreadRadius: 1,
                        blurRadius: 3,
                        offset: Offset(0, 2),
                      ),
                    ],
                  ),
                  child: _recyclerItems.isEmpty
                      ? Center(
                          child: Text(
                            'No hay elementos en la lista avanzada\n➕ Agrega algunos elementos',
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              color: Colors.grey[600],
                              fontSize: 14,
                            ),
                          ),
                        )
                      : ListView.builder(
                          padding: EdgeInsets.all(8),
                          itemCount: _recyclerItems.length,
                          itemBuilder: (context, index) {
                            return Dismissible(
                              key: Key(
                                _recyclerItems[index].title + index.toString(),
                              ),
                              direction: DismissDirection.endToStart,
                              background: Container(
                                alignment: Alignment.centerRight,
                                padding: EdgeInsets.only(right: 20),
                                color: Colors.red,
                                child: Icon(Icons.delete, color: Colors.white),
                              ),
                              onDismissed: (direction) {
                                _removeFromRecyclerView(index);
                              },
                              child: _buildProfileItemCard(
                                _recyclerItems[index],
                              ),
                            );
                          },
                        ),
                ),
                SizedBox(height: 20),

                // Sección: ListView Simple
                _buildSectionTitle('📝 Lista Simple'),
                SizedBox(height: 8),
                Text(
                  'Lista básica con elementos de texto simple:',
                  style: TextStyle(fontSize: 14),
                ),
                SizedBox(height: 8),

                Container(
                  height: 200,
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(8),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.2),
                        spreadRadius: 1,
                        blurRadius: 3,
                        offset: Offset(0, 2),
                      ),
                    ],
                  ),
                  child: _listViewItems.isEmpty
                      ? Center(
                          child: Text(
                            'No hay elementos en la lista simple\n➕ Agrega algunos elementos',
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              color: Colors.grey[600],
                              fontSize: 14,
                            ),
                          ),
                        )
                      : ListView.separated(
                          padding: EdgeInsets.all(8),
                          itemCount: _listViewItems.length,
                          separatorBuilder: (context, index) =>
                              Divider(color: Color(0xFFE0E0E0), height: 1),
                          itemBuilder: (context, index) {
                            return ListTile(
                              title: Text(_listViewItems[index]),
                              onTap: () => _removeFromListView(index),
                              trailing: Icon(
                                Icons.touch_app,
                                color: Colors.grey[400],
                                size: 16,
                              ),
                            );
                          },
                        ),
                ),
                SizedBox(height: 20),

                // Estadísticas
                Container(
                  padding: EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Color(0xFFE8F5E8),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _getStatsText(),
                    style: TextStyle(fontSize: 14, color: Color(0xFF2E7D32)),
                    textAlign: TextAlign.center,
                  ),
                ),
                SizedBox(height: 16),

                // Instrucciones
                Container(
                  padding: EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Color(0xFFF5F5F5),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    '💡 Instrucciones:\n'
                    '• Agrega elementos escribiendo en el campo de texto\n'
                    '• Usa "Datos de Ejemplo" para llenar las listas\n'
                    '• En Lista Avanzada: desliza elementos hacia la izquierda para eliminar\n'
                    '• En Lista Simple: toca un elemento para eliminarlo',
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

  Widget _buildProfileItemCard(ProfileItem item) {
    return Card(
      margin: EdgeInsets.symmetric(vertical: 4),
      child: Padding(
        padding: EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              item.title,
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
                color: Color(0xFF1976D2),
              ),
            ),
            SizedBox(height: 4),
            Text(
              item.description,
              style: TextStyle(fontSize: 14, color: Color(0xFF424242)),
            ),
            SizedBox(height: 4),
            Text(
              item.timestamp,
              style: TextStyle(
                fontSize: 12,
                color: Color(0xFF757575),
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getStatsText() {
    return '📊 Elementos en Lista Avanzada: ${_recyclerItems.length} | Lista Simple: ${_listViewItems.length}';
  }

  void _addItem() {
    final text = _etNewItemController.text.trim();
    if (text.isNotEmpty) {
      _addItemToBothLists(text);
      _etNewItemController.clear();
    } else {
      _showSnackBar('Por favor ingresa un texto');
    }
  }

  void _addItemToBothLists(String text) {
    final now = DateTime.now();
    final timestamp =
        '${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}:${now.second.toString().padLeft(2, '0')}';

    setState(() {
      // Agregar a Lista Avanzada (RecyclerView)
      final profileItem = ProfileItem(
        title: '📌 $text',
        description: 'Elemento agregado por el usuario',
        timestamp: 'Agregado a las $timestamp',
      );
      _recyclerItems.add(profileItem);

      // Agregar a Lista Simple (ListView)
      _listViewItems.add('🔹 $text ($timestamp)');
    });

    _showSnackBar('Elemento agregado a ambas listas');
  }

  void _addSampleData() {
    final sampleItems = [
      'Tarea importante',
      'Recordatorio de reunión',
      'Nota personal',
      'Lista de compras',
      'Objetivo del día',
    ];

    for (String item in sampleItems) {
      _addItemToBothLists(item);
    }

    _showSnackBar('Datos de ejemplo agregados');
  }

  void _clearAllLists() {
    setState(() {
      _recyclerItems.clear();
      _listViewItems.clear();
    });
    _showSnackBar('Todas las listas han sido limpiadas');
  }

  void _removeFromRecyclerView(int index) {
    setState(() {
      _recyclerItems.removeAt(index);
    });
    _showSnackBar('Elemento eliminado de la Lista Avanzada');
  }

  void _removeFromListView(int index) {
    setState(() {
      _listViewItems.removeAt(index);
    });
    _showSnackBar('Elemento eliminado de la Lista Simple');
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: Duration(seconds: 2)),
    );
  }
}
