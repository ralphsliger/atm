package co.com.atm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Response {
    private String descripcionRespuesta;
    private String codigoResultado;
    private String fecha;
    public Object result;
}
