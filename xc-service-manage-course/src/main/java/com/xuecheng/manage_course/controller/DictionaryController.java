package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.DictionaryApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys")
public class DictionaryController implements DictionaryApi {

    @Autowired
    CourseService courseService;

    /**
     * 课程等级和学习模式
     */
    @Override
    @GetMapping("/dictionary/get/{dType}")
    public SysDictionary getDictionary(@PathVariable String dType) {
        return courseService.getDictionary(dType);
    }
}
