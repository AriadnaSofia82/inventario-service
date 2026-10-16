package pe.edu.isil.inventario.api;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;
import pe.edu.isil.inventario.dto.ApiError;

@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

  @Context
  private UriInfo uriInfo;

  @Override
  public Response toResponse(ConstraintViolationException exception) {

    String mensaje = exception.getConstraintViolations()
            .stream()
            .map(violacion -> violacion.getMessage())
            .filter(texto -> texto != null && !texto.isBlank())
            .distinct()
            .sorted()
            .collect(Collectors.joining(" "));

    if (mensaje.isBlank()) {
      mensaje = "Los datos enviados no cumplen las validaciones requeridas.";
    }

    String ruta = "";

    if (uriInfo != null && uriInfo.getPath() != null) {
      ruta = uriInfo.getPath();
    }

    int status = Response.Status.BAD_REQUEST.getStatusCode();

    ApiError error = new ApiError(
            status,
            Response.Status.BAD_REQUEST.getReasonPhrase(),
            mensaje,
            ruta
    );

    return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
  }
}