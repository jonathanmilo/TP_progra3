#!/bin/bash

# Script de Prueba Automática - Optimización de Publicidad
# Ejecuta este script para crear datos de prueba automáticamente

BASE_URL="http://localhost:8080"

echo "🚀 Iniciando creación de datos de prueba..."
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}📝 Paso 1: Creando Usuarios...${NC}"

# Usuario 1
echo "Creando Usuario: Juan Pérez (60s)"
curl -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "intereses": ["tecnología", "deportes"],
    "tiempoMaximoExposicion": 60
  }' -s | jq '.'

# Usuario 2
echo ""
echo "Creando Usuario: María García (120s)"
curl -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María García",
    "email": "maria@example.com",
    "intereses": ["moda", "viajes"],
    "tiempoMaximoExposicion": 120
  }' -s | jq '.'

# Usuario 3
echo ""
echo "Creando Usuario: Pedro López (30s)"
curl -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Pedro López",
    "email": "pedro@example.com",
    "intereses": ["comida", "música"],
    "tiempoMaximoExposicion": 30
  }' -s | jq '.'

echo ""
echo -e "${GREEN}✅ Usuarios creados${NC}"
echo ""

echo -e "${BLUE}📢 Paso 2: Creando Publicaciones (Anuncios)...${NC}"

# Anuncio A
echo "Creando Anuncio A: Oferta Tecnología"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "¡Gran oferta en smartphones! 30% de descuento",
    "idCreador": "empresa123",
    "costo": 100,
    "duracion": 15,
    "alcancePotencial": 500
  }' -s | jq '.'

# Anuncio B
echo ""
echo "Creando Anuncio B: Promoción Viajes"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "Viajes todo incluido a Cancún - Reserva ya",
    "idCreador": "empresa123",
    "costo": 200,
    "duracion": 30,
    "alcancePotencial": 1000
  }' -s | jq '.'

# Anuncio C
echo ""
echo "Creando Anuncio C: Descuento Ropa"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "Nueva colección primavera - 40% OFF",
    "idCreador": "empresa123",
    "costo": 150,
    "duracion": 25,
    "alcancePotencial": 700
  }' -s | jq '.'

# Anuncio D
echo ""
echo "Creando Anuncio D: Restaurante"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "Menú del día en Restaurante XYZ - $15",
    "idCreador": "empresa123",
    "costo": 50,
    "duracion": 10,
    "alcancePotencial": 300
  }' -s | jq '.'

# Anuncio E
echo ""
echo "Creando Anuncio E: Concierto"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "Concierto de rock en vivo - Boletos limitados",
    "idCreador": "empresa123",
    "costo": 250,
    "duracion": 40,
    "alcancePotencial": 1500
  }' -s | jq '.'

# Anuncio F
echo ""
echo "Creando Anuncio F: App Nueva"
curl -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "contenido": "Descarga nuestra nueva app y gana $10",
    "idCreador": "empresa123",
    "costo": 80,
    "duracion": 20,
    "alcancePotencial": 800
  }' -s | jq '.'

echo ""
echo -e "${GREEN}✅ Publicaciones creadas${NC}"
echo ""

echo -e "${BLUE}🎯 Paso 3: Ejecutando Optimización...${NC}"
echo ""

echo "Probando con presupuesto: \$500"
curl -X GET "${BASE_URL}/publicaciones/optimizar-publicidad?presupuestoEmpresa=500" \
  -H "Accept: application/json" -s | jq '.'

echo ""
echo -e "${GREEN}✅ Datos de prueba creados y optimización ejecutada${NC}"
echo ""
echo "🌐 Ahora puedes ver los resultados en:"
echo "   - Usuarios: ${BASE_URL}/view/usuarios"
echo "   - Publicaciones: ${BASE_URL}/view/publicaciones"
echo "   - Optimización: ${BASE_URL}/view/optimizar-publicidad"
echo ""

