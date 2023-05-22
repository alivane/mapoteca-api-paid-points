package co.mapoteca.utils;

import co.mapoteca.entity.PointDate;

import java.util.Comparator;

public class PointDateComparator implements Comparator<PointDate> {
    @Override
    public int compare(PointDate pointDate, PointDate t1) {
        return t1.getDateTime().compareTo(pointDate.getDateTime());
    }
}
