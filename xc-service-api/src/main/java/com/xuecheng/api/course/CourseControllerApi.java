package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseControllerApi {

    public TeachplanNode  findTeachplanList(String courseId);

    public ResponseResult addTeachplan(Teachplan teachplan);
}
