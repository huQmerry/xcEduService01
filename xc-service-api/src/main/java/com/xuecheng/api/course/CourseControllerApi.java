package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

import java.util.List;

public interface CourseControllerApi {

    public TeachplanNode  findTeachplanList(String courseId);

    public ResponseResult addTeachplan(Teachplan teachplan);

    public QueryResponseResult findCourseList(
            int page,
            int size,
            CourseListRequest courseListRequest
    );


}
