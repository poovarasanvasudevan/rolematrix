package com.htc.rolematrix.controller;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.htc.rolematrix.constants.Constant;
import com.htc.rolematrix.model.RoleDetailModel;
import com.htc.rolematrix.model.RoleModel;
import com.htc.rolematrix.parser.ExcelParser;
import com.htc.rolematrix.pojo.Category;
import com.htc.rolematrix.pojo.SurveyTemplate;
import com.htc.rolematrix.repo.RoleDetailRepo;
import com.htc.rolematrix.repo.RoleRepo;
import com.htc.rolematrix.services.LoggerService;
import com.htc.rolematrix.services.RemedyServices;
import org.mapdb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */

@Controller
public class MainController {

    private final
    RemedyServices remedyServices;

    private final
    ExpressionParser expressionParser;

    @Autowired
    LoggerService loggerService;

    @Autowired
    ExcelParser excelParser;

    @Autowired
    public MainController(RemedyServices remedyServices, ExpressionParser expressionParser) {
        this.remedyServices = remedyServices;
        this.expressionParser = expressionParser;
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public String index(Model model, HttpServletRequest request) {
        //loggerService.log("Fetching All Categories");
        //model.addAttribute("categories", remedyServices.getAllCategory());
        return "index";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/api/category")
    public List<Category> allCategories(@RequestParam("env") String env) {
        ARServerUser arServerUser = remedyServices.arServerUser(env);
        return remedyServices.getAllCategory(arServerUser);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/api/surveyTemplate")
    public List<SurveyTemplate> getSurveyTemplate(@RequestParam("category") String category, @RequestParam("env") String env) {
        ARServerUser arServerUser = remedyServices.arServerUser(env);
        return remedyServices.getSurveyTemplate(arServerUser, category);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/api/roleDetail")
    public List<RoleDetailModel> getRoleDetail(@RequestParam("index") String index) {
        return remedyServices.getRoleDetail(index);
    }


    @Autowired
    RoleRepo roleRepo;

    @Autowired
    RoleDetailRepo roleDetailRepo;


    @Autowired
    DB db;


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/api/addRole")
    public RoleModel insertRole(@RequestParam(value = "rolex") String role_x,
                                @RequestParam(value = "roleprefix") String role_prefix,
                                @RequestParam(value = "costcode") String cost_code) {

        RoleModel jsonObject = roleRepo.save(new RoleModel(
                role_x,
                role_prefix,
                cost_code
        ));


        return jsonObject;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/api/updateRole")
    public RoleModel updateRole(@RequestParam(value = "rolex") String role_x,
                                @RequestParam(value = "roleprefix") String role_prefix,
                                @RequestParam(value = "id") Long id,
                                @RequestParam(value = "costcode") String cost_code) {


        RoleModel roleModel = roleRepo.findOne(id);
        roleModel.setCostcode(cost_code);
        roleModel.setRoleprefix(role_prefix);
        roleModel.setRolex(role_x);
        return roleRepo.save(roleModel);

    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/api/insertValue")
    public String insertValue(@RequestParam(value = "start", defaultValue = "0") String start,
                              @RequestParam(value = "template") String template,
                              @RequestParam("env") String env,
                              @RequestParam(value = "end", defaultValue = "0") String end) {

        int startValue = Integer.parseInt(start);
        int endValue = Integer.parseInt(end);
        Pageable pageable = null;

        ARServerUser arServerUser = remedyServices.arServerUser(env);
        List<RoleModel> roles = new ArrayList<>();
        List<RoleModel> allRoles = roleRepo.findAll();

        if (startValue > 0 && endValue > 0 && endValue > startValue && endValue <= allRoles.size()) {
            for (int i = startValue - 1; i < endValue - 1; i++) {
                RoleModel roleModel = new RoleModel();
                roleModel.setRolex(allRoles.get(i).getRolex());
                roleModel.setRoleprefix(allRoles.get(i).getRoleprefix());
                roleModel.setCostcode(allRoles.get(i).getCostcode());

                List<RoleDetailModel> thisRoleDetail = new ArrayList<>();

                for (RoleDetailModel roleDetailModel : allRoles.get(i).getRoleDetailModels()) {
                    RoleDetailModel roleDetailModel1 = new RoleDetailModel();
                    roleDetailModel1.setAnswerValue(roleDetailModel.getAnswerValue());
                    roleDetailModel1.setQuestionLabel(roleDetailModel.getQuestionLabel());
                    roleDetailModel1.setQuestion(roleDetailModel.getQuestion());
                    roleDetailModel1.setQuestionId(roleDetailModel.getQuestionId());
                    thisRoleDetail.add(roleDetailModel1);
                }

                roleModel.setRoleDetailModels(thisRoleDetail);
                roles.add(roleModel);
            }
        } else {
            for (int i = 0; i < allRoles.size(); i++) {
                RoleModel roleModel = new RoleModel();
                roleModel.setRolex(allRoles.get(i).getRolex());
                roleModel.setRoleprefix(allRoles.get(i).getRoleprefix());
                roleModel.setCostcode(allRoles.get(i).getCostcode());

                List<RoleDetailModel> thisRoleDetail = new ArrayList<>();

                for (RoleDetailModel roleDetailModel : allRoles.get(i).getRoleDetailModels()) {
                    RoleDetailModel roleDetailModel1 = new RoleDetailModel();
                    roleDetailModel1.setAnswerValue(roleDetailModel.getAnswerValue());
                    roleDetailModel1.setQuestionLabel(roleDetailModel.getQuestionLabel());
                    roleDetailModel1.setQuestion(roleDetailModel.getQuestion());
                    roleDetailModel1.setQuestionId(roleDetailModel.getQuestionId());
                    thisRoleDetail.add(roleDetailModel1);
                }

                roleModel.setRoleDetailModels(thisRoleDetail);
                roles.add(roleModel);
            }

        }

        for (RoleModel role : roles) {
            try {
                remedyServices.addRole(arServerUser, role, template);
                loggerService.excelProgress("Adding the Role : " + role.getRolex() + " CostCode : " + role.getCostcode());
            } catch (ARException e) {
                loggerService.errorLof("Error : " + e.getMessage());
                return e.getMessage();
            }
        }

        loggerService.excelProgress("Finish Importing...");

        return "Success";
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/api/parseExcel")
    public List<RoleModel> parseExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("templates") String templates,
            @RequestParam("category") String category,
            @RequestParam("env") String env,
            @RequestParam("answer_start_index") Integer answer_start_index) {



        List returnArray = new ArrayList<HashMap<String, String>>();


        //File file1 = new File("");


        // Create the file on server
        Set<String> unKnowm = new HashSet<>();
        try {

            roleDetailRepo.deleteAll();
            roleRepo.deleteAll();

            jdbcTemplate.execute("ALTER TABLE role_detail_model ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE role_model ALTER COLUMN id RESTART WITH 1;");


            InputStream in = file.getInputStream();
            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = "D:\\RoleMatrix\\upload\\" + file.getOriginalFilename();
            FileOutputStream f = new FileOutputStream(fileLocation);
            int ch = 0;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }
            f.flush();
            f.close();

            ARServerUser arServerUser = remedyServices.arServerUser(env);
            loggerService.log("Saved Excel");
            //    IOUtils.write(bytes,new FileWriter(new File("src/main/resources/static/upload/"+file.getOriginalFilename())));
            File serverFile = new File(fileLocation);


            //Validate Reqyired Columns

            Set<String> validationResults = excelParser.validateRequiredRows(serverFile);
            if (validationResults.size() < 3) {
                for (String validationResult : validationResults) {
                    loggerService.errorLof(validationResult + " Column is Missing... \n");
                }
            } else {

                Map<String, List<Map<String, String>>> fullMap = excelParser.parserExcel(serverFile, answer_start_index, templates);
                List<Map<String, String>> excelFile = fullMap.get("parsed");


                ConcurrentMap qmap = remedyServices.getRemedyQuestionMap(arServerUser, templates);


                String role_x = "{[" + Constant.ROLE_X + "]}";
                String role_prefix = "{[" + Constant.ROLE_PREFIX + "]}";
                String cost_code = "{[" + Constant.COST_CODE_EXCEL + "]}";

                loggerService.log("Got Parsed Excel");
                int i = 1;

                for (Map<String, String> jsonElement : excelFile) {
                    String role_x_value = "";
                    String role_prefix_value = "";
                    String cost_code_value = "";


                    EvaluationContext context = new StandardEvaluationContext(jsonElement);

                    if (role_x.startsWith("{")) {
                        Expression role_x_expression = expressionParser.parseExpression(role_x.replace("{", "").replace("}", "").trim());
                        role_x_value = (String) role_x_expression.getValue(context);
                        loggerService.log("Evaluating Role_x: " + role_x_value);
                    } else {
                        role_x_value = role_x;
                    }

                    if (role_prefix.startsWith("{")) {
                        Expression role_prefix_expression = expressionParser.parseExpression(role_prefix.replace("{", "").replace("}", "").trim());
                        role_prefix_value = (String) role_prefix_expression.getValue(context);
                        loggerService.log("Evaluating Role_Prefix: " + role_prefix_value);
                    } else {
                        role_prefix_value = role_prefix;
                    }


                    if (cost_code.startsWith("{")) {
                        Expression cost_code_expression = expressionParser.parseExpression(cost_code.replace("{", "").replace("}", "").trim());
                        cost_code_value = (String) cost_code_expression.getValue(context);
                        loggerService.log("Evaluating  CostCode: " + cost_code_value);
                    } else {
                        cost_code_value = cost_code;
                    }


                    RoleModel roleModel = new RoleModel();
                    roleModel.setCostcode(cost_code_value);
                    roleModel.setRoleprefix(role_prefix_value);
                    roleModel.setRolex(role_x_value);
                    RoleModel savedRM = roleRepo.save(roleModel);

                    ConcurrentMap map = db.getHashMap("answerMap");
                    HashMap mMap = (HashMap<String, String>) map.get(String.valueOf(i));

                    List<RoleDetailModel> roleDetailModels = new ArrayList<>();
                    for (Object mapper : mMap.keySet()) {

                        RoleDetailModel roleDetailModel = new RoleDetailModel();
                        roleDetailModel.setQuestion((String) mapper);
                        roleDetailModel.setAnswerValue(mMap.get(mapper).toString());


                        RemedyServices.TempQuestion tempQuestion = (RemedyServices.TempQuestion) qmap.get(((String) mapper).replace("–", "-"));
                        if (tempQuestion != null) {
                            roleDetailModel.setQuestionId(tempQuestion.getQuestionId());
                            roleDetailModel.setQuestionLabel(tempQuestion.getQuestionLabel());
                            roleDetailModel.setRoleModel(savedRM);

                            if (mMap.get(mapper).toString().trim().equalsIgnoreCase("X")) {
                                roleDetailModel.setAnswerValue(tempQuestion.getQuestionLabel());
                            }

                            if (!mMap.get(mapper).toString().trim().isEmpty() && !mMap.get(mapper).toString().trim().equalsIgnoreCase("NONE")) {
                                roleDetailRepo.save(roleDetailModel);
                            }
                        } else {
                            unKnowm.add("Question : " + mapper + " Does not exists in Remedy Database \n");
                        }

                    }

                    roleModel.setRoleDetailModels(roleDetailModels);
                    roleRepo.save(roleModel);


                    i++;
                }
            }


        } catch (IOException | ARException e) {
            e.printStackTrace();
        }


        unKnowm.iterator().forEachRemaining(s -> loggerService.errorLof(s));
        //loggerService.errorLof(unKnowm.);

        return roleRepo.findAll();
    }
}
