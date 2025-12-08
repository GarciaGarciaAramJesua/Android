from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from datetime import datetime
import os

app = Flask(__name__)

# 1. Configuración de la Base de Datos (SQLite)
# El archivo se guardará en la carpeta del contenedor como 'site.db'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///site.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)
bcrypt = Bcrypt(app)

# 2. Modelos de Base de Datos

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False)
    is_admin = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    # Relaciones
    favorites = db.relationship('Favorite', backref='user', lazy=True, cascade='all, delete-orphan')
    search_history = db.relationship('SearchHistory', backref='user', lazy=True, cascade='all, delete-orphan')

    def __repr__(self):
        return f"User('{self.username}')"
    
    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'is_admin': self.is_admin,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

class Favorite(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    book_id = db.Column(db.String(100), nullable=False)
    title = db.Column(db.String(200), nullable=False)
    author = db.Column(db.String(200))
    cover_url = db.Column(db.String(500))
    added_at = db.Column(db.DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"Favorite('{self.title}')"
    
    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'book_id': self.book_id,
            'title': self.title,
            'author': self.author,
            'cover_url': self.cover_url,
            'added_at': self.added_at.isoformat() if self.added_at else None
        }

class SearchHistory(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    query = db.Column(db.String(200), nullable=False)
    search_type = db.Column(db.String(50))  # 'book', 'author'
    searched_at = db.Column(db.DateTime, default=datetime.utcnow)

    def __repr__(self):
        return f"SearchHistory('{self.query}')"
    
    def to_dict(self):
        return {
            'id': self.id,
            'user_id': self.user_id,
            'query': self.query,
            'search_type': self.search_type,
            'searched_at': self.searched_at.isoformat() if self.searched_at else None
        }

# 3. Rutas

@app.route('/')
def hello():
    return jsonify({"message": "API Funcionando"})

# Endpoint de REGISTRO
@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    # Verificar si el usuario ya existe
    if User.query.filter_by(username=username).first():
        return jsonify({"message": "El usuario ya existe"}), 400

    # Encriptar contraseña
    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
    
    # Crear y guardar nuevo usuario
    new_user = User(username=username, password=hashed_password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "Usuario creado exitosamente"}), 201

# Endpoint de LOGIN
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    user = User.query.filter_by(username=username).first()

    # Verificamos si el usuario existe y si la contraseña coincide con el hash
    if user and bcrypt.check_password_hash(user.password, password):
        return jsonify({
            "status": "success",
            "message": "Login exitoso",
            "user_id": user.id,
            "username": user.username,
            "is_admin": user.is_admin
        }), 200
    else:
        return jsonify({"status": "error", "message": "Credenciales inválidas"}), 401

# Endpoint para agregar a favoritos
@app.route('/favorites', methods=['POST'])
def add_favorite():
    data = request.get_json()
    user_id = data.get('user_id')
    book_id = data.get('book_id')
    title = data.get('title')
    author = data.get('author')
    cover_url = data.get('cover_url')

    # Verificar si ya existe en favoritos
    existing = Favorite.query.filter_by(user_id=user_id, book_id=book_id).first()
    if existing:
        return jsonify({"message": "Ya está en favoritos"}), 400

    favorite = Favorite(
        user_id=user_id,
        book_id=book_id,
        title=title,
        author=author,
        cover_url=cover_url
    )
    db.session.add(favorite)
    db.session.commit()

    return jsonify({"message": "Agregado a favoritos", "favorite": favorite.to_dict()}), 201

# Endpoint para obtener favoritos de un usuario
@app.route('/favorites/<int:user_id>', methods=['GET'])
def get_favorites(user_id):
    favorites = Favorite.query.filter_by(user_id=user_id).order_by(Favorite.added_at.desc()).all()
    return jsonify([fav.to_dict() for fav in favorites]), 200

# Endpoint para eliminar de favoritos
@app.route('/favorites/<int:favorite_id>', methods=['DELETE'])
def delete_favorite(favorite_id):
    favorite = Favorite.query.get(favorite_id)
    if not favorite:
        return jsonify({"message": "Favorito no encontrado"}), 404
    
    db.session.delete(favorite)
    db.session.commit()
    return jsonify({"message": "Eliminado de favoritos"}), 200

# Endpoint para agregar al historial de búsqueda
@app.route('/search-history', methods=['POST'])
def add_search_history():
    data = request.get_json()
    user_id = data.get('user_id')
    query = data.get('query')
    search_type = data.get('search_type', 'book')

    history = SearchHistory(
        user_id=user_id,
        query=query,
        search_type=search_type
    )
    db.session.add(history)
    db.session.commit()

    return jsonify({"message": "Búsqueda guardada", "history": history.to_dict()}), 201

# Endpoint para obtener historial de búsqueda de un usuario
@app.route('/search-history/<int:user_id>', methods=['GET'])
def get_search_history(user_id):
    limit = request.args.get('limit', 20, type=int)
    history = SearchHistory.query.filter_by(user_id=user_id)\
        .order_by(SearchHistory.searched_at.desc()).limit(limit).all()
    return jsonify([h.to_dict() for h in history]), 200

# Endpoint para obtener recomendaciones basadas en favoritos y historial
@app.route('/recommendations/<int:user_id>', methods=['GET'])
def get_recommendations(user_id):
    # Obtener palabras clave de favoritos y búsquedas
    favorites = Favorite.query.filter_by(user_id=user_id).all()
    history = SearchHistory.query.filter_by(user_id=user_id)\
        .order_by(SearchHistory.searched_at.desc()).limit(10).all()
    
    # Extraer autores y términos de búsqueda
    authors = set()
    search_terms = set()
    
    for fav in favorites:
        if fav.author:
            authors.add(fav.author.lower())
    
    for h in history:
        search_terms.add(h.query.lower())
    
    recommendations = {
        'user_id': user_id,
        'recommended_authors': list(authors),
        'recent_searches': list(search_terms),
        'message': 'Usa estos términos para buscar recomendaciones en Open Library API'
    }
    
    return jsonify(recommendations), 200

# ENDPOINTS DE ADMINISTRADOR

# Endpoint para obtener todos los usuarios (solo admin)
@app.route('/admin/users', methods=['GET'])
def get_all_users():
    users = User.query.all()
    return jsonify([user.to_dict() for user in users]), 200

# Endpoint para obtener favoritos de todos los usuarios (solo admin)
@app.route('/admin/favorites', methods=['GET'])
def get_all_favorites():
    favorites = Favorite.query.join(User).all()
    result = []
    for fav in favorites:
        fav_dict = fav.to_dict()
        fav_dict['username'] = fav.user.username
        result.append(fav_dict)
    return jsonify(result), 200

# Endpoint para obtener historial de todos los usuarios (solo admin)
@app.route('/admin/search-history', methods=['GET'])
def get_all_search_history():
    history = SearchHistory.query.join(User).order_by(SearchHistory.searched_at.desc()).all()
    result = []
    for h in history:
        h_dict = h.to_dict()
        h_dict['username'] = h.user.username
        result.append(h_dict)
    return jsonify(result), 200

# Endpoint para obtener estadísticas de usuarios (solo admin)
@app.route('/admin/stats', methods=['GET'])
def get_stats():
    total_users = User.query.count()
    total_favorites = Favorite.query.count()
    total_searches = SearchHistory.query.count()
    
    stats = {
        'total_users': total_users,
        'total_favorites': total_favorites,
        'total_searches': total_searches
    }
    
    return jsonify(stats), 200

if __name__ == '__main__':
    # Esto crea las tablas automáticamente si no existen al iniciar
    with app.app_context():
        db.create_all()
        
        # Crear usuario admin por defecto si no existe
        admin = User.query.filter_by(username='admin').first()
        if not admin:
            hashed_password = bcrypt.generate_password_hash('admin123').decode('utf-8')
            admin = User(username='admin', password=hashed_password, is_admin=True)
            db.session.add(admin)
            db.session.commit()
            print("Usuario admin creado - username: admin, password: admin123")
    
    app.run(host='0.0.0.0', port=5000, debug=True)