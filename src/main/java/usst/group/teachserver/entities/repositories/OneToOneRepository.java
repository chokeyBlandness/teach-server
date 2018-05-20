package usst.group.teachserver.entities.repositories;

import org.springframework.data.repository.CrudRepository;
import usst.group.teachserver.entities.OneToOne;

import java.util.List;

public interface OneToOneRepository extends CrudRepository<OneToOne, Long> {
    List<OneToOne> findByTeacherId(Long teacherId);

    List<OneToOne> findByStudentId(Long studentId);
}
