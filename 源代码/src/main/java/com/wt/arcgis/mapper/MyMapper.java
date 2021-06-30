package com.wt.arcgis.mapper;

import com.wt.arcgis.pojo.*;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Mapper
public interface MyMapper
{
    @Select("select * from tb_user where username=#{username} and password=#{password} and state=1")
    public User getUserByAccount(User user);//根据用户名密码取得简单用户信息

    @Select("select * from tb_user where username=#{username} and password=#{password} ")
    public User getUserByAccountNoState(User user);//根据用户名密码取得简单用户信息，不论state状态如何

    @Select("select D.userid,D.departmentid as 'department.departmentid',D.postid as 'post.postid',D.username,D.password,D.realname,D.gender,D.telephone,D.createtime,D.state,D.id,D.roleid as 'role.roleid',D.rolename as 'role.rolename',D.detail as 'role.detail',D.privilegeid as 'privilege.privilegeid',D.privilegecode as 'privilege.privilegecode',D.privilegename as 'privilege.privilegename', tb_department.departmentname as 'department.departmentname', tb_department.parentid as 'department.parentid', tb_post.postname as 'post.postname', tb_post.postdetail as 'post.postdetail' from (select C.userid,C.departmentid,C.postid,C.username,C.password,C.realname,C.gender,C.telephone,C.createtime,C.state,C.id,C.roleid,C.rolename,C.detail,C.privilegeid,tb_privilege.privilegecode,tb_privilege.privilegename from  ( select B.userid,B.departmentid,B.postid,B.username,B.password,B.realname,B.gender,B.telephone,B.createtime,B.state,B.id,B.roleid,B.rolename,B.detail,tb_role_privilege.privilegeid from  ( select A.userid,A.departmentid,A.postid,A.username,A.password,A.realname,A.gender,A.telephone,A.createtime,A.state,A.id,A.roleid, tb_role.rolename,tb_role.detail from  ( select tb_user.userid,departmentid,postid,username,password,realname,gender,telephone,createtime,state,id,roleid from tb_user left join tb_user_role on tb_user.userid = tb_user_role.userid where tb_user.userid=#{userid} ) as A left join tb_role on A.roleid=tb_role.roleid ) as B  left join tb_role_privilege on B.roleid= tb_role_privilege.roleid ) C left join tb_privilege on C.privilegeid = tb_privilege.privilegeid ) as D ,tb_department, tb_post  where tb_department.departmentid = D.departmentid and tb_post.postid = D.postid ")
    public List<User> getUserInfo(User user);//取得完整用户信息

    @Select("select * from tb_department where parentid is null")
    public List<Department> getRootDepartment();//根部门

    @Select("select * from tb_department where parentid=#{1}")
    public List<Department> getSubDepartment(int pid);//根据PID取得子部门

    @Select("select * from ${tableName} where parentmenueid is null;")
    public List<Menue> getRootMenue(@Param("tableName") String tableName);//根菜单

    @Select("select * from ${tableName} where parentmenueid=#{pid}")
    public List<Menue> getSubMenue(@Param("pid") int pid, @Param("tableName") String tableName);//子菜单

    @Select("select * from tb_role ")
    public List<Role> getRole();

    @Select("select * from tb_post ")
    public List<Post> getPost();

    @Insert("insert into tb_user(departmentid,postid,username,password,realname,gender,telephone,state,createtime) values(#{department.departmentid},#{post.postid},#{username},#{password},#{realname},#{gender},#{telephone},#{state},#{createtime}) ")
    public int insertUser(User user);//添加用户

    @Insert("insert into tb_user_role(roleid,userid) values(#{role.roleid},#{userid}) ")
    public int insertUserRole(User user);//用户角色中间表

    @Select("select * from ${tableName} where menueid=#{menueid}")
    public Menue getMenueByMenueId(@Param("menueid") int menueid, @Param("tableName") String tableName);

    @Select("select * from tb_addressinfo where ParentId=0" )
    public List<Administration> getRootAdministration();

    @Select("select * from tb_addressinfo where ParentId=#{parentId}")
    public List<Administration> getSubAdministrations(@Param("parentId") int parentId);

    @Select("select * from tb_special_menue where parentid is null;")
    public List<SpecialMenue> getRootSpecialMenue();//根专项调查菜单

    @Select("select * from tb_special_menue where parentid=#{parentid}")
    public List<SpecialMenue> getSubSpecialMenue(@Param("parentid") int parentid);//子专项调查菜单

