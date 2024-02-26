package com.email.repo;


import com.email.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;
@EnableScheduling
@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
