# Proximity YouTube (Android / Kotlin)

App mínima que abre YouTube cuando detecta que el sensor de proximidad pasa de "lejos" a "cerca".

## Abrir el proyecto
1. Descarga y descomprime `ProximityYouTube.zip`.
2. Abre Android Studio y elige **Open**; selecciona la carpeta `ProximityYouTube`.
3. Espera a que Gradle sincronice. Puede que Android Studio te pida instalar Android SDK 35.
4. Conecta tu teléfono Android con depuración USB activada, o usa un emulador con sensor de proximidad (no todos lo tienen).
5. Pulsa **Run** para instalar y ejecutar.

## Uso
1. Abre Proximity YouTube.
2. Pulsa **Iniciar detección**.
3. Acerca la mano al sensor de proximidad, normalmente cerca del auricular/cámara frontal.
4. Se abrirá YouTube o, si no está instalada, se abrirá youtube.com en el navegador.

## Limitaciones
- Esta versión detecta proximidad mientras la actividad está visible. Android suele suspender la lectura cuando la app pierde el primer plano.
- La app no reproduce automáticamente un video; solamente abre YouTube.
- El nombre del paquete de YouTube puede variar en ROMs modificadas.
- No se ha compilado ni probado en un dispositivo físico dentro de este entorno.

## Alternativa: compilar gratis en GitHub Actions
El proyecto incluye `.github/workflows/build-apk.yml` para generar el APK en la nube.
1. Crea un repositorio en GitHub y sube todos los archivos de esta carpeta, incluido `.github`.
2. Abre la pestaña **Actions** del repositorio y ejecuta **Build Android APK** con **Run workflow** (o sube el proyecto a la rama `main`).
3. Cuando el trabajo termine correctamente, abre la ejecución y descarga el artefacto **ProximityYouTube-debug-apk**.
4. Descomprime el artefacto; dentro estará `app-debug.apk`. Envíalo al teléfono e instálalo. Android puede pedirte autorizar la instalación desde esa fuente.

El APK de depuración sirve para instalación personal; no es una versión firmada para publicar en Google Play.
