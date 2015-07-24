package fr.cnes.sitools.dictionary.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;

import java.util.List;

public class ListCollection<T> {

    @JsonProperty("data")
    private List<T> list;

    private int total;

    public ListCollection() {
    }

    public ListCollection(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
