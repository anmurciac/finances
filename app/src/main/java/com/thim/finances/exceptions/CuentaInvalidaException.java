package com.thim.finances.exceptions;

public class CuentaInvalidaException extends Exception {
    public CuentaInvalidaException(String message) {
        super(message);
    }
    public CuentaInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}