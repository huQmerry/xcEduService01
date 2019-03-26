package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseDictionaryRepository extends MongoRepository<SysDictionary,String> {
    SysDictionary findBydType(String type);
}
