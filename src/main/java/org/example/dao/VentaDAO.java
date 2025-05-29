package org.example.dao;

import org.example.model.Resultado;
import org.example.model.Venta;

import java.util.List;

public interface VentaDAO {

    void guardar(Venta venta);
    Venta buscarPorId(int id);
    List<Venta> listarTodas();
    void actualizar(Venta venta);
    boolean eliminar(int id);
    Resultado<Venta> buscarPorIdResultado(int id);
    Resultado<Boolean> guardarConResultado(Venta venta);
    Resultado<Boolean> actualizarConResultado(Venta venta);
    Resultado<Boolean> eliminarConResultado(int id);
    Resultado<Boolean> registrarVentasTransaccion(Venta venta);

}

