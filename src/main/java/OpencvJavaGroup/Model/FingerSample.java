package OpencvJavaGroup.Model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class FingerSample {
    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public Map<String, Double> getFeatures2D() {
        return features2D;
    }

    public void setFeatures2D(Map<String, Double> features2D) {
        this.features2D = features2D;
    }
    private String side;
    private String series;
    private String sample;
    private Map<String, Double> features2D;
    private long timestamp;
    private Object features3D;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getFeatures3D() {
        return features3D;
    }

    public void setFeatures3D(Object features3D) {
        this.features3D = features3D;
    }
}
