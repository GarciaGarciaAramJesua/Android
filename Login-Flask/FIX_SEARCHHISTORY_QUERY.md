# 🔧 FIX: Error AttributeError en SearchHistory.query

## Problema Identificado

El modelo `SearchHistory` tenía una columna llamada `query` que **entraba en conflicto** con el atributo `query` de SQLAlchemy usado para hacer consultas a la base de datos.

### Errores que se presentaban:
```
AttributeError: Neither 'InstrumentedAttribute' object nor 'Comparator' object 
associated with SearchHistory.query has an attribute 'filter_by'

AttributeError: Neither 'InstrumentedAttribute' object nor 'Comparator' object 
associated with SearchHistory.query has an attribute 'count'
```

## Solución Aplicada

1. ✅ Renombré la columna `query` a `search_query` en el modelo
2. ✅ Actualicé todas las referencias en el código
3. ✅ Mantuve la compatibilidad con la API (to_dict() sigue retornando 'query')
4. ✅ Creé migración de base de datos para actualizar el esquema

## 📋 Pasos para Aplicar la Corrección

### Opción 1: Usando Docker (RECOMENDADO)

1. **Detener el contenedor actual:**
   ```powershell
   # En PowerShell (Windows)
   cd D:\Android\AndroidStudioProjects\Login-Flask
   docker-compose down
   ```

2. **Reconstruir y reiniciar el contenedor:**
   ```powershell
   docker-compose up --build -d
   ```
   
   Esto automáticamente:
   - Aplicará la migración (`flask db upgrade`)
   - Renombrará la columna en la base de datos
   - Iniciará la API actualizada

3. **Verificar que funciona:**
   ```powershell
   docker-compose logs -f flask-api
   ```
   
   Deberías ver:
   ```
   ✅ Usuario admin creado - username: admin, password: admin123
   Running on all addresses (0.0.0.0)
   Running on http://127.0.0.1:5000
   ```

### Opción 2: Actualización Manual de la Base de Datos

Si prefieres actualizar sin reconstruir el contenedor:

1. **Acceder al contenedor en ejecución:**
   ```powershell
   docker exec -it login-flask-flask-api-1 bash
   ```

2. **Ejecutar la migración:**
   ```bash
   flask db upgrade
   ```

3. **Salir del contenedor:**
   ```bash
   exit
   ```

4. **Reiniciar el contenedor:**
   ```powershell
   docker-compose restart
   ```

### Opción 3: Resetear Base de Datos (SOLO SI PIERDES DATOS NO ES PROBLEMA)

Si no te importa perder los datos de prueba:

1. **Detener contenedor:**
   ```powershell
   docker-compose down
   ```

2. **Eliminar la base de datos:**
   ```powershell
   rm D:\Android\AndroidStudioProjects\Login-Flask\instance\database.db
   ```

3. **Reiniciar contenedor:**
   ```powershell
   docker-compose up -d
   ```

## 🧪 Verificación

Después de aplicar la corrección, prueba en tu app Android:

### 1. Recomendaciones
1. Agregar al menos 2 libros a favoritos
2. Navegar a la pestaña "Recomendaciones"
3. Verificar que se muestran autores y búsquedas recientes

### 2. Estadísticas de Admin
1. Hacer login como admin (username: admin, password: admin123)
2. Ir al panel de administración
3. Navegar a la pestaña "Estadísticas"
4. Verificar que se muestran:
   - Total de usuarios
   - Total de favoritos
   - Total de búsquedas

### 3. Historial de Admin
1. En el panel de admin
2. Navegar a la pestaña "Historial"
3. Verificar que se muestra el listado numerado de búsquedas

## 📊 Cambios Técnicos Realizados

### En `app.py`:

**Modelo SearchHistory:**
```python
# ANTES
query = db.Column(db.String(200), nullable=False)

# DESPUÉS
search_query = db.Column(db.String(200), nullable=False)
```

**Acceso al campo:**
```python
# ANTES
for h in history:
    search_terms.add(h.query.lower())

# DESPUÉS
for h in history:
    search_terms.add(h.search_query.lower())
```

**to_dict() - mantiene compatibilidad:**
```python
def to_dict(self):
    return {
        'id': self.id,
        'user_id': self.user_id,
        'query': self.search_query,  # ← Sigue usando 'query' en JSON
        'search_type': self.search_type,
        'searched_at': self.searched_at.isoformat() if self.searched_at else None
    }
```

### En la Base de Datos:

Migración `2abc3def4567_rename_query_column.py`:
- Renombra la columna `query` → `search_query` en la tabla `search_history`
- Mantiene el tipo `VARCHAR(200)` y `NOT NULL`
- Incluye función `downgrade()` para revertir si es necesario

## ❓ Preguntas Frecuentes

**P: ¿Por qué no puedo usar el nombre 'query'?**  
R: `query` es un atributo especial de SQLAlchemy usado para hacer consultas (Ej: `Model.query.filter_by()`). Al definir una columna con ese nombre, se sobreescribe y causa conflictos.

**P: ¿La API cambió? ¿Debo modificar el código Android?**  
R: NO. El método `to_dict()` sigue retornando `'query'` en JSON, por lo que tu código Android no necesita cambios.

**P: ¿Qué pasa si ya tengo datos en la base de datos?**  
R: La migración automáticamente renombra la columna SIN perder datos. Todos los registros se conservan.

**P: ¿Y si hay un error al aplicar la migración?**  
R: Revisa los logs con `docker-compose logs flask-api`. Si persiste, usa la Opción 3 (resetear BD).

## 🚀 Siguiente Paso

Una vez aplicada la corrección, **rebuild tu app Android** y prueba las tres funcionalidades que estaban fallando. Todos los errores deberían estar resueltos.

Si encuentras algún problema, revisa los logs de Logcat con el filtro:
```
tag:OkHttp OR tag:Retrofit
```

¡Listo! 🎉
