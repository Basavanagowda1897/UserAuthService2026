package com.gowda.userauthservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class Role extends BaseModel {
    private String value;
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
//    private String description;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
//    public void setDescription(String description) {
//        this.description = description;

}
