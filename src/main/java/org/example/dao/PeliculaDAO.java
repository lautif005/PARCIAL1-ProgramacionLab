package org.example.dao;

import org.example.model.Pelicula;
import org.example.model.Resultado;

import java.util.List;

public interface PeliculaDAO {

    void guardar(Pelicula pelicula);
    Pelicula buscarPorId(int id);
    List<Pelicula> listarTodas();
    void actualizar(Pelicula pelicula);
    boolean eliminar(int id);
    Resultado<Pelicula> buscarPorIdResultado(int id);
    Resultado<Boolean> guardarConResultado(Pelicula pelicula);
    Resultado<Boolean> actualizarConResultado(Pelicula pelicula);
    Resultado<Boolean> eliminarConResultado(int id);


}
