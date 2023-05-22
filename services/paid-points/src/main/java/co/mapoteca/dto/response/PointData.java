package co.mapoteca.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointData {

    private List<PointInformationResponse> errors;
    private List<PointInformationResponse> successful;
    private Integer created;

    public PointData(List<PointInformationResponse> responses) {
        this.successful = responses.stream().filter(el -> el.getStatus().is2xxSuccessful()).toList();
        this.errors = responses.stream().filter(el -> !el.getStatus().is2xxSuccessful()).toList();
    }

    public Integer getCreated() {
        return successful.size() - errors.size();
    }

}
