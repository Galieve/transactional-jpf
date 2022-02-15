package fr.irif.events;

import gov.nasa.jpf.vm.Instruction;

import java.util.Objects;

public class EventData implements Comparable<EventData>{

    String path;

    Instruction i;

    //Pair<Integer, Integer> info;


    public EventData(String path, Instruction i) {

        //TODO: revisar de donde viene este trId.
        this.path = path;
        this.i = i;
    }

    @Override
    public int hashCode() {
        return i.hashCode();
        //return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        final EventData other = (EventData) obj;
        if (!Objects.equals(this.path, other.path)) return false;

        return Objects.equals(this.i, other.i);
    }

    @Override
    public int compareTo(EventData o) {
        return path.compareTo(o.path);
    }

    public String getPath() {
        return path;
    }

    public Instruction getInstruction() {
        return i;
    }
}