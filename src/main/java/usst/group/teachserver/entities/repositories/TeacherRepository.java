package usst.group.teachserver.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import usst.group.teachserver.entities.Teacher;

import java.util.List;

public interface TeacherRepository extends CrudRepository<Teacher,Long> {
    Teacher findTeacherByPhoneNumber(String phoneNumber);

    List<Teacher> findTeachersByGrade(String grade);
}
