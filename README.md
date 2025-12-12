# MusicMUM

> **Proyecto Integrador - Desarrollo de Aplicaciones Móviles**
>
> **Semestre:** 4°E
> **Fecha de entrega:** 11 de Diciembre

---

## Equipo de Desarrollo

| Nombre Completo | Rol / Tareas Principales | Usuario GitHub |
| :--- | :--- | :--- |
| Mireya Betzaida Silvano Martínez | Design, Repositorio | @MireyaSilvano21 |
| Usiel Shamed Servín Hernández | Backend, Retrofit | @Usiel-Curt |
| Julia Mariana Villafaña Salazar | Repositorio, Lógica | @marianaVsa |

---

## Descripción del Proyecto

**¿Qué hace la aplicación?**
[MusicMUM es una aplicación móvil que permite a los usuarios reproducir la música almacenada en su dispositivo, organizarla dentro de playlists personalizadas, y controlar la reproducción utilizando el sensor de proximidad del teléfono.

La app está dirigida a usuarios que desean una forma rápida y accesible de organizar su música sin depender de servicios de streaming, con una interfaz intuitiva desarrollada en Jetpack Compose.]

**Objetivo:**
Implementar una arquitectura moderna en Android (MVVM), consumo de API REST mediante Retrofit, persistencia local mediante Room/SQLite y el uso de hardware del dispositivo para crear una aplicación funcional y completa.

---

## Stack Tecnológico y Características

Este proyecto ha sido desarrollado siguiendo estrictamente los lineamientos de la materia:

* **Lenguaje:** Kotlin 100%.
* **Interfaz de Usuario:** Jetpack Compose.
* **Arquitectura:** MVVM (Model-View-ViewModel).
* **Conectividad (API REST):** Retrofit.
    * **GET:** [Se obtienen listas de elementos remotos.]
    * **POST:** [Se envían datos creados por el usuario hacia la API]
    * **UPDATE:** [Se permite actualizar un dato existente en el servidor]
    * **DELETE:** [Se elimina un elemento almacenado en la API.]
* **Sensor Integrado:** [Sensor de proximidad]
    * *Uso:* [El sensor permite controlar la reproducción de música sin necesidad de tocar la pantalla:

-Sensor cubierto → Pausar la música

-Sensor descubierto → Reanudar la reproducción

Esto crea una experiencia fluida y útil especialmente para actividades donde no se puede interactuar directamente con el dispositivo.
]

---

## Capturas de Pantalla

[Coloca al menos 3 (investiga como agregarlas y se vean en GitHub)]

| Pantalla de Inicio | Operación CRUD | Uso del Sensor |
| :---: | :---: | :---: |
| ![Inicio](url_imagen) | ![CRUD](url_imagen) | ![Sensor](url_imagen) |

---

## Instalación y Releases

El ejecutable firmado (.apk) se encuentra disponible en la sección de **Releases** de este repositorio.

[Liga correctamente tu link de releases en la siguiente sección]

1.  Ve a la sección "Releases" (o haz clic [aquí](link_a_tus_releases)).
2.  Descarga el archivo `.apk` de la última versión.
3.  Instálalo en tu dispositivo Android (asegúrate de permitir la instalación de orígenes desconocidos).
