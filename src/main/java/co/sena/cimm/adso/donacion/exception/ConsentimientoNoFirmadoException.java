package co.sena.cimm.adso.donacion.exception;

public class ConsentimientoNoFirmadoException extends RuntimeException {

    public ConsentimientoNoFirmadoException() {
        super("El donante no tiene consentimiento informado firmado. No puede registrar una donación.");
    }

    public ConsentimientoNoFirmadoException(String message) {
        super(message);
    }
}