package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.COMM_FAILURE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseDictionaryRepository dictionaryRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

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
            teachplanRoot.setGrade("1");    //1级
            teachplanRoot.setStatus("0");   //未发布
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }

    /**
     * 查询课程列表
     */
    public QueryResponseResult findCourseListPage(int page, int size, CourseListRequest courseListRequest){
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage();
        if(courseListPage == null){
            return new QueryResponseResult(CommonCode.FAIL,null);
        }

        QueryResult<CourseInfo> result = new QueryResult<>();
        result.setList(courseListPage);
        result.setTotal(courseListPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,result);
    }

    /**
     * 课程等级和学习模式
     */
    public SysDictionary getDictionary(String type){
        if(type == null){
            return null;
        }
        SysDictionary sysDictionary = dictionaryRepository.findBydType(type);
        return sysDictionary;
    }

    /**
     * 新增课程
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase){
        if(courseBase == null){
            return new AddCourseResult(CommonCode.FAIL,"");
        }
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    /**
     * 根据课程id查询课程信息
     */
    public CourseBase getCourseBaseById(String courseId){
        if(courseId == null || courseId == ""){
            return null;
        }

        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 更新coursebase
     */
    public ResponseResult updateCourseBase(String id,CourseBase courseBase){
        CourseBase one = this.getCourseBaseById(id);
        if(one == null){
            ExceptionCast.cast(CommonCode.INVALIDPARAM);
        }
        BeanUtils.copyProperties(courseBase,one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询营销信息，和更新课程营销信息
     */
    public CourseMarket getCourseMarketById(String courseId){
        if(courseId == null || courseId == ""){
            return null;
        }
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    public ResponseResult updateCourseMarket(String id,CourseMarket courseMarket){
        CourseMarket one = this.getCourseMarketById(id);
        if(one == null){
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket,one);
            courseMarketRepository.save(one);
        }else{
            BeanUtils.copyProperties(courseMarket,one);
            courseMarketRepository.save(one);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 上传图片
     */
    @Transactional
    public ResponseResult uploadAndSaveImage(MultipartFile file,String courseId){
        if(courseId == null || courseId == "")
            return new ResponseResult(CommonCode.FAIL);

        ResponseResult responseResult = this.uploadImage(file);
        if(!responseResult.isSuccess()){
            return new ResponseResult(CommonCode.FAIL);
        }
        Optional<CoursePic> one = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if(one.isPresent()){
            coursePic = one.get();
        }
        if(coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(file.getOriginalFilename());
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public ResponseResult uploadImage(MultipartFile file) {
        String name = file.getOriginalFilename();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String pagePath = "I:/teach/xcEdu/xcEduUI01/xc-ui-pc-teach/static/images/"+name;
        try {
            inputStream = file.getInputStream();
            fileOutputStream = new FileOutputStream(new File(pagePath));
            //将文件内容保存到服务物理路径
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取课程图片,删除课程图片
     */
    public CoursePic getCoursePic(String courseId){
        if(courseId == null || courseId == ""){
            return null;
        }
        Optional<CoursePic> one = coursePicRepository.findById(courseId);
        if(one.isPresent()){
            return one.get();
        }
        return null;
    }

    @Transactional
    public ResponseResult deletePic(String courseId,String picName){
        if(courseId == null || courseId == ""){
            return null;
        }
        File file = new File("I:/teach/xcEdu/xcEduUI01/xc-ui-pc-teach/static/images/" + picName);
        file.delete();
        long result = coursePicRepository.deleteCoursePicByCourseid(courseId);
        if(result > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
