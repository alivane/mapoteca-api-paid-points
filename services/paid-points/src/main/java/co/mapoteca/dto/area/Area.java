package co.mapoteca.dto.area;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Area {
    public String type;
    public String id;
    public Property properties;
    public ArrayList<Feature> features;
    public Geometry geometry;

    @Data
    static
    class Property {
        private String renderType;
        private Boolean isClosed;
        private List<String> bbox;
    }

    @Data
    static
    class Geometry {
        private String type;
        private List<List<Double>> coordinates;
    }
}
