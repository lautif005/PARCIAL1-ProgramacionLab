package org.example.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.PeliculaDAO;
import org.example.dao.PeliculaDAOimpl;
import org.example.model.Pelicula;
import org.example.model.Resultado;
import org.example.util.ConexionDB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class MenuPeliculas {

    private static final Logger logger = LogManager.getLogger(MenuPeliculas.class);

    public static void main(String[] args) {
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             InputStream is = MenuPeliculas.class.getResourceAsStream("/crearBD.sql");
             Scanner sqlScanner = new Scanner(is).useDelimiter(";")) {

            while (sqlScanner.hasNext()) {
                String ddl = sqlScanner.next().trim();
                if (!ddl.isEmpty()) {
                    stmt.execute(ddl);
                    logger.debug("Ejecutado DDL: ", ddl);
                }
            }
            logger.info("Inicialización de BD Completada");
        } catch (Exception e) {
            System.err.println("No se inicializó la BD: " + e.getMessage());
            logger.error("Error al Inicializar BD: ", e);
        }
        Scanner scanner = new Scanner(System.in);
        PeliculaDAO peliculaDAO = new PeliculaDAOimpl();
        int opcion = -1;
        do {
            logger.debug("Mostrando Menú de Películas");
            System.out.println("\nGESTIÓN DE PELÍCULAS");
            System.out.println("1. Listar Todas");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Crear Nueva");
            System.out.println("4. Actualizar");
            System.out.println("5. Eliminar");
            System.out.println("6. Salir");
            System.out.print("Elegí una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                logger.info("Opción Seleccionada: {}", opcion);
            } catch (NumberFormatException e) {
                logger.warn("Opción Inválida Ingresada");
                System.out.println("Error: ingresá un número válido.");
                continue;
            }
            switch (opcion) {
                case 1 -> {
                    logger.info("Listar Todas las Películas");
                    List<Pelicula> peliculas = peliculaDAO.listarTodas();
                    if (peliculas.isEmpty()) {
                        logger.warn("No Hay Películas Registradas");
                        System.out.println("No hay películas registradas.");
                    } else {
                        peliculas.forEach(System.out::println);
                    }
                }
                case 2 -> {
                    try {
                        System.out.print("ID de la Película: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Buscar Película pòr ID: {}", id);
                        Resultado<Pelicula> res = peliculaDAO.buscarPorIdResultado(id);
                        System.out.println(res.getMensaje());
                        if (!res.isExitoso()) {
                            logger.warn("Venta No Encontrada con ID: {}", id);
                        } else {
                            System.out.println(res.getDato());
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("ID Inválido Ingresado para Búsqueda");
                        System.out.println("Error: ingresá un ID válido.");
                    }
                }
                case 3 -> {
                    try {
                        System.out.print("Título: ");
                        String titulo = scanner.nextLine();
                        System.out.print("Género: ");
                        String genero = scanner.nextLine();
                        System.out.print("Precio: ");
                        double precio = Double.parseDouble(scanner.nextLine());
                        System.out.print("Stock: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        logger.info("Crear Película: {} - {} - {} - {}", titulo, genero, precio, stock);
                        Resultado<Boolean> res = peliculaDAO.guardarConResultado(
                                new Pelicula(0, titulo, genero, precio, stock)
                        );
                        System.out.println(res.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("Valores Inválidos al Crear la Película");
                        System.out.println("Error: precio y stock deben ser números válidos.");
                    }
                }
                case 4 -> {
                    try {
                        System.out.print("ID de la Película a actualizar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Actualizar película ID: {}", id);
                        Pelicula existente = peliculaDAO.buscarPorId(id);
                        if (existente == null) {
                            logger.warn("Película No Encontrada para Actualizar ID: {}", id);
                            System.out.println("Película no encontrada.");
                            break;
                        }
                        System.out.print("Nuevo Título [" + existente.getTitulo() + "]: ");
                        String titulo = scanner.nextLine();
                        System.out.print("Nuevo Género [" + existente.getGenero() + "]: ");
                        String genero = scanner.nextLine();
                        System.out.print("Nuevo Precio [" + existente.getPrecio() + "]: ");
                        double precio = Double.parseDouble(scanner.nextLine());
                        System.out.print("Nuevo Stock [" + existente.getStock() + "]: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        logger.info("Datos Actualizados: {} - {} - {} - {}", titulo, genero, precio, stock);
                        existente.setTitulo(titulo);
                        existente.setGenero(genero);
                        existente.setPrecio(precio);
                        existente.setStock(stock);
                        Resultado<Boolean> res = peliculaDAO.actualizarConResultado(existente);
                        System.out.println(res.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("Valores Inválidos al Actualizar la Película");
                        System.out.println("Error: ingresá valores válidos para precio/stock.");
                    }
                }
                case 5 -> {
                    try {
                        System.out.print("ID de la Película a eliminar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Eliminar Película ID: {}", id);
                        Resultado<Boolean> resDel = peliculaDAO.eliminarConResultado(id);
                        System.out.println(resDel.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("ID Ingresado Inválido para Eliminar");
                        System.out.println("Error: ingresá un ID válido.");
                    }
                }
                case 6 -> {
                    logger.info("Saliendo del Menú de Películas");
                    System.out.println("Saliendo...");
                }
                default -> {
                    logger.warn("Opción Inválida Seleccionada: {}", opcion);
                    System.out.println("Opción inválida.");
                }
            }

        } while (opcion != 6);
        scanner.close();
    }
}
