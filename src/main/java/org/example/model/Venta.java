package org.example.model;

import java.time.LocalDate;

public class Venta {

    private int id;
    private int pelicula_id;
    private LocalDate fecha;
    private int cantidad;

    public Venta() {
    }

    public Venta(int id, int pelicula_id, LocalDate fecha, int cantidad) {
        this.id = id;
        this.pelicula_id = pelicula_id;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "VENTA" + "\n" +
                "ID: " + id + "\n" +
                "ID Película: '" + pelicula_id + "\n" +
                "Fecha: " + fecha + "\n" +
                "Cantidad: " + cantidad + "\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPelicula_id() {
        return pelicula_id;
    }

    public void setPelicula_id(int pelicula_id) {
        this.pelicula_id = pelicula_id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
