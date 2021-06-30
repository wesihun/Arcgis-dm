package com.wt.arcgis.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wt.arcgis.Config;
import com.wt.arcgis.mapper.MyMapper;
import com.wt.arcgis.pojo.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials="true")
public class MyController {
    @Autowired
    MyMapper myMapper;

    @Autowired
    Config config;

    @RequestMapping(value="login", produces = "application/json;charset=utf-8")
    public String getUser(User user,  HttpSession session) {// 登录
        User resultUser = myMapper.getUserByAccount(user);

        String json = "{" + '"' + "result" + '"' + ":" + '"' + "fail" + '"' + "}";

        if (null == resultUser) {
            return json;
        } else {
            session.setAttribute("user", resultUser);
            session.setMaxInactiveInterval(60 * 30);
            json = "{" + '"' + "result" + '"' + ":" + '"' + "success" + '"' + "}";


            return json;
        }

    }

    @RequestMapping(value = "getUserInfo", produces = "application/json;charset=utf-8")
    public List<User> getUserInfo(HttpSession session) {// 取得用户完整信息
        User user = (User) session.getAttribute("user");

        List<User> listUser = myMapper.getUserInfo(user);
        return listUser;
    }

    @RequestMapping(value = "getDepartment", produces = "application/json;charset=utf-8")
    public List<Department> getDepartment() {// 取得部门
        List<Department> root = new ArrayList<Department>();

        List<Department> listDepartment = myMapper.getRootDepartment();

        for (int i = 0; i < listDepartment.size(); i++) {
            Department rootDepartment = listDepartment.get(i);

            rootDepartment.setSubDepartment(this.getSubDepartment(rootDepartment));

            root.add(rootDepartment);
        }

        return root;
    }

    public List<Department> getSubDepartment(Department rootDepartment) {// 递归部门
        List<Department> subDepartmentList = new ArrayList<Department>();
        List<Department> subList = myMapper.getSubDepartment(rootDepartment.getDepartmentid());

        if (subList == null) {
            return subDepartmentList;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                Department subDepartment = subList.get(i);

                subDepartment.setSubDepartment(this.getSubDepartment(subDepartment));
                subDepartmentList.add(subDepartment);
            }
        }

