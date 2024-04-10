package ait.cohort34.accounting.dao;

import ait.cohort34.accounting.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserAccount, String> {
}
