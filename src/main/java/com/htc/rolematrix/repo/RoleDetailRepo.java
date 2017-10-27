package com.htc.rolematrix.repo;

import com.htc.rolematrix.model.RoleDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kvivek on 10/22/2017.
 */
@Repository
public interface RoleDetailRepo extends JpaRepository<RoleDetailModel,Long> {
}
