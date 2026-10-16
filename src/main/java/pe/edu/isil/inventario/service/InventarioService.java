package pe.edu.isil.inventario.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pe.edu.isil.inventario.domain.ProductoInventario;


@Stateless
public class InventarioService {

  @PersistenceContext(unitName = "InventarioPU")
  private EntityManager entityManager;

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public List<ProductoInventario> listarProductos() {
    return entityManager.createQuery(
            """
            SELECT p
            FROM ProductoInventario p
            ORDER BY p.id
            """,
            ProductoInventario.class
    ).getResultList();
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario obtenerProducto(Long id) {
    if (id == null || id <= 0) {
      throw datosInvalidos(
              "El identificador del producto debe ser mayor que cero."
      );
    }

    ProductoInventario producto =
            entityManager.find(ProductoInventario.class, id);

    if (producto == null) {
      throw productoNoEncontrado(
              "No existe un producto de inventario con ID " + id + "."
      );
    }

    return producto;
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario obtenerProductoPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw datosInvalidos("El código del producto es obligatorio.");
    }

    String codigoNormalizado = codigo.trim();

    List<ProductoInventario> resultado = entityManager.createQuery(
                    """
                    SELECT p
                    FROM ProductoInventario p
                    WHERE p.codigo = :codigo
                    """,
                    ProductoInventario.class
            )
            .setParameter("codigo", codigoNormalizado)
            .setMaxResults(1)
            .getResultList();

    if (resultado.isEmpty()) {
      throw productoNoEncontrado(
              "No existe un producto de inventario con código "
                      + codigoNormalizado + "."
      );
    }

    return resultado.get(0);
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario consultarStock(Long productoId) {
    return obtenerProducto(productoId);
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario consultarStockPorCodigo(String codigo) {
    return obtenerProductoPorCodigo(codigo);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario reservarStock(Long productoId, int cantidad) {
    validarCantidad(cantidad, "La cantidad a reservar");

    ProductoInventario producto = obtenerProducto(productoId);
    return reservar(producto, cantidad);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario reservarStockPorCodigo(
          String codigo,
          int cantidad
  ) {
    validarCantidad(cantidad, "La cantidad a reservar");

    ProductoInventario producto = obtenerProductoPorCodigo(codigo);
    return reservar(producto, cantidad);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario devolverStock(Long productoId, int cantidad) {
    validarCantidad(cantidad, "La cantidad a devolver");

    ProductoInventario producto = obtenerProducto(productoId);
    return devolver(producto, cantidad);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario devolverStockPorCodigo(
          String codigo,
          int cantidad
  ) {
    validarCantidad(cantidad, "La cantidad a devolver");

    ProductoInventario producto = obtenerProductoPorCodigo(codigo);
    return devolver(producto, cantidad);
  }

  private ProductoInventario reservar(
          ProductoInventario producto,
          int cantidad
  ) {
    try {
      producto.reservarStock(cantidad);
      entityManager.flush();
      return producto;

    } catch (IllegalArgumentException e) {
      throw datosInvalidos(e.getMessage());

    } catch (IllegalStateException e) {
      throw new InventarioException(
              InventarioException.Tipo.STOCK_INSUFICIENTE,
              e.getMessage()
      );

    } catch (OptimisticLockException e) {
      throw conflictoConcurrencia();
    }
  }

  private ProductoInventario devolver(
          ProductoInventario producto,
          int cantidad
  ) {
    try {
      producto.devolverStock(cantidad);
      entityManager.flush();
      return producto;

    } catch (IllegalArgumentException e) {
      throw datosInvalidos(e.getMessage());

    } catch (ArithmeticException e) {
      throw datosInvalidos(
              "La cantidad a devolver supera el límite permitido de stock."
      );

    } catch (OptimisticLockException e) {
      throw conflictoConcurrencia();
    }
  }

  private static void validarCantidad(int cantidad, String campo) {
    if (cantidad <= 0) {
      throw datosInvalidos(campo + " debe ser mayor que cero.");
    }
  }

  private static InventarioException conflictoConcurrencia() {
    return new InventarioException(
            InventarioException.Tipo.CONFLICTO_CONCURRENCIA,
            "El stock fue modificado por otra operación. "
                    + "Vuelva a consultar e intente nuevamente."
    );
  }

  private static InventarioException datosInvalidos(String mensaje) {
    return new InventarioException(
            InventarioException.Tipo.DATOS_INVALIDOS,
            mensaje
    );
  }

  private static InventarioException productoNoEncontrado(String mensaje) {
    return new InventarioException(
            InventarioException.Tipo.PRODUCTO_NO_ENCONTRADO,
            mensaje
    );
  }
}
