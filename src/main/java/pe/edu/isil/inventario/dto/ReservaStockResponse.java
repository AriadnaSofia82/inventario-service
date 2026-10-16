package pe.edu.isil.inventario.dto;

import java.math.BigDecimal;

public class ReservaStockResponse {

  private Long productoId;
  private String codigo;
  private String nombre;
  private BigDecimal precio;
  private String operacion;
  private int cantidad;
  private int stockActual;
  private Long version;

  public ReservaStockResponse() {
  }

  public ReservaStockResponse(
          Long productoId,
          String codigo,
          String nombre,
          BigDecimal precio,
          String operacion,
          int cantidad,
          int stockActual,
          Long version
  ) {
    this.productoId = productoId;
    this.codigo = codigo;
    this.nombre = nombre;
    this.precio = precio;
    this.operacion = operacion;
    this.cantidad = cantidad;
    this.stockActual = stockActual;
    this.version = version;
  }

  public Long getProductoId() {
    return productoId;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public BigDecimal getPrecio() {
    return precio;
  }

  public String getOperacion() {
    return operacion;
  }

  public int getCantidad() {
    return cantidad;
  }

  public int getStockActual() {
    return stockActual;
  }

  public Long getVersion() {
    return version;
  }
}