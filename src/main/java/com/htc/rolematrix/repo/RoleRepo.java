package com.htc.rolematrix.repo;

import com.htc.rolematrix.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kvivek on 10/22/2017.
 */
@Repository
public interface RoleRepo extends JpaRepository<RoleModel, Long> {
    @Query("SELECT p FROM RoleModel p WHERE LOWER(p.costcode) = LOWER(:costcode)")
    public List<RoleModel> findRoleModel(@Param("costcode") String costcode);

}
