package com.md.solutions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.md.solutions.entity.Users;

@Repository
public interface UserDetailsRepository extends JpaRepository<Users, Long>{

	@Query("select u from Users u where u.username=:userName")
	public Optional<Users> getUserByName(@Param("userName") String userName);
}
