package co.mapoteca.dto.area;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Geometry2 {
    public String type;
    public ArrayList<ArrayList<ArrayList<Double>>> coordinates;
}
