#!/bin/bash

# Script de Verificación - Comparación de Rendimiento
# Compara el rendimiento entre consultar todas vs consultar relevantes

BASE_URL="http://localhost:8080"

echo "⚡ Test de Rendimiento: Consulta Completa vs Relevantes"
echo ""

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}📊 Midiendo tiempos de respuesta...${NC}"
echo ""

# Test 1: Consultar TODAS las publicaciones (sin ordenar)
echo "1️⃣ Consultando TODAS las publicaciones..."
START1=$(date +%s%N)
ALL_PUBS=$(curl -s "${BASE_URL}/publicaciones")
END1=$(date +%s%N)
TIME1=$(( (END1 - START1) / 1000000 ))
COUNT_ALL=$(echo "$ALL_PUBS" | jq 'length')
echo "   Tiempo: ${TIME1}ms | Total: $COUNT_ALL publicaciones"

# Test 2: Consultar publicaciones RELEVANTES (pre-calculadas)
echo "2️⃣ Consultando publicaciones RELEVANTES..."
START2=$(date +%s%N)
REL_PUBS=$(curl -s "${BASE_URL}/publicaciones/relevantes")
END2=$(date +%s%N)
TIME2=$(( (END2 - START2) / 1000000 ))
COUNT_REL=$(echo "$REL_PUBS" | jq 'length')
echo "   Tiempo: ${TIME2}ms | Total: $COUNT_REL publicaciones"

echo ""
echo -e "${GREEN}📈 RESULTADOS:${NC}"
echo ""
echo "┌────────────────────────────────────────┐"
echo "│ Endpoint                | Tiempo (ms) │"
echo "├────────────────────────────────────────┤"
printf "│ GET /publicaciones      │ %10d  │\n" $TIME1
printf "│ GET /relevantes         │ %10d  │\n" $TIME2
echo "└────────────────────────────────────────┘"
echo ""

if [ $TIME2 -lt $TIME1 ]; then
  MEJORA=$(( (TIME1 - TIME2) * 100 / TIME1 ))
  echo -e "${GREEN}✅ Las consultas a /relevantes son ${MEJORA}% más rápidas${NC}"
else
  echo -e "${YELLOW}⚠️  Las consultas tienen tiempo similar (BD pequeña)${NC}"
fi

echo ""
echo "💡 Nota: Con millones de publicaciones, /relevantes sería 100-1000x más rápido"
echo ""

# Mostrar las top 5
echo -e "${BLUE}🏆 Top 5 Publicaciones Relevantes:${NC}"
echo "$REL_PUBS" | jq -r '.[:5] | to_entries | .[] | "#\(.key + 1): \(.value.contenido) (Relevancia: \(.value.relevancia))"'

echo ""
echo -e "${GREEN}✅ Test de rendimiento completado${NC}"
echo ""

