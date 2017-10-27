package com.htc.rolematrix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by kvivek on 10/22/2017.
 */
@Entity
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String rolex;


    @Column
    private String roleprefix;

    @Column
    private String costcode;


    @JsonIgnore
    @OneToMany(mappedBy = "roleModel", cascade = CascadeType.ALL)
    private List<RoleDetailModel> roleDetailModels;


    public RoleModel() {
    }

    public Long getId() {
        return id;
    }

    public String getRolex() {
        return rolex;
    }

    public void setRolex(String rolex) {
        this.rolex = rolex;
    }

    public String getRoleprefix() {
        return roleprefix;
    }

    public void setRoleprefix(String roleprefix) {
        this.roleprefix = roleprefix;
    }

    public String getCostcode() {
        return costcode;
    }

    public void setCostcode(String costcode) {
        this.costcode = costcode;
    }

    public List<RoleDetailModel> getRoleDetailModels() {
        return roleDetailModels;
    }

    public void setRoleDetailModels(List<RoleDetailModel> roleDetailModels) {
        this.roleDetailModels = roleDetailModels;
    }

    public RoleModel(String rolex, String roleprefix, String costcode) {
        this.rolex = rolex;
        this.roleprefix = roleprefix;
        this.costcode = costcode;
    }

    public RoleModel(String rolex, String roleprefix, String costcode, List<RoleDetailModel> roleDetailModels) {
        this.rolex = rolex;
        this.roleprefix = roleprefix;
        this.costcode = costcode;
        this.roleDetailModels = roleDetailModels;
    }
}
