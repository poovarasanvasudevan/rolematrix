package com.htc.rolematrix.pojo;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */
public class SurveyTemplate {
    String templateId; //1
    String templateInstanceId; //179
    String SurveyTemplateName;//700001000
    String surveyDescription;//700001010

    public SurveyTemplate(String templateId, String templateInstanceId, String surveyTemplateName, String surveyDescription) {
        this.templateId = templateId;
        this.templateInstanceId = templateInstanceId;
        SurveyTemplateName = surveyTemplateName;
        this.surveyDescription = surveyDescription;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateInstanceId() {
        return templateInstanceId;
    }

    public void setTemplateInstanceId(String templateInstanceId) {
        this.templateInstanceId = templateInstanceId;
    }

    public String getSurveyTemplateName() {
        return SurveyTemplateName;
    }

    public void setSurveyTemplateName(String surveyTemplateName) {
        SurveyTemplateName = surveyTemplateName;
    }

    public String getSurveyDescription() {
        return surveyDescription;
    }

    public void setSurveyDescription(String surveyDescription) {
        this.surveyDescription = surveyDescription;
    }
}
