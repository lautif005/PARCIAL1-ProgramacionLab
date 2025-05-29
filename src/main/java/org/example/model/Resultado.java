package org.example.model;

public class Resultado<T> {

    private final T dato;
    private final String mensaje;
    private final boolean exitoso;

    public Resultado(T dato, String mensaje, boolean exitoso) {
        this.dato = dato;
        this.mensaje = mensaje;
        this.exitoso = exitoso;
    }

    public T getDato() {
        return dato;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    @Override
    public String toString() {
        return mensaje + (dato != null ? " - Dato: " + dato.toString() : "");
    }
}
