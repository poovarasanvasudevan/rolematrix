package com.htc.rolematrix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by kvivek on 10/22/2017.
 */
@Entity
public class RoleDetailModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 9000)
    private String question;

    @Column(length = 9000)
    private String questionLabel;

    @Column(length = 9000)
    private String answerValue;

    @Column(length = 9000)
    private String questionId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rle_detail_role_id")
    RoleModel roleModel;


    public RoleDetailModel() {
    }

    public RoleDetailModel(String question, String questionLabel, String answerValue, String questionId, RoleModel roleModel) {
        this.question = question;
        this.questionLabel = questionLabel;
        this.answerValue = answerValue;
        this.questionId = questionId;
        this.roleModel = roleModel;
    }

    public RoleModel getRoleModel() {
        return roleModel;
    }

    public void setRoleModel(RoleModel roleModel) {
        this.roleModel = roleModel;
    }

    public Long getId() {
        return id;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
