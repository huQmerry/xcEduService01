package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    TeachplanRepository teachplanRepository;

    public TeachplanNode findTeachplanList(String courseId){

        return teachplanMapper.selectList(courseId);
    }

    /**
     * 添加课程计划
     */
   public ResponseResult addTeachplan(Teachplan teachplan){
       if(teachplan == null ||
               StringUtils.isEmpty(teachplan.getCourseid())||
               StringUtils.isEmpty(teachplan.getPname())){
           ExceptionCast.cast(CommonCode.INVALIDPARAM);
       }

       String courseId = teachplan.getCourseid();
       String parentId = teachplan.getParentid();
       if(StringUtils.isEmpty(parentId)){
           parentId = getTeachplanRoot(courseId);
       }
       Optional<Teachplan> optional = teachplanRepository.findById(parentId);
       if(!optional.isPresent()){
          ExceptionCast.cast(CommonCode.INVALIDPARAM); 
       }
       Teachplan teachplanParent = optional.get();
       String parentGgrade = teachplanParent.getGrade();

       teachplan.setParentid(parentId);
       if(parentGgrade.equals("1")){
           teachplan.setGrade("2");
       }else if(parentGgrade.equals("2")){
           teachplan.setGrade("3");
       }
       teachplanRepository.save(teachplan);
       return new ResponseResult(CommonCode.SUCCESS);

   }

    /**
     * 获取根节点，没有则创建根节点
     */
    public String getTeachplanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();

        //获取该课程的根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size() <= 0){
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");   //1级
            teachplanRoot.setStatus("0");  //未发布
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }
}
