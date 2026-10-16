package pe.edu.isil.inventario.api;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pe.edu.isil.inventario.dto.ApiError;
import pe.edu.isil.inventario.service.InventarioException;

@Provider
public class InventarioExceptionMapper
        implements ExceptionMapper<InventarioException> {

  @Context
  private UriInfo uriInfo;

  @Override
  public Response toResponse(InventarioException exception) {

    int status = switch (exception.getTipo()) {
      case PRODUCTO_NO_ENCONTRADO ->
              Response.Status.NOT_FOUND.getStatusCode();

      case STOCK_INSUFICIENTE, CONFLICTO_CONCURRENCIA ->
              Response.Status.CONFLICT.getStatusCode();

      case DATOS_INVALIDOS ->
              Response.Status.BAD_REQUEST.getStatusCode();
    };

    String ruta = "";

    if (uriInfo != null && uriInfo.getPath() != null) {
      ruta = uriInfo.getPath();
    }

    String mensaje = exception.getMessage();

    if (mensaje == null || mensaje.isBlank()) {
      mensaje = "Se produjo un error al procesar la operación de inventario.";
    }

    ApiError error = new ApiError(
            status,
            Response.Status.fromStatusCode(status).getReasonPhrase(),
            mensaje,
            ruta
    );

    return Response.status(status)
            .entity(error)
            .build();
  }
}