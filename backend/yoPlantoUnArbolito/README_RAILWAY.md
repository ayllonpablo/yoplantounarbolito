# Despliegue en Railway.app

## Pasos para desplegar

### 1. Subir código a GitHub
El código ya debe estar en: https://github.com/ayllonpablo/yoplantounarbolito

### 2. Crear cuenta en Railway
- Ve a https://railway.app
- Regístrate con tu cuenta de GitHub

### 3. Crear nuevo proyecto
1. Click en "New Project"
2. Selecciona "Deploy from GitHub repo"
3. Autoriza Railway para acceder a tus repositorios
4. Selecciona `ayllonpablo/yoplantounarbolito`
5. Railway detectará automáticamente Laravel

### 4. Agregar MySQL Database
1. En tu proyecto Railway, click en "+ New"
2. Selecciona "Database" → "Add MySQL"
3. Railway creará una base de datos MySQL automáticamente

### 5. Configurar Variables de Entorno
En la pestaña "Variables" de tu servicio Laravel, agrega:

```
APP_NAME=Sembrando Vidas
APP_ENV=production
APP_KEY=base64:GENERA_NUEVA_KEY
APP_DEBUG=false
APP_URL=${{RAILWAY_PUBLIC_DOMAIN}}

DB_CONNECTION=mysql
DB_HOST=${{MySQL.MYSQLHOST}}
DB_PORT=${{MySQL.MYSQLPORT}}
DB_DATABASE=${{MySQL.MYSQLDATABASE}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}

LOG_LEVEL=error
SESSION_DRIVER=file
CACHE_DRIVER=file
```

**Nota:** Railway reemplazará automáticamente las variables `${{MySQL.*}}` con los valores de tu base de datos.

### 6. Generar APP_KEY
Ejecuta en tu terminal local:
```bash
php artisan key:generate --show
```
Copia el resultado y úsalo como `APP_KEY` en Railway.

### 7. Deploy
Railway iniciará el despliegue automáticamente. Puedes ver los logs en tiempo real.

### 8. Ejecutar migraciones (si no se ejecutan automáticamente)
En Railway, ve a tu servicio y ejecuta:
```bash
php artisan migrate --force
```

### 9. Obtener URL pública
Railway generará una URL como: `https://yoplantounarbolito-production.up.railway.app`

### 10. Actualizar app Android
Actualiza la URL en el archivo Android:
`front_end/Sembrando Vidas/app/src/main/java/app/sembrando/vidas/java_class/Variables.java`

```java
private String url = "https://tu-app.railway.app/api";
```

## Costos estimados
- Railway ofrece $5 de crédito gratis mensual
- Plan Hobby: ~$5-10/mes
- Plan Team: ~$20/mes

## Troubleshooting

### Error: "Class not found"
Ejecuta en Railway:
```bash
composer dump-autoload
php artisan config:clear
php artisan cache:clear
```

### Error de migraciones
```bash
php artisan migrate:fresh --force
```

### Ver logs
En Railway, click en "View Logs" para ver errores en tiempo real.
