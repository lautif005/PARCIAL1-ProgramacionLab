package org.example.model;

public class Pelicula {

    private int id;
    private String titulo;
    private String genero;
    private double precio;
    private int stock;

    public Pelicula() {
    }

    public Pelicula(int id, String titulo, String genero, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.precio = precio;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "PELICULA" + "\n" +
                "ID: " + id + "\n" +
                "Titulo: '" + titulo + "\n" +
                "Genero: " + genero + "\n" +
                "Precio: " + precio + "\n" +
                "Stock: " + stock + "\n";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
