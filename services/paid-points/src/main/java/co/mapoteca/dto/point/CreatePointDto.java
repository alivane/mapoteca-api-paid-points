package co.mapoteca.dto.point;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePointDto {

    private UUID id;
    private Double latitude;
    private Double longitude;
    private Integer range;
    private String address;
    private String type;
    private String countryCode;
    private Integer year;
    private Integer month;
    private Integer day;


    public LocalDate getFullPointDate() {
        String date = this.day + "/" + this.month + "/" + this.year;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        return LocalDate.parse(date, formatter);
    }
}
