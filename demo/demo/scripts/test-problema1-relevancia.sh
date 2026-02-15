#!/bin/bash

# Script de Prueba - Problema 1: Gestión de Publicaciones con Vista por Relevancia
# Este script crea publicaciones con diferentes niveles de engagement y verifica
# que el sistema mantenga correctamente las K más relevantes

BASE_URL="http://localhost:8080"

echo "🚀 Test: Problema 1 - Gestión de Publicaciones por Relevancia"
echo ""

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}📝 Paso 1: Creando usuarios de prueba...${NC}"

# Usuario 1
USER1=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana López",
    "email": "ana@example.com",
    "intereses": ["tecnología"],
    "tiempoMaximoExposicion": 60
  }' | jq -r '.id')

echo "✅ Usuario creado: Ana López (ID: $USER1)"

# Usuario 2
USER2=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos Ruiz",
    "email": "carlos@example.com",
    "intereses": ["deportes"],
    "tiempoMaximoExposicion": 90
  }' | jq -r '.id')

echo "✅ Usuario creado: Carlos Ruiz (ID: $USER2)"

echo ""
echo -e "${BLUE}📢 Paso 2: Creando publicaciones con diferentes niveles de engagement...${NC}"

# Publicación con BAJO engagement
PUB1=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Publicación con bajo engagement\",
    \"idCreador\": \"$USER1\",
    \"costo\": 50,
    \"duracion\": 10,
    \"alcancePotencial\": 200
  }" | jq -r '.id')
echo "📝 Publicación 1 creada (ID: $PUB1)"

sleep 1

# Publicación con MEDIO engagement
PUB2=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Publicación con medio engagement\",
    \"idCreador\": \"$USER1\",
    \"costo\": 100,
    \"duracion\": 15,
    \"alcancePotencial\": 500
  }" | jq -r '.id')
echo "📝 Publicación 2 creada (ID: $PUB2)"

sleep 1

# Publicación con ALTO engagement
PUB3=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Publicación con alto engagement\",
    \"idCreador\": \"$USER2\",
    \"costo\": 150,
    \"duracion\": 20,
    \"alcancePotencial\": 1000
  }" | jq -r '.id')
echo "📝 Publicación 3 creada (ID: $PUB3)"

sleep 1

# Publicación con MUY ALTO engagement
PUB4=$(curl -s -X POST "${BASE_URL}/publicaciones/crear" \
  -H "Content-Type: application/json" \
  -d "{
    \"contenido\": \"Publicación VIRAL\",
    \"idCreador\": \"$USER2\",
    \"costo\": 200,
    \"duracion\": 25,
    \"alcancePotencial\": 2000
  }" | jq -r '.id')
echo "📝 Publicación 4 creada (ID: $PUB4)"

echo ""
echo -e "${BLUE}👍 Paso 3: Agregando reacciones para generar relevancia...${NC}"

# Agregar muchas reacciones a PUB4 (VIRAL)
for i in {1..5}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB4}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${PUB4}/comentario?idUsuario=user1&cantidad=3&textoComentario=Increíble!" > /dev/null

echo "✅ Publicación 4: 5 likes + 3 comentarios = 8 reacciones"

# Agregar reacciones moderadas a PUB3
for i in {1..3}; do
  curl -s -X POST "${BASE_URL}/publicaciones/${PUB3}/like?idUsuario=user${i}" > /dev/null
done
curl -s -X POST "${BASE_URL}/publicaciones/${PUB3}/comentario?idUsuario=user1&cantidad=1&textoComentario=Me gusta" > /dev/null

echo "✅ Publicación 3: 3 likes + 1 comentario = 4 reacciones"

# Agregar pocas reacciones a PUB2
curl -s -X POST "${BASE_URL}/publicaciones/${PUB2}/like?idUsuario=user1" > /dev/null
curl -s -X POST "${BASE_URL}/publicaciones/${PUB2}/comentario?idUsuario=user2&cantidad=1" > /dev/null

echo "✅ Publicación 2: 1 like + 1 comentario = 2 reacciones"

# PUB1 sin reacciones
echo "✅ Publicación 1: 0 reacciones"

echo ""
echo -e "${BLUE}🔄 Paso 4: Recalculando top K=3 publicaciones relevantes...${NC}"

RELEVANTES=$(curl -s -X POST "${BASE_URL}/publicaciones/relevantes/actualizarK?nuevoK=3")

echo "$RELEVANTES" | jq -r '.[] | "📊 \(.contenido) - Relevancia: \(.relevancia) - Reacciones: \(.reaccionesTotales)"'

echo ""
echo -e "${GREEN}✅ Paso 5: Verificando resultados...${NC}"

# Contar cuántas publicaciones relevantes hay
COUNT=$(echo "$RELEVANTES" | jq 'length')
echo "Total de publicaciones relevantes: $COUNT (esperado: 3)"

# Verificar que PUB4 es la más relevante
TOP1=$(echo "$RELEVANTES" | jq -r '.[0].id')
if [ "$TOP1" == "$PUB4" ]; then
  echo -e "${GREEN}✅ La publicación VIRAL es la #1 más relevante${NC}"
else
  echo -e "${YELLOW}⚠️  La publicación VIRAL NO es la #1${NC}"
fi

echo ""
echo -e "${BLUE}🎯 Paso 6: Consultando publicaciones relevantes (O(1) lookup)...${NC}"

RELEVANTES_QUERY=$(curl -s "${BASE_URL}/publicaciones/relevantes")
echo "$RELEVANTES_QUERY" | jq -r '.[] | "  \(.contenido) - \(.reaccionesTotales) reacciones"'

echo ""
echo -e "${GREEN}✅ Test completado${NC}"
echo ""
echo "📊 Puedes ver los resultados en:"
echo "   - Relevantes: ${BASE_URL}/view/relevantes"
echo "   - Swagger: ${BASE_URL}/swagger-ui/index.html"
echo ""