    @Select("select * from tb_special_menue_update where parentid is null;")
    public List<SpecialMenue> getRootSpecialMenueUpdate();//根专项调查更新菜单

    @Select("select * from tb_special_menue_update where parentid=#{parentid}")
    public List<SpecialMenue> getSubSpecialMenueUpdate(@Param("parentid") int parentid);//子专项调查更新菜单

    @Select("select * from tb_analysis_menue left join tb_analysis_data on tb_analysis_menue.id = tb_analysis_data.id where parentid is null")
    public List<AnalysisMenue> getRootAnalysisMenue();//根统计分析菜单

    @Select("select * from tb_analysis_menue left join tb_analysis_data on tb_analysis_menue.id = tb_analysis_data.id where parentid=#{parentid}")
    public List<AnalysisMenue> getSubAnalysisMenue(@Param("parentid") int parentid);//子统计分析菜单





    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_dltb where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_DLTB getLastUpdateDLTBService( @Param("type")int type);//取得最后一次更新的地类图斑服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_dltb where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_DLTB getDLTBServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得地类图斑服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,version,title from tb_dltb where run=1 and type=${type};")
    public List<TB_DLTB> getAllDLTBServiceVersion( @Param("type")int type);//取得所有地类图斑服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_xzq where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_XZQ getLastUpdateXZQService( @Param("type")int type);//取得最后一次更新的行政区服务根据服务类型(0动态地图，1要素，2影像，5000 1：5000缩放隐藏)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_xzq where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_XZQ getXZQServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间，服务类型取得行政区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_xzq where run=1 and type=${type};")
    public List<TB_XZQ> getAllXZQServiceVersion( @Param("type")int type);//根据服务类型取得所有行政区服务版本日期



