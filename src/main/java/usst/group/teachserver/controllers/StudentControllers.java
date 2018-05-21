package usst.group.teachserver.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSException;
import usst.group.teachserver.entities.*;
import usst.group.teachserver.entities.repositories.*;
import usst.group.teachserver.entities.transactEntities.TransComment;
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
    private CommentRepository commentRepository;


    public StudentControllers(StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              OneToOneRepository oneToOneRepository,
                              CourseRepository courseRepository,
                              CommentRepository commentRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.oneToOneRepository = oneToOneRepository;
        this.courseRepository = courseRepository;
        this.commentRepository = commentRepository;
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
            oneToOne=judgeOneToOneDateToChangeStatus(oneToOne);
            transOneToOnes.add(changeOneToOneToTransOneToOne(oneToOne));
        }
        return gson.toJson(transOneToOnes);
    }

    //判断OneToOne对象中的时间来调整其状态量
    OneToOne judgeOneToOneDateToChangeStatus(OneToOne oneToOne) {
        if (oneToOne.getDate().getYear()==Calendar.getInstance().getTime().getYear()&&
                oneToOne.getDate().getMonth()==Calendar.getInstance().getTime().getMonth()&&
                oneToOne.getDate().getDate()==Calendar.getInstance().getTime().getDate()) {
            oneToOne.setStatus(1);
            oneToOneRepository.save(oneToOne);
        } else if (oneToOne.getDate().after(Calendar.getInstance().getTime())) {
            oneToOne.setStatus(2);
            oneToOneRepository.save(oneToOne);
        }
        return oneToOne;
    }

    //查看老师评价
    @PostMapping(path = "/getComments")
    public @ResponseBody
    String getComments(@RequestBody String teacherPhoneNumber) {
        teacherPhoneNumber = gson.fromJson(teacherPhoneNumber, String.class);
        Teacher teacher=teacherRepository.findTeacherByPhoneNumber(teacherPhoneNumber);
        if (teacher == null) {
            return gson.toJson(null);
        }
        List<Comment> comments = commentRepository.findByTeacherId(teacher.getId());
        return gson.toJson(changeCommentToTrans(comments));

    }

    private List<TransComment> changeCommentToTrans(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        List<TransComment> commentList = new ArrayList<TransComment>();
        for (Comment comment : comments) {
            TransComment transComment = new TransComment();
            transComment.setId(comment.getId());
            transComment.setStudentPhone(studentRepository.findStudentById(comment.getStudentId()).getPhoneNumber());
            transComment.setTeacherPhone(teacherRepository.findTeacherById(comment.getTeacherId()).getPhoneNumber());
            transComment.setContent(comment.getContent());
            transComment.setDate(comment.getDate());
            commentList.add(transComment);
        }
        return commentList;
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
