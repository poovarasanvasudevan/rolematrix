package com.htc.rolematrix.services;

import com.bmc.arsys.api.*;
import com.bmc.arsys.api.Value;
import com.htc.rolematrix.constants.Constant;
import com.htc.rolematrix.model.RemedyContext;
import com.htc.rolematrix.model.RoleDetailModel;
import com.htc.rolematrix.model.RoleModel;
import com.htc.rolematrix.pojo.Category;
import com.htc.rolematrix.pojo.SurveyTemplate;
import com.htc.rolematrix.repo.RoleRepo;
import org.mapdb.DB;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */

@Service
public class RemedyServices {

    @Autowired
    LoggerService loggerService;

    @Autowired
    DB db;

    @org.springframework.beans.factory.annotation.Value("${htc.remedy.username}")
    String htcUsername;

    @org.springframework.beans.factory.annotation.Value("${htc.remedy.password}")
    String htcPassword;

    @org.springframework.beans.factory.annotation.Value("${htc.remedy.server}")
    String htcServer;

    @org.springframework.beans.factory.annotation.Value("${htc.remedy.port}")
    Integer htcPort;


    @org.springframework.beans.factory.annotation.Value("${cts.remedy.username}")
    String ctsUsername;

    @org.springframework.beans.factory.annotation.Value("${cts.remedy.password}")
    String ctsPassword;

    @org.springframework.beans.factory.annotation.Value("${cts.remedy.server}")
    String ctsServer;

    @org.springframework.beans.factory.annotation.Value("${cts.remedy.port}")
    Integer ctsPort;


    @org.springframework.beans.factory.annotation.Value("${prod.remedy.username}")
    String prodUsername;

    @org.springframework.beans.factory.annotation.Value("${prod.remedy.password}")
    String prodPassword;

    @org.springframework.beans.factory.annotation.Value("${prod.remedy.server}")
    String prodServer;

    @org.springframework.beans.factory.annotation.Value("${prod.remedy.port}")
    Integer prodPort;


    public ARServerUser arServerUser(String server) {
        ARServerUser arServerUser = new ARServerUser();

        if (server.equalsIgnoreCase("htc")) {
            arServerUser.setServer(htcServer);
            arServerUser.setUser(htcUsername);
            arServerUser.setPassword(htcPassword);
            arServerUser.setPort(htcPort);

        } else if (server.equalsIgnoreCase("cts")) {

            arServerUser.setServer(ctsServer);
            arServerUser.setUser(ctsUsername);
            arServerUser.setPassword(ctsPassword);
            arServerUser.setPort(ctsPort);
        } else if (server.equalsIgnoreCase("prod")) {
            arServerUser.setServer(prodServer);
            arServerUser.setUser(prodUsername);
            arServerUser.setPassword(prodPassword);
            arServerUser.setPort(prodPort);
        }

        try {
            arServerUser.login();
        } catch (ARException e) {
            e.printStackTrace();
        }
        return arServerUser;
    }


    public List<Category> getAllCategory(ARServerUser arServerUser) {


        List<Category> categories = new ArrayList<>();

        OutputInteger nMatches = new OutputInteger();
        loggerService.log("Querying to KS_SRV_Category Form");
        String applicationName = "Kinetic Request";

        //AND ('Type' = "ServiceItem")
        List<Entry> entryList = queryEntrysByQual(arServerUser, "KS_SRV_Category", new int[]{1, 179, 600000500}, "('Application' = \"" + applicationName + "\")");

        loggerService.log("Got The Result With " + nMatches.toString() + " Long");
        for (Entry entry : entryList) {
            categories.add(new Category(entry.get(179).toString(), entry.get(600000500).toString()));
        }

        categories.sort(Comparator.comparing(Category::getCategoryName));
        loggerService.log("Returning");
        return categories;
    }


