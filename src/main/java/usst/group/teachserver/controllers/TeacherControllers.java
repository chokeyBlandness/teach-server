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
import usst.group.teachserver.entities.transactEntities.OneToOneStudentInfo;
import usst.group.teachserver.entities.transactEntities.TransCourse;

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
        TransCourse courseInfo = gson.fromJson(course, TransCourse.class);
        List<Course> courses = courseRepository.findByTeacherId(teacherRepository
                .findTeacherByPhoneNumber(courseInfo.getTeacherPhone())
                .getId());
        for (Course course1 : courses) {
            if (course1.getDay().getYear() == courseInfo.getDate().getYear() &&
                    course1.getDay().getMonth() == courseInfo.getDate().getMonth() &&
                    course1.getDay().getDate() == courseInfo.getDate().getDate()) {
                return gson.toJson("exist");
            }
        }
        Course newCourse = new Course();
        newCourse.setTeacherId(teacherRepository.findTeacherByPhoneNumber(courseInfo.getTeacherPhone()).getId());
        newCourse.setDay(courseInfo.getDate());
        newCourse.setIsAble(1);
        courseRepository.save(newCourse);
        return gson.toJson("succeed");
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


}
