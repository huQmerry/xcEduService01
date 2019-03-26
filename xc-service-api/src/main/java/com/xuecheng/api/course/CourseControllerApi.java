package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.hibernate.sql.Delete;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseControllerApi {

    public TeachplanNode  findTeachplanList(String courseId);

    public ResponseResult addTeachplan(Teachplan teachplan);

    public QueryResponseResult findCourseList(
            int page,
            int size,
            CourseListRequest courseListRequest
    );

    public AddCourseResult addCourseBase(CourseBase courseBase);

    public CourseBase getCourseBaseById(String courseId);

    public ResponseResult updateCourseBase(String id,CourseBase courseBase);

    public CourseMarket getCourseMarketById(String courseId);

    public ResponseResult UpdateCourseMarket(String id,CourseMarket courseMarket);

    public ResponseResult uploadCoursePic(MultipartFile file,String courseId);

    public CoursePic getCoursePicById(String courseId);

    public ResponseResult deletePic(String courseId,String picName);

}
