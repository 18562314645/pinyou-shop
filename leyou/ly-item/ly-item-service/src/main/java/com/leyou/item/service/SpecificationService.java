package com.leyou.item.service;

import com.leyou.item.pojo.SpecParam;
import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.groupMapper;
import com.leyou.item.mapper.specParamMapper;
import com.leyou.item.pojo.SpecGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationService {
    @Autowired
    private groupMapper groupMapper;

    @Autowired
    private specParamMapper specParamMapper;
    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryById(Long cid) {
        SpecGroup group=new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = groupMapper.select(group);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnums.SPEC_GROUP_NOT_FIND);
        }
        return list;
    }

    /**
     * 增加分组
     */
    public void addGroup(SpecGroup specGroup){
        int i = groupMapper.insert(specGroup);
        System.out.println(i);
        if(i!=1){
            throw new LyException(ExceptionEnums.ADD_SPEC_GROUP_FILE);
        }
    }

    /**
     * 更新分组
     */
    public void updateGroup(SpecGroup specGroup){
        int i = groupMapper.updateByPrimaryKey(specGroup);
        System.out.println("==========================="+i);
        if(i!=1){
            throw new LyException(ExceptionEnums.UPDATE_SPECCGROUP_FILE);
        }
    }

    /**
     * 删除分组
     */
    public void deleGroup(Long id){
        groupMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam param=new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setGeneric(generic);
        param.setSearching(searching);
        List<SpecParam> paramList = specParamMapper.select(param);
        if(CollectionUtils.isEmpty(paramList)){
            throw new LyException(ExceptionEnums.SPECPARAMS_NOT_FIND);
        }
        return paramList;
    }


    /**
     * 根据cid查询规格组及对用的规格参数
     * @return
     */
    public List<SpecGroup> querySpecsByCid(Long cid) {
        //1.获取规格组对象
        List<SpecGroup> specGroups = queryById(cid);

        //2.查询组内参数
        specGroups.forEach(g->{
            g.setParams(queryParams(g.getId(),null,null,null));
        });
        return specGroups;
    }
}