    @Select("select * from tb_imagelayer where date_format(updatetime, '%Y-%m-%d' )=(select distinct date_format(updatetime, '%Y-%m-%d' )updatetime from tb_imagelayer where run=1 and type=${type} order by updatetime desc limit 0,1);")
    public List<TB_IMAGELAYER> getAllRunImageLayerService( @Param("type")int type);//取得最有一次更新所有启用影像服务根据服务类型(0动态地图，1要素，2影像，5000 1：5000缩放隐藏)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime from tb_imagelayer where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime};")
    public List<TB_IMAGELAYER>  getAllRunImageLayerByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间，服务类型取得全部启用影像服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime from tb_imagelayer where run=1 and type=${type};")
    public List<TB_IMAGELAYER>  getAllRunImageLayerServiceVersion( @Param("type")int type);//根据服务类型取得所有启用影像服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_ccwjq where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_CCWJQ getLastUpdateCCWJQService( @Param("type")int type);//取得最后一次更新的拆除未尽区服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_ccwjq where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_CCWJQ getCCWJQServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得拆除未尽区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_ccwjq where run=1 and type=${type};")
    public List<TB_CCWJQ> getAllCCWJQServiceVersion( @Param("type")int type);//取得所有拆除未尽区服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_czcdyd where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_CZCDYD getLastUpdateCZCDYDService( @Param("type")int type);//取得最后一次更新的城镇村等用地服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_czcdyd where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_CZCDYD getCZCDYDServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得城镇村等用地服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_czcdyd where run=1 and type=${type};")
    public List<TB_CZCDYD> getAllCZCDYDServiceVersion( @Param("type")int type);//取得所有拆城镇村等用地服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_cjdcqjx where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_CJDCQJX getLastUpdateCJDCQJXService( @Param("type")int type);//取得最后一次更新的村籍调查区界线服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_cjdcqjx where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_CJDCQJX getCJDCQJXServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得村籍调查区界线服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_cjdcqjx where run=1 and type=${type};")
    public List<TB_CJDCQJX> getAllCJDCQJXServiceVersion( @Param("type")int type);//取得所有村籍调查区界线服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_gjgy where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_GJGY getLastUpdateGJGYService( @Param("type")int type);//取得最后一次更新的国家公园服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_gjgy where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_GJGY getGJGYServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得国家公园服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_gjgy where run=1 and type=${type};")
    public List<TB_GJGY> getAllGJGYServiceVersion( @Param("type")int type);//取得所有国家公园服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_kfyq where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_KFYQ getLastUpdateKFYQService( @Param("type")int type);//取得最后一次更新的开发园区服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_kfyq where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_KFYQ getKFYQServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得开发园区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_kfyq where run=1 and type=${type};")
    public List<TB_KFYQ> getAllKFYQServiceVersion( @Param("type")int type);//取得所有开发园区服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_lsyd where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_LSYD getLastUpdateLSYDService( @Param("type")int type);//取得最后一次更新的临时用地服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_lsyd where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_LSYD getLSYDServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得临时用地服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_lsyd where run=1 and type=${type};")
    public List<TB_LSYD> getAllLSYDServiceVersion( @Param("type")int type);//取得所有临时用地服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_pdt where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_PDT getLastUpdatePDTService( @Param("type")int type);//取得最后一次更新的坡度图服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_pdt where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_PDT getPDTServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得坡度图服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_pdt where run=1 and type=${type};")
    public List<TB_PDT> getAllPDTServiceVersion( @Param("type")int type);//取得所有坡度图服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_pdt where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_SDGY getLastUpdateSDGYService( @Param("type")int type);//取得最后一次更新的湿地公园服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_pdt where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_SDGY getSDGYServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得湿地公园服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_pdt where run=1 and type=${type};")
    public List<TB_SDGY> getAllSDGYServiceVersion( @Param("type")int type);//取得所有湿地公园服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_sdgy where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_SLGY getLastUpdateSLGYService( @Param("type")int type);//取得最后一次更新的森林公园服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_sdgy where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_SLGY getSLGYServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得森林公园服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_sdgy where run=1 and type=${type};")
    public List<TB_SLGY> getAllSLGYServiceVersion( @Param("type")int type);//取得所有森林公园服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_stbhhx where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_STBHHX getLastUpdateSTBHHXService( @Param("type")int type);//取得最后一次更新的生态保护红线服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_stbhhx where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_STBHHX getSTBHHXServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得生态保护红线服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_stbhhx where run=1 and type=${type};")
    public List<TB_STBHHX> getAllSTBHHXServiceVersion( @Param("type")int type);//取得所有生态保护红线服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_ttq where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_TTQ getLastUpdateTTQService( @Param("type")int type);//取得最后一次更新的推土区服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_ttq where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_TTQ getTTQServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得推土区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_ttq where run=1 and type=${type};")
    public List<TB_TTQ> getAllTTQServiceVersion( @Param("type")int type);//取得所有推土区服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_xzqjx where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_XZQJX getLastUpdateXZQJXService( @Param("type")int type);//取得最后一次更新的行政区界线服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_xzqjx where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_XZQJX getXZQJXServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得行政区界线服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_xzqjx where run=1 and type=${type};")
    public List<TB_XZQJX> getAllXZQJXServiceVersion( @Param("type")int type);//取得所有行政区界线服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_yjjbnt where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_YJJBNT getLastUpdateYJJBNTService( @Param("type")int type);//取得最后一次更新的永久基本农田服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_yjjbnt where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_YJJBNT getYJJBNTServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得永久基本农田服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_yjjbnt where run=1 and type=${type};")
    public List<TB_YJJBNT> getAllYJJBNTServiceVersion( @Param("type")int type);//取得所有永久基本农田服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_yjjbnt where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_ZRBHQ getLastUpdateZRBHQService( @Param("type")int type);//取得最后一次更新的自然保护区服务(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from tb_yjjbnt where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_ZRBHQ getZRBHQServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type);//根据更新时间取得自然保护区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from tb_yjjbnt where run=1 and type=${type};")
    public List<TB_ZRBHQ> getAllZRBHQServiceVersion( @Param("type")int type);//取得所有自然保护区服务版本日期



    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from ${physicstable} where run=1 and type=${type} order by updatetime desc limit 0,1 ;")
    public TB_PHYSICSTABLE getLastUpdatephysicstableService( @Param("type")int type, @Param("physicstable")String physicstable);//取得最后一次更新的xx服务physicstable(0动态地图，1要素，2影像)

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime,run,createtime,title from ${physicstable} where run=1 and type=${type} and date_format(updatetime, '%Y-%m-%d' )=#{argupdatetime} order by updatetime desc limit 0,1 ;")
    public TB_PHYSICSTABLE getPhysicsServiceByUpdatetime(@Param("argupdatetime")String updatetime, @Param("type")int type, @Param("physicstable")String physicstable);//根据更新时间取得自然保护区服务

    @Select("select id,servicename,serviceaddr,databasename,tablename, type, updatetime ,run,createtime,title from ${physicstable} where run=1 and type=${type};")
    public List<TB_PHYSICSTABLE> getAllPhysicsServiceVersion( @Param("type")int type,@Param("physicstable")String physicstable);//取得所有自然保护区服务版本日期

}
