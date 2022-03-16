package com.koreait.vehicleservice.vehicle;

import com.koreait.vehicleservice.MyFileUtils;
import com.koreait.vehicleservice.MyUserUtils;
import com.koreait.vehicleservice.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleService {
    @Autowired
    private VehicleMapper mapper;
    @Autowired
    private MyFileUtils fileUtils;
    @Autowired
    private MyUserUtils myUserUtils;
    @Autowired
    MyUserUtils userUtils;

    public int inVehicle(VehicleEntity entity){ //차량등록
        entity.setWriteriuser(userUtils.getLoginUserPk());
        return mapper.inVehicle(entity);
    }


    public String uploadMainImg(MultipartFile mf){  //메인 이미지 저장
        if(mf==null){return null;}
        var iboard = mapper.selMaxiboard();

        final String PATH = "D:/upload/images/vehicle/"+iboard;
        fileUtils.delFolderFiles(PATH,true);
        String fileNm = fileUtils.saveFile(PATH,mf);
        if(fileNm == null){return null;}

        CarImageEntity entity = new CarImageEntity();

        //파일명을 db 에 저장하기
        entity.setSelliboard(iboard);
        entity.setMainimg(fileNm);
        mapper.inCarimg(entity);

        return fileNm;
    }

    public String uploadSubImg(List<MultipartFile> mhsr){ //서브이미지 저장
        var iboard = mapper.selMaxiboard();
        final String PATH = "D:/upload/images/vehicle/"+iboard+"/sub";
        fileUtils.delFolderFiles(PATH,true);
        for (MultipartFile mf : mhsr) {
            if(mf==null||mf.getSize()==0){return null;}
            String fileNm = fileUtils.saveFile(PATH,mf);
            if(fileNm == null){return null;}
        }
        return "";
    }

    public int inOptions(VehicleDto dto){
        String[] options = dto.getChecked_option();
        CarOption car_option = new CarOption();
        var iboard = mapper.selMaxiboard();
        car_option.setSelliboard(iboard);
        for(int i=0; i<options.length; i++){
            switch (options[i]){
                case "aircon" :car_option.setAircon(1);
                    break;
                case "smart_key":car_option.setSmart_key(1);
                    break;
                case "camera":car_option.setCamera(1);
                    break;
                case "hi_pass":car_option.setHi_pass(1);
                    break;
                case "navigation":car_option.setNavigation(1);
                    break;
                case "bluetooth":car_option.setBluetooth(1);
                    break;
            }
        }
        return mapper.inOptions(car_option);
    }

    public int inExplanation(VehicleDto dto){
        String[] explanation = dto.getExplanations();
        CarExplanationEntity car_explanation = new CarExplanationEntity();
        var iboard = mapper.selMaxiboard();
        car_explanation.setSelliboard(iboard);
        car_explanation.setCar_state(explanation[0]);
        car_explanation.setAccident_state(explanation[1]);
        car_explanation.setManage_state(explanation[2]);
        car_explanation.setInout_inform(explanation[3]);
        car_explanation.setTuning_inform(explanation[4]);
        car_explanation.setOther(explanation[5]);

        return mapper.inExplanation(car_explanation);
    }

    public int selCarNum(String car_num){
        VehicleEntity entity = new VehicleEntity();
        entity.setCar_number(car_num);
        VehicleEntity resultentity = mapper.selCarNum(entity);
        if(resultentity != null){
            String CarNumber = mapper.selCarNum(entity).getCar_number();
            if(CarNumber.equals(car_num)){
                return 1; //중복
            }
        }

        return 0; //사용가능 차량번호
    }


    public List<VehicleVo> vehicleList(VehicleDto dto){ //차량 리스트
        int startIdx = (dto.getCurrentPage() - 1) * dto.getRecordCount();
        if(startIdx < 0) { startIdx = 0; }
        dto.setStartIdx(startIdx);
        return mapper.vehicleList(dto);
    }

     public List<VehicleVo> vehicleList2(VehicleDto dto){ //모델명검색
         int startIdx = (dto.getCurrentPage() - 1) * dto.getRecordCount();
         if(startIdx < 0) { startIdx = 0; }
         dto.setStartIdx(startIdx);
        List<VehicleVo> list = null;
        if(dto.getSearchVal() != null){
            list = mapper.vehicleSearchList(dto);
        } else {
            list = mapper.vehicleList(dto);
        }
        return list;
       
    }

    public List<VehicleVo> searchList(ListSearchEntity list){ //사이드 검색
        int startIdx = (list.getCurrentPage() - 1) * list.getRecordCount();
        if(startIdx < 0) { startIdx = 0; }
        list.setStartIdx(startIdx);
        return mapper.vehicleSearchList2(list);
    }

    public VehicleVo vehicledetail(VehicleEntity entity){
        //세선에서 유저iboard값 받아야함
        int hisRs = mapper.hitsCount(entity);
        if(hisRs == 1){ //detail로 들어갔을때 올려진 hits가 바로보이게하기위해
            entity.setHits(entity.getHits() + 1);
        }
        VehicleVo vo= mapper.vehicledetail(entity);
        String strDirPath = "D:\\upload\\images\\vehicle\\"+entity.getSelliboard()+"\\sub";
        File file = new File(strDirPath);
        if(file.exists() && file.isDirectory()) {
            List subimg = ListFile(strDirPath);
            vo.setSubimg(subimg);
        }
        return vo;
    }


    private List<String> ListFile( String strDirPath ) {
        List list = new ArrayList<String>();
        File path = new File(strDirPath);
        File[] fList = path.listFiles();
        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isFile()) {
                list.add(fList[i].getName());
            }
        }
        return list;
    }


       

