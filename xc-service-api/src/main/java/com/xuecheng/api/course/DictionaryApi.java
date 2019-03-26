package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;

import java.util.List;

public interface DictionaryApi{
    public SysDictionary getDictionary(String type);
}
