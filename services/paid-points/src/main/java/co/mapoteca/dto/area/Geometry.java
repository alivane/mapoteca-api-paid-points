package co.mapoteca.dto.area;


import lombok.Data;

import java.util.ArrayList;

@Data
public class Geometry {
    public String type;
    public ArrayList<Geometry2> geometries;
}