        return subDepartmentList;
    }

    @RequestMapping(value = "getMenue", produces = "application/json;charset=utf-8")
    public List<Menue> getMenue(String version) {// 取得菜单
        if(null == version || "".equals(version)) return null;
        List<Menue> root = new ArrayList<Menue>();

        String tableName = "tb_menue";
        if(version.equals("二调")) {tableName = "tb_menue_ed";}

        List<Menue> listMenue = myMapper.getRootMenue(tableName);

        for (int i = 0; i < listMenue.size(); i++) {
            Menue rootMenue = listMenue.get(i);

            rootMenue.setSubMenue(this.getSubMenue(rootMenue, tableName));

            root.add(rootMenue);
        }

        return root;
    }

    public List<Menue> getSubMenue(Menue rootMenue, String tableName) {// 递归菜单
        List<Menue> subMenueList = new ArrayList<Menue>();
        List<Menue> subList = myMapper.getSubMenue(rootMenue.getMenueid(), tableName);

        if (subList == null) {
            return null;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                Menue subMenue = subList.get(i);

                subMenue.setSubMenue(this.getSubMenue(subMenue, tableName));
                subMenueList.add(subMenue);
            }
        }

        return subMenueList;
    }

    @RequestMapping(value = "upload", produces = "application/json;charset=utf-8") // 单文件上传
    public String upload(@RequestParam("file") MultipartFile multipartFile)
            throws IllegalStateException, IOException {
        if (multipartFile.isEmpty()) {
            return "no file";
        }

        double MB = multipartFile.getSize() / 1024 / 1024.0;
        System.out.println("MB:" + MB);

        String folder = config.getFile_dir();

        String upFileName = multipartFile.getOriginalFilename();

        String path = folder + upFileName;

        File myFile = new File(path);

        if (!myFile.getParentFile().exists()) {
            myFile.getParentFile().mkdirs();
        }

        multipartFile.transferTo(myFile);

        return path;
    }

    @RequestMapping(value = "uploadMulty", produces = "application/json;charset=utf-8")
    public String uploadMulty(@RequestParam("file") MultipartFile[] multipartFile)
            throws IllegalStateException, IOException {// 多文件上传

        String folder = config.getFile_dir();

        for (int i = 0; i < multipartFile.length; i++) {
            MultipartFile sigleFile = multipartFile[i];

            if (sigleFile.isEmpty()) {
                return "no single file";
            }

            double MB = sigleFile.getSize() / 1024 / 1024.0;
            System.out.println("MB:" + MB);

            String upFileName = sigleFile.getOriginalFilename();
            String path = folder + upFileName;
            File myFile = new File(path);

            if (!myFile.getParentFile().exists()) {
                myFile.getParentFile().mkdirs();
            }

            sigleFile.transferTo(myFile);

        }

        return folder;
    }

    @RequestMapping("download")
    public void download(HttpServletResponse response) throws IOException {//下载

        String requestFileName = "xx.jpg";
        String filename = config.getFile_dir() + requestFileName;

         // 设置信息给客户端不解析
         String type = new MimetypesFileTypeMap().getContentType(filename);
         // 设置contenttype，即告诉客户端所发送的数据属于什么类型
         response.setHeader("Content-type",type);
         // 设置编码
         String hehe = new String(requestFileName.getBytes("utf-8"), "iso-8859-1");
         // 设置扩展头，当Content-Type 的类型为要下载的类型时 , 这个信息头会告诉浏览器这个文件的名字和类型。
         response.setHeader("Content-Disposition", "attachment;filename=" + hehe);

        // 发送给客户端的数据
         OutputStream outputStream = response.getOutputStream();
         byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        // 读取filename
        bis = new BufferedInputStream(new FileInputStream(new File(filename)));
        int i = bis.read(buff);
        while (i != -1) {
            outputStream.write(buff, 0, buff.length);
            outputStream.flush();
            i = bis.read(buff);
        }
    }


    @RequestMapping("getRole")
    public List<Role> getRole(){//角色

        List<Role> roleList =  myMapper.getRole();

        return roleList;
    }

    @RequestMapping("getPost")
    public List<Post> getPost(){//岗位
        List<Post> postList = myMapper.getPost();

        return postList;
    }
  
    @RequestMapping("regist")
    @Transactional(rollbackFor=Exception.class)
    public String regist(User user) throws RuntimeException {//注册

        String json = "{" + '"' + "result" + '"' + ":" + '"' + "success" + '"' + "}";
        User checkUser = myMapper.getUserByAccountNoState(user); //账户重复
        if(null !=checkUser){
            json = "{" + '"' + "result" + '"' + ":" + '"' + "repeat" + '"' + "}";
            return json;
        }

        user.setCreatetime(new Date());

        try{
            myMapper.insertUser(user);
            User savedUser = myMapper.getUserByAccountNoState(user);
            user.setUserid(savedUser.getUserid());

            myMapper.insertUserRole(user);
        }catch(Exception e){
            e.printStackTrace();
            json = "{" + '"' + "result" + '"' + ":" + '"' + "fail" + '"' + "}";
            throw new RuntimeException();
        }
        finally{
            return json;
        }
    }

    @RequestMapping(value = "getSecondCategory", produces = "application/json;charset=utf-8")
    public List<Menue> getSecondCategory(Menue menue, String version){//取得目录树点击的二级地类编码
        if(null == version || "".equals(version)) return null;

        String tableName = "tb_menue";
        if(version.equals("二调")) {tableName = "tb_menue_ed";}

        List<Menue> menueList = this.getSubMenue(menue, tableName);

        return menueList;
    }

    @RequestMapping(value = "getMenueByMenueId", produces = "application/json;charset=utf-8")
    public Menue getMenueByMenueId(int menueid, String version){//根据menueid取得menue
        if(null == version || "".equals(version)) return null;

        String tableName = "tb_menue";
        if(version.equals("二调")) {tableName = "tb_menue_ed";}

        Menue resultMenue = myMapper.getMenueByMenueId(menueid, tableName);

        return resultMenue;
    }


    @RequestMapping(value = "getAdministration", produces = "application/json;charset=utf-8")
    public List<Administration> getAdministration() {// 取得行政区
        List<Administration> root = new ArrayList<Administration>();

        List<Administration> listAdministrations = myMapper.getRootAdministration();

        for (int i = 0; i < listAdministrations.size(); i++) {
            Administration rootAdministration = listAdministrations.get(i);

            rootAdministration.setSubAdministrations(this.getSubAdministration(rootAdministration));

            root.add(rootAdministration);
        }

        return root;
    }

    public List<Administration> getSubAdministration(Administration administration) {//递归行政区
        List<Administration> subAdministrationList = new ArrayList<Administration>();
        List<Administration> subList = myMapper.getSubAdministrations(administration.getId());

        if (subList == null) {
            return null;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                Administration subAdministration = subList.get(i);

                subAdministration.setSubAdministrations(this.getSubAdministration(subAdministration));
                subAdministrationList.add(subAdministration);
            }
        }

        return subAdministrationList;
    }

    @RequestMapping(value="logOut", produces = "application/json;charset=utf-8")
    public void logOut(HttpSession session) {// 退出
        session.removeAttribute("user");

    }

    @RequestMapping(value = "getSpecialMenue", produces = "application/json;charset=utf-8")
    public List<SpecialMenue> getSpecialMenue() {// 取得专项菜单
        List<SpecialMenue> root = new ArrayList<SpecialMenue>();

        List<SpecialMenue> listMenue = myMapper.getRootSpecialMenue();

        for (int i = 0; i < listMenue.size(); i++) {
            SpecialMenue rootMenue = listMenue.get(i);

            rootMenue.setSubSpecialMenue(this.getSubSpecialMenue(rootMenue));

            root.add(rootMenue);
        }

        return root;
    }

    public List<SpecialMenue> getSubSpecialMenue(SpecialMenue rootMenue) {// 递归专项菜单
        List<SpecialMenue> subMenueList = new ArrayList<SpecialMenue>();
        List<SpecialMenue> subList = myMapper.getSubSpecialMenue(rootMenue.getId());

        if (subList == null) {
            return null;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                SpecialMenue subMenue = subList.get(i);

                subMenue.setSubSpecialMenue(this.getSubSpecialMenue(subMenue));
                subMenueList.add(subMenue);
            }
        }

        return subMenueList;
    }


    @RequestMapping(value = "getSpecialMenueUpdate", produces = "application/json;charset=utf-8")
    public List<SpecialMenue> getSpecialMenueUpdate() {// 取得专项更新菜单
        List<SpecialMenue> root = new ArrayList<SpecialMenue>();

        List<SpecialMenue> listMenue = myMapper.getRootSpecialMenueUpdate();

        for (int i = 0; i < listMenue.size(); i++) {
            SpecialMenue rootMenue = listMenue.get(i);

            rootMenue.setSubSpecialMenue(this.getSubSpecialMenueUpdate(rootMenue));

            root.add(rootMenue);
        }

        return root;
    }

    public List<SpecialMenue> getSubSpecialMenueUpdate(SpecialMenue rootMenue) {// 递归专项更新菜单
        List<SpecialMenue> subMenueList = new ArrayList<SpecialMenue>();
        List<SpecialMenue> subList = myMapper.getSubSpecialMenueUpdate(rootMenue.getId());

        if (subList == null) {
            return null;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                SpecialMenue subMenue = subList.get(i);

                subMenue.setSubSpecialMenue(this.getSubSpecialMenueUpdate(subMenue));
                subMenueList.add(subMenue);
            }
        }

        return subMenueList;
    }

    @RequestMapping(value = "getAnalysisMenue", produces = "application/json;charset=utf-8")
    public List<AnalysisMenue> getAnalysisMenue() {// 取得统计分析菜单
        List<AnalysisMenue> root = new ArrayList<AnalysisMenue>();

        List<AnalysisMenue> listMenue = myMapper.getRootAnalysisMenue();

        for (int i = 0; i < listMenue.size(); i++) {
            AnalysisMenue rootMenue = listMenue.get(i);

            rootMenue.setSubAnalysisMenue(this.getSubAnalysisMenue(rootMenue));

            root.add(rootMenue);
        }

        return root;
    }

    public List<AnalysisMenue> getSubAnalysisMenue(AnalysisMenue rootMenue) {// 递归专项更新菜单
        List<AnalysisMenue> subMenueList = new ArrayList<AnalysisMenue>();
        List<AnalysisMenue> subList = myMapper.getSubAnalysisMenue(rootMenue.getId());

        if (subList == null) {
            return null;
        } else {

            for (int i = 0; i < subList.size(); i++) {
                AnalysisMenue subMenue = subList.get(i);

                subMenue.setSubAnalysisMenue(this.getSubAnalysisMenue(subMenue));
                subMenueList.add(subMenue);
            }
        }

        return subMenueList;
    }



    @RequestMapping(value = "getLastUpdateDLTBService", produces = "application/json;charset=utf-8")
    public TB_DLTB getLastUpdateDLTBService(int type) {//取得最后一次更新的地类图斑服务
        TB_DLTB tb_dltb = myMapper.getLastUpdateDLTBService(type);
        return tb_dltb;
    }

    @RequestMapping(value = "getDLTBServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_DLTB getDLTBServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_DLTB tb_dltb = myMapper.getDLTBServiceByUpdatetime(updatetime, type);
        return tb_dltb;
    }

    @RequestMapping(value = "getAllDLTBServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_DLTB> getAllDLTBServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_DLTB> tb_dltbList = myMapper.getAllDLTBServiceVersion(type);
        return tb_dltbList;
    }

    @RequestMapping(value = "getLastUpdateXZQService", produces = "application/json;charset=utf-8")
    public TB_XZQ getLastUpdateXZQService(int type) {//取得最后一次更新的行政区服务
        TB_XZQ tb_xzq = myMapper.getLastUpdateXZQService(type);
        return tb_xzq;
    }

    @RequestMapping(value = "getXZQServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_XZQ getXZQServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得行政区服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_XZQ tb_xzq = myMapper.getXZQServiceByUpdatetime(updatetime, type);
        return tb_xzq;
    }

    @RequestMapping(value = "getAllXZQServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_XZQ> getAllXZQServiceVersion(int type) {//取得所有行政区服务版本日期
        List<TB_XZQ> tb_xzqList = myMapper.getAllXZQServiceVersion(type);
        return tb_xzqList;
    }


    @RequestMapping(value = "getAllRunImageLayerService", produces = "application/json;charset=utf-8")
    public List<TB_IMAGELAYER> getAllRunImageLayerService(int type) {//取得最有一次更新所有启用影像服务根据服务类型(0动态地图，1要素，2影像，5000 1：5000缩放隐藏)
        List<TB_IMAGELAYER> tb_imagelayerList = myMapper.getAllRunImageLayerService(type);
        return tb_imagelayerList;
    }

    @RequestMapping(value = "getAllRunImageLayerByUpdatetime", produces = "application/json;charset=utf-8")
    public List<TB_IMAGELAYER> getAllRunImageLayerByUpdatetime(String updatetime, int type) {//根据更新时间，服务类型取得全部启用影像服务
        if(null==updatetime || "".equals(updatetime)) return null;

        List<TB_IMAGELAYER> tb_imagelayerList = myMapper.getAllRunImageLayerByUpdatetime(updatetime, type);
        return tb_imagelayerList;
    }

    @RequestMapping(value = "getAllRunImageLayerServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_IMAGELAYER> getAllRunImageLayerServiceVersion(int type) {//根据服务类型取得所有启用影像服务版本日期
        List<TB_IMAGELAYER> tb_imagelayerList = myMapper.getAllRunImageLayerServiceVersion(type);
        return tb_imagelayerList;
    }








    @RequestMapping(value = "getLastUpdateCCWJQService", produces = "application/json;charset=utf-8")
    public TB_CCWJQ getLastUpdateCCWJQService(int type) {//取得最后一次更新的地类图斑服务
        TB_CCWJQ tb_ccwjq = myMapper.getLastUpdateCCWJQService(type);
        return tb_ccwjq;
    }

    @RequestMapping(value = "getCCWJQServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_CCWJQ getCCWJQServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_CCWJQ tb_ccwjq = myMapper.getCCWJQServiceByUpdatetime(updatetime, type);
        return tb_ccwjq;
    }

    @RequestMapping(value = "getAllCCWJQServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_CCWJQ> getAllCCWJQServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_CCWJQ> tb_ccwjqList = myMapper.getAllCCWJQServiceVersion(type);
        return tb_ccwjqList;
    }


    @RequestMapping(value = "getLastUpdateCZCDYDService", produces = "application/json;charset=utf-8")
    public TB_CZCDYD getLastUpdateCZCDYDService(int type) {//取得最后一次更新的地类图斑服务
        TB_CZCDYD tb_czcdyd = myMapper.getLastUpdateCZCDYDService(type);
        return tb_czcdyd;
    }

    @RequestMapping(value = "getCZCDYDServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_CZCDYD getCZCDYDServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_CZCDYD tb_czcdyd = myMapper.getCZCDYDServiceByUpdatetime(updatetime, type);
        return tb_czcdyd;
    }

    @RequestMapping(value = "getAllCZCDYDServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_CZCDYD> getAllCZCDYDServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_CZCDYD> tb_czcdydList = myMapper.getAllCZCDYDServiceVersion(type);
        return tb_czcdydList;
    }


    @RequestMapping(value = "getLastUpdateCJDCQJXService", produces = "application/json;charset=utf-8")
    public TB_CJDCQJX getLastUpdateCJDCQJXService(int type) {//取得最后一次更新的地类图斑服务
        TB_CJDCQJX tb_cjdcqjx = myMapper.getLastUpdateCJDCQJXService(type);
        return tb_cjdcqjx;
    }

    @RequestMapping(value = "getCJDCQJXServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_CJDCQJX getCJDCQJXServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_CJDCQJX tb_cjdcqjx = myMapper.getCJDCQJXServiceByUpdatetime(updatetime, type);
        return tb_cjdcqjx;
    }

    @RequestMapping(value = "getAllCJDCQJXServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_CJDCQJX> getAllCJDCQJXServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_CJDCQJX> tb_cjdcqjxList = myMapper.getAllCJDCQJXServiceVersion(type);
        return tb_cjdcqjxList;
    }


    @RequestMapping(value = "getLastUpdateGJGYService", produces = "application/json;charset=utf-8")
    public TB_GJGY getLastUpdateGJGYService(int type) {//取得最后一次更新的地类图斑服务
        TB_GJGY tb_gjgy = myMapper.getLastUpdateGJGYService(type);
        return tb_gjgy;
    }

    @RequestMapping(value = "getGJGYServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_GJGY getGJGYServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_GJGY tb_gjgy = myMapper.getGJGYServiceByUpdatetime(updatetime, type);
        return tb_gjgy;
    }

    @RequestMapping(value = "getAllGJGYServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_GJGY> getAllGJGYServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_GJGY> tb_gjgyList = myMapper.getAllGJGYServiceVersion(type);
        return tb_gjgyList;
    }

    @RequestMapping(value = "getLastUpdateKFYQService", produces = "application/json;charset=utf-8")
    public TB_KFYQ getLastUpdateKFYQService(int type) {//取得最后一次更新的地类图斑服务
        TB_KFYQ tb_kfyq = myMapper.getLastUpdateKFYQService(type);
        return tb_kfyq;
    }

    @RequestMapping(value = "getKFYQServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_KFYQ getKFYQServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_KFYQ tb_kfyq = myMapper.getKFYQServiceByUpdatetime(updatetime, type);
        return tb_kfyq;
    }

    @RequestMapping(value = "getAllKFYQServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_KFYQ> getAllKFYQServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_KFYQ> tb_kfyqList = myMapper.getAllKFYQServiceVersion(type);
        return tb_kfyqList;
    }


    @RequestMapping(value = "getLastUpdateLSYDService", produces = "application/json;charset=utf-8")
    public TB_LSYD getLastUpdateLSYDService(int type) {//取得最后一次更新的地类图斑服务
        TB_LSYD tb_lsyd = myMapper.getLastUpdateLSYDService(type);
        return tb_lsyd;
    }

    @RequestMapping(value = "getLSYDServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_LSYD getLSYDServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_LSYD tb_lsyd = myMapper.getLSYDServiceByUpdatetime(updatetime, type);
        return tb_lsyd;
    }

    @RequestMapping(value = "getAllLSYDServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_LSYD> getAllLSYDServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_LSYD> tb_lsydList = myMapper.getAllLSYDServiceVersion(type);
        return tb_lsydList;
    }


    @RequestMapping(value = "getLastUpdatePDTService", produces = "application/json;charset=utf-8")
    public TB_PDT getLastUpdatePDTService(int type) {//取得最后一次更新的地类图斑服务
        TB_PDT tb_pdt = myMapper.getLastUpdatePDTService(type);
        return tb_pdt;
    }

    @RequestMapping(value = "getPDTServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_PDT getPDTServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_PDT tb_pdt = myMapper.getPDTServiceByUpdatetime(updatetime, type);
        return tb_pdt;
    }

    @RequestMapping(value = "getAllPDTServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_PDT> getAllPDTServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_PDT> tb_pdtList = myMapper.getAllPDTServiceVersion(type);
        return tb_pdtList;
    }


    @RequestMapping(value = "getLastUpdateSDGYService", produces = "application/json;charset=utf-8")
    public TB_SDGY getLastUpdateSDGYService(int type) {//取得最后一次更新的地类图斑服务
        TB_SDGY tb_sdgy = myMapper.getLastUpdateSDGYService(type);
        return tb_sdgy;
    }

    @RequestMapping(value = "getSDGYServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_SDGY getSDGYServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_SDGY tb_sdgy = myMapper.getSDGYServiceByUpdatetime(updatetime, type);
        return tb_sdgy;
    }

    @RequestMapping(value = "getAllSDGYServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_SDGY> getAllSDGYServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_SDGY> tb_sdgyList = myMapper.getAllSDGYServiceVersion(type);
        return tb_sdgyList;
    }


    @RequestMapping(value = "getLastUpdateSLGYService", produces = "application/json;charset=utf-8")
    public TB_SLGY getLastUpdateSLGYService(int type) {//取得最后一次更新的地类图斑服务
        TB_SLGY tb_slgy = myMapper.getLastUpdateSLGYService(type);
        return tb_slgy;
    }

    @RequestMapping(value = "getSLGYServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_SLGY getSLGYServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_SLGY tb_slgy = myMapper.getSLGYServiceByUpdatetime(updatetime, type);
        return tb_slgy;
    }

    @RequestMapping(value = "getAllSLGYServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_SLGY> getAllSLGYServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_SLGY> tb_slgyList = myMapper.getAllSLGYServiceVersion(type);
        return tb_slgyList;
    }


    @RequestMapping(value = "getLastUpdateSTBHHXService", produces = "application/json;charset=utf-8")
    public TB_STBHHX getLastUpdateSTBHHXService(int type) {//取得最后一次更新的地类图斑服务
        TB_STBHHX tb_stbhhx = myMapper.getLastUpdateSTBHHXService(type);
        return tb_stbhhx;
    }

    @RequestMapping(value = "getSTBHHXServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_STBHHX getSTBHHXServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_STBHHX tb_stbhhx = myMapper.getSTBHHXServiceByUpdatetime(updatetime, type);
        return tb_stbhhx;
    }

    @RequestMapping(value = "getAllSTBHHXServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_STBHHX> getAllSTBHHXServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_STBHHX> tb_stbhhxList = myMapper.getAllSTBHHXServiceVersion(type);
        return tb_stbhhxList;
    }



    @RequestMapping(value = "getLastUpdateTTQService", produces = "application/json;charset=utf-8")
    public TB_TTQ getLastUpdateTTQService(int type) {//取得最后一次更新的地类图斑服务
        TB_TTQ tb_ttq = myMapper.getLastUpdateTTQService(type);
        return tb_ttq;
    }

    @RequestMapping(value = "getTTQServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_TTQ getTTQServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_TTQ tb_ttq = myMapper.getTTQServiceByUpdatetime(updatetime, type);
        return tb_ttq;
    }

    @RequestMapping(value = "getAllTTQServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_TTQ> getAllTTQServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_TTQ> tb_ttqList = myMapper.getAllTTQServiceVersion(type);
        return tb_ttqList;
    }


    @RequestMapping(value = "getLastUpdateXZQJXService", produces = "application/json;charset=utf-8")
    public TB_XZQJX getLastUpdateXZQJXService(int type) {//取得最后一次更新的地类图斑服务
        TB_XZQJX tb_xzqjx = myMapper.getLastUpdateXZQJXService(type);
        return tb_xzqjx;
    }

    @RequestMapping(value = "getXZQJXServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_XZQJX getXZQJXServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_XZQJX tb_xzqjx = myMapper.getXZQJXServiceByUpdatetime(updatetime, type);
        return tb_xzqjx;
    }

    @RequestMapping(value = "getAllXZQJXServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_XZQJX> getAllXZQJXServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_XZQJX> tb_xzqjxList = myMapper.getAllXZQJXServiceVersion(type);
        return tb_xzqjxList;
    }


    @RequestMapping(value = "getLastUpdateYJJBNTService", produces = "application/json;charset=utf-8")
    public TB_YJJBNT getLastUpdateYJJBNTService(int type) {//取得最后一次更新的地类图斑服务
        TB_YJJBNT tb_yjjbnt = myMapper.getLastUpdateYJJBNTService(type);
        return tb_yjjbnt;
    }

    @RequestMapping(value = "getYJJBNTServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_YJJBNT getYJJBNTServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_YJJBNT tb_yjjbnt = myMapper.getYJJBNTServiceByUpdatetime(updatetime, type);
        return tb_yjjbnt;
    }

    @RequestMapping(value = "getAllYJJBNTServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_YJJBNT> getAllYJJBNTServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_YJJBNT> tb_yjjbntList = myMapper.getAllYJJBNTServiceVersion(type);
        return tb_yjjbntList;
    }


    @RequestMapping(value = "getLastUpdateZRBHQService", produces = "application/json;charset=utf-8")
    public TB_ZRBHQ getLastUpdateZRBHQService(int type) {//取得最后一次更新的地类图斑服务
        TB_ZRBHQ tb_zrbhq = myMapper.getLastUpdateZRBHQService(type);
        return tb_zrbhq;
    }

    @RequestMapping(value = "getZRBHQServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_ZRBHQ getZRBHQServiceByUpdatetime(String updatetime, int type) {//根据更新时间取得地类图斑服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_ZRBHQ tb_zrbhq = myMapper.getZRBHQServiceByUpdatetime(updatetime, type);
        return tb_zrbhq;
    }

    @RequestMapping(value = "getAllZRBHQServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_ZRBHQ> getAllZRBHQServiceVersion(int type) {//取得所有地类图斑服务版本日期
        List<TB_ZRBHQ> tb_zrbhqList = myMapper.getAllZRBHQServiceVersion(type);
        return tb_zrbhqList;
    }








    @RequestMapping(value = "getLastUpdatephysicstableService", produces = "application/json;charset=utf-8")
    public TB_PHYSICSTABLE getLastUpdatephysicstableService(int type, String physicstable) {//取得最后一次更新的xx服务
        TB_PHYSICSTABLE tb_physicstable = myMapper.getLastUpdatephysicstableService(type, physicstable);
        return tb_physicstable;
    }

    @RequestMapping(value = "getPhysicsServiceByUpdatetime", produces = "application/json;charset=utf-8")
    public TB_PHYSICSTABLE getPhysicsServiceByUpdatetime(String updatetime, int type,String physicstable) {//根据更新时间取得xx服务
        if(null==updatetime || "".equals(updatetime)) return null;

        TB_PHYSICSTABLE tb_physicstable = myMapper.getPhysicsServiceByUpdatetime(updatetime, type,physicstable);
        return tb_physicstable;
    }
    @RequestMapping(value = "getAllPhysicsServiceVersion", produces = "application/json;charset=utf-8")
    public List<TB_PHYSICSTABLE> getAllPhysicsServiceVersion(int type,String physicstable) {//取得所有xx服务版本日期
        List<TB_PHYSICSTABLE> tb_physicstableList = myMapper.getAllPhysicsServiceVersion(type,physicstable);
        return tb_physicstableList;
    }




}
