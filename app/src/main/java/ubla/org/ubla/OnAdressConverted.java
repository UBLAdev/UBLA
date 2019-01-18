package ubla.org.ubla;

import com.mapbox.geojson.Point;

import java.util.Map;

public interface OnAdressConverted {
    void onOriginConverted(Point point);
    void onDestinationConverted(Point point);
}
