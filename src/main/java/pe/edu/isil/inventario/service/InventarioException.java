package pe.edu.isil.inventario.service;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InventarioException extends RuntimeException {

  public enum Tipo {
    PRODUCTO_NO_ENCONTRADO,
    STOCK_INSUFICIENTE,
    CONFLICTO_CONCURRENCIA,
    DATOS_INVALIDOS
  }

  private final Tipo tipo;

  public InventarioException(Tipo tipo, String message) {
    super(message);

    if (tipo == null) {
      throw new IllegalArgumentException(
              "El tipo de excepción es obligatorio."
      );
    }

    this.tipo = tipo;
  }

  public Tipo getTipo() {
    return tipo;
  }
}
