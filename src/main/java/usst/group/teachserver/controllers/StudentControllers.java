package usst.group.teachserver.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;
import usst.group.teachserver.entities.OneToOne;
import usst.group.teachserver.entities.Student;
import usst.group.teachserver.entities.Teacher;
import usst.group.teachserver.entities.repositories.OneToOneRepository;
import usst.group.teachserver.entities.repositories.StudentRepository;
import usst.group.teachserver.entities.repositories.TeacherRepository;

import java.util.List;

@RestController
@RequestMapping(path = "/student")
public class StudentControllers {

    private Gson gson;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private OneToOneRepository oneToOneRepository;

    public StudentControllers(StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              OneToOneRepository oneToOneRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.oneToOneRepository = oneToOneRepository;
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    //学生登陆
    @PostMapping(path = "/login")
    public @ResponseBody
    String studentLogin(@RequestBody String student) {
        Student studentInfo = gson.fromJson(student, Student.class);
        Student student1;
        Student foundStudent = studentRepository.findStudentByPhoneNumber(studentInfo.getPhoneNumber());
        if (foundStudent != null && foundStudent.getPassword().equals(studentInfo.getPassword())) {
            student1 = foundStudent;
            return gson.toJson(student1);
        } else {
            return gson.toJson("loginFail");
        }
    }


    //学生账号注册
    @PostMapping(path = "/register")
    public @ResponseBody
    String studentRegister(@RequestBody String student) {
        Student studentInfo = gson.fromJson(student, Student.class);
        String registerMessage;
        if (studentRepository.findStudentByPhoneNumber(studentInfo.getPhoneNumber()) != null) {
            registerMessage = "existStudent";
        } else {
            studentRepository.save(studentInfo);
            registerMessage = "registerSuccessfully";
        }
        return gson.toJson(registerMessage);
    }

    

    //查询教师信息
    @PostMapping(path = "/getTeacherList")
    public @ResponseBody
    String getTeacherList(@RequestBody String grade) {
        grade = gson.fromJson(grade, String.class);
        List<Teacher> foundTeacher;
        foundTeacher = teacherRepository.findTeachersByGrade(grade);
        return gson.toJson(foundTeacher);
    }

    //查询指定老师信息
    @PostMapping(path = "getTeacherInfo")
    public @ResponseBody
    String getTeacherInfo(@RequestBody String phoneNumber) {
        phoneNumber = gson.fromJson(phoneNumber, String.class);
        return gson.toJson(teacherRepository.findTeacherByPhoneNumber(phoneNumber));
    }

    //预约辅导
    @PostMapping(path = "/appointTutor")
    public @ResponseBody
    String increaseOneToOne(@RequestBody String tutor) {
        OneToOne oneToOne = gson.fromJson(tutor, OneToOne.class);
        oneToOneRepository.save(oneToOne);
        return gson.toJson("appointSuccessfully");
    }


    @GetMapping(path = "test")
    public @ResponseBody
    String test() {
        return gson.toJson(studentRepository.findAll());
    }
}
