package com.md.solutions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.md.solutions.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>{

	@Query("select r from Roles r where r.name=:roleName")
	public Roles findByRoleName(@Param("roleName") String roleName);
}
