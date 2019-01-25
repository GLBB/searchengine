package cn.gl.analysis.mapper;


import cn.gl.analysis.dto.GraspPage;
import cn.gl.analysis.vo.FilepathURL;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Set;


@Mapper
public interface FilePageMapper {

    @Insert("insert into graspPage(file_path, url, downdate) values(#{filePath}, #{url}, #{downdate})")
    public Integer insert(GraspPage graspPage);

    @Select("select * from graspPage where file_path=#{path}")
    public GraspPage getGraspPageByPath(String path);

    @Select("select file_path from graspPage")
    public Set<String> getAllFilename();

    @Select("select file_path as filepath, url from graspPage")
    Set<FilepathURL> getAllFilepathURL();

}
