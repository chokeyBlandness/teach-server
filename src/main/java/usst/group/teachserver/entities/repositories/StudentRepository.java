package usst.group.teachserver.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import usst.group.teachserver.entities.Student;


public interface StudentRepository extends CrudRepository<Student,Long> {
    Student findStudentByPhoneNumber(String phoneNumber);

    Student findStudentById(Long id);
}
