package usst.group.teachserver.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;
import usst.group.teachserver.entities.*;
import usst.group.teachserver.entities.repositories.CourseRepository;
import usst.group.teachserver.entities.repositories.OneToOneRepository;
import usst.group.teachserver.entities.repositories.StudentRepository;
import usst.group.teachserver.entities.repositories.TeacherRepository;
import usst.group.teachserver.entities.transactEntities.TransOneToOne;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/student")
public class StudentControllers {

    private Gson gson;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private OneToOneRepository oneToOneRepository;
    private CourseRepository courseRepository;

    public StudentControllers(StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              OneToOneRepository oneToOneRepository,
                              CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.oneToOneRepository = oneToOneRepository;
        this.courseRepository = courseRepository;
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
        if (grade.equals("小学生")) {
            foundTeacher = teacherRepository.findTeachersByGrade("高一");
        } else {
            foundTeacher = teacherRepository.findTeachersByGrade(grade);
        }
        return gson.toJson(foundTeacher);
    }

    //查询指定老师信息
    @PostMapping(path = "getTeacherInfo")
    public @ResponseBody
    String getTeacherInfo(@RequestBody String phoneNumber) {
        phoneNumber = gson.fromJson(phoneNumber, String.class);
        return gson.toJson(teacherRepository.findTeacherByPhoneNumber(phoneNumber));
    }

    //查询指定老师可预约时间
    @PostMapping(path = "/getTeacherCourses")
    public @ResponseBody
    String getTeacherCourses(@RequestBody String teacherInfo) {
        teacherInfo = gson.fromJson(teacherInfo, String.class);
        List<Course> teacherCourses = courseRepository.findByTeacherId(teacherRepository.findTeacherByPhoneNumber(teacherInfo).getId());
        List<Date> coursesDate = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,30);
        for (Course course : teacherCourses) {
            if (course.getIsAble() == 1) {
                if (course.getDay().before(calendar.getTime())) {
                    coursesDate.add(course.getDay());
                }
            }
        }
        List<Date> notOrderedDates = new ArrayList<Date>();
        for (Calendar calendar1 = Calendar.getInstance();
             calendar1.getTime().before(calendar.getTime());
             calendar1.add(Calendar.DATE, 1)) {
            boolean isOrdered = false;
            for (Date date : coursesDate) {
                if (calendar1.getTime().getYear() == date.getYear() &&
                        calendar1.getTime().getMonth() == date.getMonth() &&
                        calendar1.getTime().getDate() == date.getDate()) {
                    isOrdered = true;
                }
            }
            if (isOrdered) {
                notOrderedDates.add(calendar1.getTime());
            }
        }
        return gson.toJson(notOrderedDates);
    }

    //查询指定学生辅导信息
    @PostMapping(path = "/getTutors")
    public @ResponseBody
    String getTutors(@RequestBody String studentPhoneNumber) {
        studentPhoneNumber = gson.fromJson(studentPhoneNumber, String.class);
        List<OneToOne> oneToOnes = oneToOneRepository.findByStudentId(
                studentRepository.findStudentByPhoneNumber(studentPhoneNumber).getId()
        );
        List<TransOneToOne> transOneToOnes = new ArrayList<TransOneToOne>();
        for (OneToOne oneToOne : oneToOnes) {
            transOneToOnes.add(changeOneToOneToTransOneToOne(oneToOne));
        }
        return gson.toJson(transOneToOnes);
    }

    //预约辅导
    @PostMapping(path = "/appointTutor")
    public @ResponseBody
    String increaseOneToOne(@RequestBody String tutor) {
        TransOneToOne transOneToOne = gson.fromJson(tutor, TransOneToOne.class);
        OneToOne newOneToOne = changeTransOneToOneToOneToOne(transOneToOne);
        if (newOneToOne == null) {
            return gson.toJson("appointFail");
        }
        oneToOneRepository.save(newOneToOne);
        return gson.toJson("appointSuccessfully");
    }


    private TransOneToOne changeOneToOneToTransOneToOne(OneToOne oneToOne) {
        if (oneToOne == null) {
            return null;
        }
        TransOneToOne transOneToOne = new TransOneToOne();
        transOneToOne.setStudentPhone(studentRepository.findStudentById(oneToOne.getStudentId()).getPhoneNumber());
        transOneToOne.setTeacherPhone(teacherRepository.findTeacherById(oneToOne.getTeacherId()).getPhoneNumber());
        transOneToOne.setDate(oneToOne.getDate());
        transOneToOne.setStatus(oneToOne.getStatus());
        return transOneToOne;
    }

    private OneToOne changeTransOneToOneToOneToOne(TransOneToOne transOneToOne) {
        if (transOneToOne == null) {
            return null;
        }
        OneToOne oneToOne = new OneToOne();
        oneToOne.setTeacherId(teacherRepository.findTeacherByPhoneNumber(transOneToOne.getTeacherPhone()).getId());
        oneToOne.setStudentId(studentRepository.findStudentByPhoneNumber(transOneToOne.getStudentPhone()).getId());
        oneToOne.setDate(transOneToOne.getDate());
        oneToOne.setStatus(transOneToOne.getStatus());
        return oneToOne;
    }

}
