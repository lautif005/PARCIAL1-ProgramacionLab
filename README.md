# Tienda de Películas – Consola Java + JDBC + H2

Esta aplicación de consola permite gestionar **Películas** y **Ventas** usando JDBC, H2 (archivo local), patrón DAO, transacciones y logging con Log4j 2.

## Requisitos

- Java 17 o superior
- Gradle instalado 
- H2 Console

## Ejecutar la aplicación

1. **Compilar**:
   `./gradlew clean build`

2. **Ejecutar Menú de Películas**:
   `./gradlew run --args="org.example.main.MenuPeliculas"`

3. **Crear una película (opción 3) y Seguir con el Flujo de la Ejecución**:

4. **Ejecutar Menú de Ventas**:

   `./gradlew run --args="org.example.main.MenuVentas"`

5. **Crear una venta (opción 3) y Seguir con el Flujo de la Ejecución**:

## Aclaración

La base de datos se crea automáticamente dentro de la carpeta "data".