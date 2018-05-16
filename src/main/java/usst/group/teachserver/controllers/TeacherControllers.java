package usst.group.teachserver.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;
import usst.group.teachserver.entities.Course;
import usst.group.teachserver.entities.OneToOne;
import usst.group.teachserver.entities.Student;
import usst.group.teachserver.entities.Teacher;
import usst.group.teachserver.entities.repositories.CourseRepository;
import usst.group.teachserver.entities.repositories.OneToOneRepository;
import usst.group.teachserver.entities.repositories.StudentRepository;
import usst.group.teachserver.entities.repositories.TeacherRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/teacher")
public class TeacherControllers {

    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private OneToOneRepository oneToOneRepository;

    private Gson gson;

    public TeacherControllers(TeacherRepository teacherRepository,
                              StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              OneToOneRepository oneToOneRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.oneToOneRepository = oneToOneRepository;
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    //教师账号注册
    @PostMapping(path = "/register")
    public @ResponseBody
    String teacherRegister(@RequestBody String teacher) {
        Teacher teacherInfo = gson.fromJson(teacher, Teacher.class);
        String registerMessage;
        if (teacherRepository.findTeacherByPhoneNumber(teacherInfo.getPhoneNumber()) != null) {
            registerMessage = "existTeacher";
        } else {
            teacherRepository.save(teacherInfo);
            registerMessage = "registerSuccessfully";
        }

        return gson.toJson(registerMessage);
    }

    //教师登陆
    @PostMapping(path = "/login")
    public @ResponseBody
    String teacherLogin(@RequestBody String teacher) {
        Teacher teacherInfo = gson.fromJson(teacher, Teacher.class);
        Boolean loginMessage = false;
        Teacher foundTeacher = teacherRepository.findTeacherByPhoneNumber(teacherInfo.getPhoneNumber());
        if (foundTeacher != null && foundTeacher.getPassword().equals(teacherInfo.getPassword())) {
            loginMessage = true;
        }
        return gson.toJson(loginMessage);
    }

    //教师信息查询
    @PostMapping(path = "/getInfo")
    public @ResponseBody
    String getInfo(@RequestBody String phoneNumber) {
        phoneNumber = gson.fromJson(phoneNumber, String.class);
        Teacher teacherInfo = teacherRepository.findTeacherByPhoneNumber(phoneNumber);
        return gson.toJson(teacherInfo);
    }

    //增加课程
    @PostMapping(path = "/increaseCourse")
    public @ResponseBody
    String increaseCourse(@RequestBody String course) {
        Course courseInfo = gson.fromJson(course, Course.class);
        courseRepository.save(courseInfo);
        return gson.toJson(courseInfo);
    }

    @GetMapping(path = "/getCourses")
    public @ResponseBody
    String getCourses() {
        return gson.toJson(courseRepository.findAll());
    }

    //查询已预约学生信息
    @PostMapping(path = "/getOneToOneStudents")
    public @ResponseBody
    String getOneToOneStudents(@RequestBody String teacherPhoneNumber) {
        teacherPhoneNumber = gson.fromJson(teacherPhoneNumber, String.class);
        Teacher teacher = teacherRepository.findTeacherByPhoneNumber(teacherPhoneNumber);
        if (teacher == null) {
            return gson.toJson(teacher);
        }
        List<OneToOne> oneToOnes = oneToOneRepository.findByTeacherId(teacher.getId());
        if (oneToOnes == null) {
            return gson.toJson(oneToOnes);
        }
        List<OneToOneStudentInfo> oneToOneStudentInfos = new ArrayList<OneToOneStudentInfo>();
        for (OneToOne oneToOne : oneToOnes) {
            Student student=studentRepository.findStudentById(oneToOne.getStudentId());
            oneToOneStudentInfos.add(new OneToOneStudentInfo(student.getPhoneNumber(),
                    student.getName(),oneToOne.getDate()));
        }

        return gson.toJson(oneToOneStudentInfos);
    }

    //已预约学生信息
    private class OneToOneStudentInfo {
        private String phoneNumber;
        private String name;
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public OneToOneStudentInfo(String phoneNumber, String name, Date date) {
            this.phoneNumber = phoneNumber;
            this.name = name;
            this.date = date;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
