package org.example.dao;

import org.example.model.Pelicula;
import org.example.model.Resultado;
import org.example.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PeliculaDAOimpl implements PeliculaDAO {

    private static final Logger logger = LogManager.getLogger(PeliculaDAOimpl.class);

    @Override
    public void guardar(Pelicula pelicula) {
        String sql = "INSERT INTO peliculas (titulo, genero, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getGenero());
            stmt.setDouble(3, pelicula.getPrecio());
            stmt.setInt(4, pelicula.getStock());
            stmt.executeUpdate();
            logger.info("Nueva Película Guardada: {}", pelicula.getTitulo());
        } catch (SQLException e) {
            logger.error("Error al Guardad Película: {}", pelicula, e);
        }
    }

    @Override
    public Pelicula buscarPorId(int id) {
        String sql = "SELECT * FROM peliculas WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Pelicula p = new Pelicula(rs.getInt("id"), rs.getString("titulo"), rs.getString("genero"), rs.getDouble("precio"), rs.getInt("stock"));
                logger.info("Encontrada película {} con ID: {}", p.getTitulo(), id);
                return p;
            } else {
                logger.info("No existe película con ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error al Buscar Película con ID {}: {}", id, e);
        }
        return null;
    }

    @Override
    public List<Pelicula> listarTodas() {
        List<Pelicula> lista = new ArrayList<>();
        String sql = "SELECT * FROM peliculas";
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pelicula pelicula = new Pelicula(rs.getInt("id"), rs.getString("titulo"), rs.getString("genero"), rs.getDouble("precio"), rs.getInt("stock"));
                lista.add(pelicula);
            }
            logger.info("Se Listaron Películas: {}", lista.size());
        } catch (SQLException e) {
            logger.error("Error al Listar Películas: ", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Pelicula pelicula) {
        String sql = "UPDATE peliculas SET titulo = ?, genero = ?, precio = ?, stock = ? WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pelicula.getTitulo());
            stmt.setString(2, pelicula.getGenero());
            stmt.setDouble(3, pelicula.getPrecio());
            stmt.setInt(4, pelicula.getStock());
            stmt.setInt(5, pelicula.getId());
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                logger.info("Película con ID Actualizada Correctamente: {}", pelicula.getId());
            } else {
                logger.warn("No se Encontró Película con ID para Actualizar: {}", pelicula.getId());
            }
        } catch (SQLException e) {
            logger.error("Error al Actualizar Película con ID: {}", pelicula.getId());
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM peliculas WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                logger.info("Película con ID Eliminada Correctamente: {}", id);
                return true;
            } else {
                logger.warn("No se Encontró Película con ID para Eliminar: {}", id);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error al Eliminar Película con ID: {}", id, e);
            return false;
        }
    }

    @Override
    public Resultado<Pelicula> buscarPorIdResultado(int id) {
        Pelicula p = buscarPorId(id);
        if (p != null) {
            return new Resultado<>(p, "Pelicula Encontrada con Éxito", true);
        } else {
            return new Resultado<>(null, "No se Encontró una Pelicula con ese ID", false);
        }
    }

    @Override
    public Resultado<Boolean> guardarConResultado(Pelicula pelicula) {
        try {
            guardar(pelicula);
            return new Resultado<>(true, "Pelicula Registrada con Éxito", true);
        } catch (Exception e) {
            return new Resultado<>(false, "Error al Registrar Película: ", false);
        }
    }

    @Override
    public Resultado<Boolean> actualizarConResultado(Pelicula pelicula) {
        try {
            actualizar(pelicula);
            return new Resultado<>(true, "Película Actualizada con Éxito", true);
        } catch (Exception e) {
            return new Resultado<>(false, "Error al Actualizar Película", false);
        }
    }

    @Override
    public Resultado<Boolean> eliminarConResultado(int id) {
        boolean eliminado = eliminar(id);
        try {
            if (eliminado) {
                return new Resultado<>(true, "Película Eliminada con Éxito", true);
            } else {
                return new Resultado<>(false, "No Existe Película con ese ID", false);
            }
        } catch (Exception e) {
            return new Resultado<>(false, "Error al Eliminar Película", false);
        }
    }
}
