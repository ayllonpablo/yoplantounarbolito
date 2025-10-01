#!/bin/bash

# Script para subir el proyecto a GitHub
# Uso: ./push_to_github.sh

echo "======================================"
echo "Subiendo proyecto a GitHub..."
echo "======================================"

cd /home/m0w/ProyectosClaude/Entregables

# Verificar estado de git
echo ""
echo "Estado del repositorio:"
git status

# Hacer push
echo ""
echo "Intentando hacer push a GitHub..."
git push -u origin main

echo ""
echo "======================================"
echo "Si el push fue exitoso, el proyecto"
echo "está ahora en:"
echo "https://github.com/ayllonpablo/yoplantounarbolito"
echo "======================================"