//    public List<VehicleVo> vehicleList(VehicleEntity entity){
//
//
//        final String PATH = "../img/vehicle/";
//        List<VehicleVo> list = mapper.vehicleList(entity);
//        for(int i=0; i<list.size(); i++){
//            String result =PATH + list.get(i).getSelliboard()+ "/"+list.get(i).getMainimg();
//            list.get(i).setMainimg(result);
//        }
//        return list;
//    }

    public int likes(VehicleDto dto){ //좋아요
        dto.setLikesiuser(myUserUtils.getLoginUserPk());
        mapper.likeCount(dto);
        return mapper.likes(dto);
    }
    public int dellikes(VehicleDto dto){ //좋아요 취소
        dto.setLikesiuser(myUserUtils.getLoginUserPk());
        mapper.likeMinus(dto);
        return mapper.dellikes(dto);
    }

    public int jimchk(VehicleDto dto){ //좋아요 목록
        if(myUserUtils.getLoginUser() != null){
            dto.setLikesiuser(myUserUtils.getLoginUserPk());
            return mapper.jimchk(dto);
        }
        return 2;//로그인안되어있으면 2 리턴
    }

    public VehicleDto selMaxPageVal(VehicleDto dto){ //페이징처리
        if(dto.getRoot()!= null){
        switch (dto.getRoot()){
            case "home":
                return mapper.homeMaxPageVal(dto);
            case "modelSearch":
                return mapper.searchMaxPageVal(dto);
            case "AllList":
                return mapper.selMaxPageVal(dto);
        }
        }
        return mapper.selMaxPageVal(dto);
    }


    public VehicleDto sideSearchMaxPageVal(ListSearchEntity list){ //사이드검색 페이징
        if(list.getRoot()!= null){
           if(list.getRoot().equals("sideSearch")){
               return mapper.sideSearchMaxPageVal(list);
           }
        }
        return null;
    }


    public List<VehicleVo> homeSearchList(VehicleDto dto){ //홈에서 검색
        int startIdx = (dto.getCurrentPage() - 1) * dto.getRecordCount();
        if(startIdx < 0) { startIdx = 0; }
        dto.setStartIdx(startIdx);
        return mapper.homeSearchList(dto);
    }

    //좋아요 순위 가져오기
    public List<VehicleVo> selLikeLank(){
        return mapper.selLikeLank();
    }

    //조회수 순위 가져오기
    public List<VehicleVo> selhitsLank(){
        return mapper.selhitsLank();
    }
}
