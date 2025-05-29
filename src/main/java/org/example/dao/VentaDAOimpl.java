package org.example.dao;

import org.example.model.Resultado;
import org.example.model.Venta;
import org.example.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VentaDAOimpl implements VentaDAO {

    private static final Logger logger = LogManager.getLogger(PeliculaDAOimpl.class);

    @Override
    public void guardar(Venta venta) {
        String sql = "INSERT INTO ventas (pelicula_id, fecha, cantidad) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venta.getPelicula_id());
            stmt.setDate(2, Date.valueOf(venta.getFecha()));
            stmt.setInt(3, venta.getCantidad());
            stmt.executeUpdate();
            logger.info("Guardada Nueva Venta: {}", venta.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error al Guardar Venta: {}", venta, e);
        }
    }

    @Override
    public Venta buscarPorId(int id) {
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Venta v = new Venta(rs.getInt("id"), rs.getInt("pelicula_id"), rs.getDate("fecha").toLocalDate(), rs.getInt("cantidad"));
                logger.info("Encontrada Venta con ID {}: {}", id, v.getFecha());
                return v;
            } else {
                logger.warn("No Existe Venta con ID: {}", id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error al Buscar Venta con ID: {}", id, e);
        }
        return null;
    }

    @Override
    public List<Venta> listarTodas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ventas";
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Venta venta = new Venta(rs.getInt("id"), rs.getInt("pelicula_id"), rs.getDate("fecha").toLocalDate(), rs.getInt("cantidad"));
                lista.add(venta);
            }
            logger.info("Se Listaron Ventas: {}", lista.size());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error al Listar Ventas: ", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Venta venta) {
        String sql = "UPDATE ventas SET pelicula_id = ?, fecha = ?, cantidad = ? WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venta.getPelicula_id());
            stmt.setDate(2, Date.valueOf(venta.getFecha()));
            stmt.setInt(3, venta.getCantidad());
            stmt.setInt(4, venta.getId());
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                logger.info("Venta con ID Actualizada Correctamente: {}", venta.getId());
            } else {
                logger.warn("No se encontró Venta con ID para Actualizar: {}", venta.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error al Actualizar Venta con ID: {}", venta.getId(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM ventas WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                logger.info("Venta con ID Eliminada Correctamente: {}", id);
                return true;
            } else {
                logger.warn("No se encontró Venta con ID para Eliminar: {}", id);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error al Cancelar Venta con ID: {}", id, e);
            return false;
        }
    }

    @Override
    public Resultado<Venta> buscarPorIdResultado(int id) {
        Venta v = buscarPorId(id);
        if (v != null) {
            return new Resultado<>(v, "Venta encontrada con éxito", true);
        } else {
            return new Resultado<>(null, "No se encontró una venta con ese ID", false);
        }
    }

    @Override
    public Resultado<Boolean> guardarConResultado(Venta venta) {
        try {
            guardar(venta);
            return new Resultado<>(true, "Venta registrada con éxito", true);
        } catch (Exception e) {
            return new Resultado<>(false, "Error al registrar venta: " + e.getMessage(), false);
        }
    }

    @Override
    public Resultado<Boolean> actualizarConResultado(Venta venta) {
        try {
            actualizar(venta);
            return new Resultado<>(true, "Venta actualizada con éxito", true);
        } catch (Exception e) {
            return new Resultado<>(false, "Error al actualizar venta: " + e.getMessage(), false);
        }
    }

    @Override
    public Resultado<Boolean> eliminarConResultado(int id) {
        boolean eliminado = eliminar(id);
        try {
            if (eliminado) {
                return new Resultado<>(true, "Venta Eliminada con éxito", true);
            } else {
                return new Resultado<>(false, "No Existe Venta con ese ID", false);
            }
        } catch (Exception e) {
            return new Resultado<>(false, "Error al eliminar venta: " + e.getMessage(), false);
        }
    }

    @Override
    public Resultado<Boolean> registrarVentasTransaccion(Venta venta) {

        String sqlInsertVenta = "INSERT INTO ventas (pelicula_id, fecha, cantidad) VALUES (?, ?, ?)";
        String sqlUpdateStock = "UPDATE peliculas SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertVenta = conn.prepareStatement(sqlInsertVenta);
                 PreparedStatement updateStock = conn.prepareStatement(sqlUpdateStock)) {
                updateStock.setInt(1, venta.getCantidad());
                updateStock.setInt(2, venta.getPelicula_id());
                updateStock.setInt(3, venta.getCantidad());
                int filas = updateStock.executeUpdate();
                if (filas == 0) {
                    conn.rollback();
                    return new Resultado<>(false, "Stock Insufiiciente o Película No Encontrada", false);
                }
                insertVenta.setInt(1, venta.getPelicula_id());
                insertVenta.setDate(2, Date.valueOf(venta.getFecha()));
                insertVenta.setInt(3, venta.getCantidad());
                insertVenta.executeUpdate();
                conn.commit();
                return new Resultado<>(true, "Venta Registrada y Stock Actualizado Correctamente", true);
            } catch (SQLException e) {
                conn.rollback();
                return new Resultado<>(false, "Error durante las Transacción", false);
            }
        } catch (SQLException e) {
            return new Resultado<>(false, "Error de Conexión", false);
        }

    }
}
