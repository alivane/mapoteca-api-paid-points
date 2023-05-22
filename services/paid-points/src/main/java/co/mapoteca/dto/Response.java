package co.mapoteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private T data;
    private String message;
    private Long total;
    private String error;

    public Response(String message) {
        this.message = message;
    }
    public static <T> Response<T> data(T data) {
        return Response.<T>builder().data(data).build();
    }
    public static <T> Response<T> data(T data, long total) {
        return Response.<T>builder().data(data).total(total).build();
    }
    public static Response<Object> message(String message) {
        return Response.builder().message(message).build();
    }
}
