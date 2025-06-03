package org.example.main;

import org.example.dao.PeliculaDAO;
import org.example.dao.PeliculaDAOimpl;
import org.example.dao.VentaDAO;
import org.example.dao.VentaDAOimpl;
import org.example.model.Resultado;
import org.example.model.Venta;
import org.example.util.ConexionDB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MenuVentas {

    private static final Logger logger = LogManager.getLogger(MenuVentas.class);

    public static void main(String[] args) {
        logger.info("Iniciando inicialización de BD para Ventas");
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             InputStream is = MenuVentas.class.getResourceAsStream("/crearBD.sql");
             Scanner sqlScanner = new Scanner(is).useDelimiter(";")) {
            while (sqlScanner.hasNext()) {
                String ddl = sqlScanner.next().trim();
                if (!ddl.isEmpty()) {
                    stmt.execute(ddl);
                    logger.debug("Ejecutado DDL: {}", ddl);
                }
            }
            logger.info("Inicialización de BD completada");
        } catch (Exception e) {
            System.err.println("No se inicializó la BD: " + e.getMessage());
            logger.error("Error al inicializar BD", e);
        }
        Scanner scanner = new Scanner(System.in);
        VentaDAO ventaDAO = new VentaDAOimpl();
        PeliculaDAO peliculaDAO = new PeliculaDAOimpl();
        int opcion = -1;
        do {
            logger.debug("Mostrando Menú de Ventas");
            System.out.println("\nGESTIÓN DE VENTAS");
            System.out.println("1. Listar Todas");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Crear Nueva");
            System.out.println("4. Actualizar");
            System.out.println("5. Eliminar");
            System.out.println("6. Salir");
            System.out.print("Elegí una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                logger.info("Opción seleccionada: {}", opcion);
            } catch (NumberFormatException e) {
                logger.warn("Opción inválida ingresada");
                System.out.println("Error: ingresá un número válido.");
                continue;
            }
            switch (opcion) {
                case 1 -> {
                    logger.info("Listar Todas las Ventas");
                    List<Venta> ventas = ventaDAO.listarTodas();
                    if (ventas.isEmpty()) {
                        logger.warn("No hay Ventas Registradas");
                        System.out.println("No hay ventas registradas.");
                    } else {
                        ventas.forEach(System.out::println);
                    }
                }
                case 2 -> {
                    try {
                        System.out.print("ID de la Venta: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Buscar Venta por ID: {}", id);
                        Resultado<Venta> res = ventaDAO.buscarPorIdResultado(id);
                        System.out.println(res.getMensaje());
                        if (!res.isExitoso()) {
                            logger.warn("Venta No Encontrada con ID: {}", id);
                        } else {
                            System.out.println(res.getDato());
                        }
                    } catch (NumberFormatException e) {
                        logger.warn("ID inválido ingresado para búsqueda");
                        System.out.println("Error: ingresá un ID válido.");
                    }
                }
                case 3 -> {
                    try {
                        System.out.print("ID de la Película Vendida: ");
                        int peliculaId = Integer.parseInt(scanner.nextLine());
                        if (peliculaDAO.buscarPorId(peliculaId) == null) {
                            System.out.println("Error: No existe una película con ese ID.");
                            break;
                        }
                        System.out.print("Cantidad Vendida: ");
                        int cantidad = Integer.parseInt(scanner.nextLine());
                        LocalDate fecha = LocalDate.now();
                        logger.info("Intentando Registrar Venta: {} - {} - {} ", peliculaId, cantidad, fecha);
                        Venta v = new Venta(0, peliculaId, fecha, cantidad);
                        Resultado<Boolean> res = ventaDAO.registrarVentasTransaccion(v);
                        System.out.println(res.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("Valores Inválidos al Crear Venta");
                        System.out.println("Error: Ingresa valores válidos.");
                    }
                }
                case 4 -> {
                    try {
                        System.out.print("ID de la Venta a actualizar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Actualizar Venta ID: {}", id);
                        Venta existente = ventaDAO.buscarPorId(id);
                        if (existente == null) {
                            logger.warn("Venta No Encontrada para Actualizar ID: {}", id);
                            System.out.println("Venta no encontrada.");
                            break;
                        }
                        System.out.print("Nuevo ID de Película [" + existente.getPelicula_id() + "]: ");
                        int peliculaId = Integer.parseInt(scanner.nextLine());
                        if (peliculaDAO.buscarPorId(peliculaId) == null) {
                            System.out.println("Error: No existe una película con ese ID.");
                            break;
                        }
                        System.out.print("Nueva Cantidad Vendida [" + existente.getCantidad() + "]: ");
                        int cantidad = Integer.parseInt(scanner.nextLine());
                        LocalDate fecha = LocalDate.now();
                        logger.info("Datos Actualizados: {} - {} - {} ", peliculaId, cantidad, fecha);
                        existente.setFecha(fecha);
                        existente.setPelicula_id(peliculaId);
                        existente.setCantidad(cantidad);
                        // Resultado<Boolean> res = ventaDAO.actualizarConResultado(existente);
                        Resultado<Boolean> res2 = ventaDAO.registrarVentasTransaccion(existente);
                        System.out.println(res2.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("Valores Inválidos al Actualizar Venta");
                        System.out.println("Error: Ingresá valores válidos.");
                    }
                }
                case 5 -> {
                    try {
                        System.out.print("ID de la Venta a eliminar: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        logger.info("Eliminar Venta ID: {}", id);
                        Resultado<Boolean> res = ventaDAO.eliminarConResultado(id);
                        System.out.println(res.getMensaje());
                    } catch (NumberFormatException e) {
                        logger.warn("ID Inválido Ingresado para Eliminar");
                        System.out.println("Error: Ingresá un ID válido.");
                    }
                }
                case 6 -> {
                    logger.info("Saliendo del Menú de Ventas");
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
