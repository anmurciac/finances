package com.thim.finances.exceptions;

public class TransaccionInvalidaException extends Exception {
    public TransaccionInvalidaException(String mensaje) {
        super(mensaje);
    }
    public TransaccionInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}