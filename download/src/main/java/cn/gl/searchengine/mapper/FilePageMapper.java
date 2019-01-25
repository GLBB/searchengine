package cn.gl.searchengine.mapper;

import cn.gl.searchengine.dto.GraspPage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@Mapper
public interface FilePageMapper {

    @Insert("insert into graspPage(file_path, url, downdate) values(#{filePath}, #{url}, #{downdate})")
    public Integer insert(GraspPage graspPage);

    @Select("select * from graspPage where file_path=#{path}")
    GraspPage getGraspPageByPath(String path);

    @Select("select file_path from graspPage")
    Set<String> getAllFilename();

}
