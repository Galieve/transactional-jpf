package country.lab.events;

import java.util.Objects;

public class EventData{

    String path;

    Integer time;

    EventData beginEvent;

    public EventData(String path, Integer time, EventData beginData) {
        this.path = path;
        this.time = time;
        this.beginEvent = beginData;
    }

    public EventData(String path, Integer time) {
        this.path = path;
        this.time = time;
        this.beginEvent = this;
    }

    @Override
    public int hashCode() {
        return path.hashCode()*31+ time.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        final EventData other = (EventData) obj;
        if (!Objects.equals(this.path, other.path)) return false;
        return Objects.equals(this.time, other.time);
    }

    /*
    //TODO: FIX THIS WHEN WE ARE IN A LOOP.
    @Override
    public int compareTo(EventData o) {
        if(!Objects.equals(this.path, o.path)) return path.compareTo(o.path);
        return pos.compareTo(o.pos);
    }

     */

    public String getPath() {
        return path;
    }

    public Integer getTime() {
        return time;
    }

    public EventData getBeginEvent() {
        return beginEvent;
    }
}