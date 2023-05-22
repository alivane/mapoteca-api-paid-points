package co.mapoteca.dto.area;

import lombok.Data;

@Data
public class Feature {
    public String type;
    public Geometry geometry;
    public Properties properties;
}