    public List<Entry> queryEntrysByQual(ARServerUser server,
                                         String formName,
                                         int[] requiredFields,
                                         String qualStr) {
        List<Entry> entryList = null;
        try {

            loggerService.log("Getting " + formName);
            // Retrieve the detail info of all fields from the form.
            List<Field> fields = server.getListFieldObjects(formName);
            //
            QualifierInfo qual = null;
            if (qualStr != null) {
                qual = server.parseQualification(qualStr, fields, null, Constants.AR_QUALCONTEXT_DEFAULT);
            }

            loggerService.log("Querying : " + formName + "with " + qualStr);

            OutputInteger nMatches = new OutputInteger();
            // Retrieve entries from the form using the given
            // qualification.
            entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    null, requiredFields, true, nMatches);


            loggerService.log("Returning Result");
        } catch (ARException e) {
            e.printStackTrace();
        }
        return entryList;
    }


    public List<SurveyTemplate> getSurveyTemplate(ARServerUser arServerUser, String category) {
        List<SurveyTemplate> surveyTemplates = new ArrayList<>();


        int[] rf = new int[]{1, 179, 700001000, 700001010};
        List<Entry> entries = queryEntrysByQual(arServerUser, "KS_SRV_SurveyTemplate", rf, "( 'Category' = \"" + category + "\" ) AND ('Type' = \"Service Items\")");

        for (Entry entry : entries) {
            surveyTemplates.add(new SurveyTemplate(
                    entry.get(rf[0]).toString(),
                    entry.get(rf[1]).toString(),
                    entry.get(rf[2]).toString(),
                    entry.get(rf[3]).toString()
            ));
        }

        return surveyTemplates;
    }

    public Map<String, Object> getFormFields(ARServerUser arServerUser, String formName) {
        Map<String, Object> fieldResult = new HashMap<>();
        try {
            List<Field> fields = arServerUser.getListFieldObjects(formName);
            for (Field field : fields) {
                fieldResult.put(field.getName().toLowerCase(), field.getFieldID());
            }

        } catch (ARException e) {
            e.printStackTrace();
        }
        return fieldResult;

    }

    public void addRole(ARServerUser arServerUser, RoleModel roleParams, String templateId) throws ARException {


        Entry entry = new Entry();
        entry.put(Constant.ROLE_X_COLUMN, new Value(roleParams.getRolex().trim()));
        entry.put(Constant.ROLE_PREFIX_COLUMN, new Value(roleParams.getRoleprefix().trim()));
        entry.put(Constant.COST_CODE, new Value(roleParams.getCostcode().trim()));
        entry.put(Constant.TEMPLATE_INSTANCE_ID_COLUMN, new Value(templateId.trim()));

        String entryId = arServerUser.createEntry(
                Constant.ROLE_TABLE,
                entry
        );


        loggerService.roleLog("============================================================ \n");
        loggerService.roleLog("Inserted : " + roleParams.getCostcode() + " ==> " + entryId + "\n");
        loggerService.roleLog("Fetching : " + roleParams.getCostcode() + " ==> " + entryId + "\n");
        Entry entry1 = arServerUser.getEntry(Constant.ROLE_TABLE, entryId, new int[]{179});
        if (entry1 != null) {
            for (RoleDetailModel roleDetailModel : roleParams.getRoleDetailModels()) {
                addRoleDetail(arServerUser, roleDetailModel, entry1.get(179).toString(), templateId);
            }
        }


    }

    public void addRoleDetail(ARServerUser arServerUser, RoleDetailModel roleDetailParams, String parentRole, String templateId) throws ARException {
        Entry entry = new Entry();

        entry.put(Constant.ROLE_DETAIL_UNIQUE_IDENTIFIER, new Value(parentRole.trim()));
        entry.put(Constant.ROLE_DETAIL_QUESTION_INSTANCE_ID, new Value(roleDetailParams.getQuestionId().trim()));
        entry.put(Constant.ROLE_DETAIL_CHANGE_TO, new Value(roleDetailParams.getAnswerValue().trim()));
        entry.put(Constant.ROLE_DETAIL_TEMPLATE_INSTANCE_ID, new Value(templateId.trim()));

        String entryId = arServerUser.createEntry(
                Constant.ROLE_DETAIL_TABLE,
                entry
        );

        loggerService.roleLog("Inserted : " + roleDetailParams.getQuestion() + " ==> " + entryId + "\n");

    }

    public String getQuestionId(ARServerUser arServerUser, String templateId, String question) {
        List<Entry> questionEntry = queryEntrysByQual(
                arServerUser,
                "KS_SRV_SurveyQuestion",
                new int[]{179, 700060003},
                "('SurveyInstanceID' = \"" + templateId + "\")"
        );

        String result = "";

        if (questionEntry.size() == 1) {
            result = questionEntry.get(0).get("179").toString() + "@" + questionEntry.get(0).get("700060003").toString();
        }
        return result;
    }

    public class RoleDetails_x {
        String question;
        String questionLabel;
        String questionId;
        String questionValue;


        public RoleDetails_x(String question, String questionLabel, String questionId, String questionValue) {
            this.question = question;
            this.questionLabel = questionLabel;
            this.questionId = questionId;
            this.questionValue = questionValue;
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

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getQuestionValue() {
            return questionValue;
        }

        public void setQuestionValue(String questionValue) {
            this.questionValue = questionValue;
        }
    }

    @Autowired
    RoleRepo roleRepo;

    public List<RoleDetailModel> getRoleDetail(String costcode) {

        List<RoleModel> roleModel = roleRepo.findAll();
        RoleModel roleModel1 = new RoleModel();
        List<RoleModel> filterRole = roleModel.parallelStream()
                .filter(new Predicate<RoleModel>() {
                    @Override
                    public boolean test(RoleModel roleModel) {
                        return roleModel.getCostcode().trim().equalsIgnoreCase(costcode);
                    }
                })
                .collect(Collectors.toList());

        if (filterRole != null && filterRole.size() == 1) {
            return filterRole.get(0).getRoleDetailModels();
        } else {
            return null;
        }


    }

    public void addToQuestionMap(String key, String value) {
        ConcurrentMap map = db.createHashMap("questionMap")
                .makeOrGet();

        map.put(key, value);
    }

    public ConcurrentMap getQuestionMap() {
        ConcurrentMap map = db.createHashMap("questionMap")
                .makeOrGet();

        return map;
    }

    @org.springframework.beans.factory.annotation.Value("${remedy.questionform}")
    String questionFOrm;

    public class TempQuestion {
        String questionId;
        String questionLabel;

        public String getQuestionId() {
            return questionId;
        }

        public TempQuestion(String questionId, String questionLabel) {
            this.questionId = questionId;
            this.questionLabel = questionLabel;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getQuestionLabel() {
            return questionLabel;
        }

        public void setQuestionLabel(String questionLabel) {
            this.questionLabel = questionLabel;
        }
    }


    public ConcurrentMap<String, TempQuestion> getRemedyQuestionMap(ARServerUser arServerUser, String templateId) throws ARException {

        ConcurrentMap map = new ConcurrentHashMap<String, TempQuestion>();

        if (map.size() == 0) {

            List<Entry> fields = queryEntrysByQual(arServerUser, questionFOrm, new int[]{179, 700000003, 700060003}, "('SurveyInstanceID' = \"" + templateId + "\")");
            for (Entry field : fields) {
                try {
                    if (field.get(700060003) != null && !field.get(700060003).toString().trim().isEmpty()) {

                        TempQuestion tempQuestion = new TempQuestion(
                                field.get(179).toString(),
                                field.get(700060003).toString()
                        );
                        map.put(field.get(700000003).toString(), tempQuestion);

                    }
                } catch (NullPointerException e) {
                    loggerService.log("Question or answwer is Null");
                }
            }
        }

        return map;
    }

    public void addToMap(String key, HashMap<String, String> value) {
        ConcurrentMap map = db.createHashMap("answerMap")
                .makeOrGet();


        map.put(key, value);
        // System.out.println(map.size());
        //db.close();
    }
}
