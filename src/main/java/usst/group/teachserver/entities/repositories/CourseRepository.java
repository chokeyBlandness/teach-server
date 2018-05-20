package usst.group.teachserver.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import usst.group.teachserver.entities.Course;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course,Long> {
    List<Course> findByTeacherId(Long teacherId);
}
