package com.koreait.vehicleservice.center;


import com.koreait.vehicleservice.MyUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired private BoardMapper mapper;
    @Autowired private MyUserUtils userUtils;

    //게시글 등록
    public int insBoard(BoardEntity boardEntity){
        boardEntity.setWriteriuser(userUtils.getLoginUserPk());
        return mapper.insBoard(boardEntity);
    }

    //게시글(공지) 등록
    public int insNoticeBoard(NoticeBoardEntity noticeBoardEntity){
        noticeBoardEntity.setWriteriuser(userUtils.getLoginUserPk());
        return mapper.insNoticeBoard(noticeBoardEntity);
    }


    //답변 등룍
    public int insCmtBoard(BoardCmtEntity cmtEntity){
        cmtEntity.setWriterNm(userUtils.getLoginUser().getNm());
        mapper.modBoardIsAnw(cmtEntity);
        return mapper.insCmtBoard(cmtEntity);
    }


    public List<BoardEntity> selBoardList(BoardDto dto){
        if(dto.getCurrentPage() > 0) {
            int startIdx = (dto.getCurrentPage() - 1) * dto.getRecordCount();
            if (startIdx < 0) {
                startIdx = 0;
            }
            dto.setStartIdx(startIdx);
            return mapper.selBoardList(dto); //질문게시판 리스트
        }
        return mapper.selHomeBoardList(); //home에서 뿌려주는 질문게시판 리스트
    }

    public List<NoticeBoardEntity> selNoticeBoardList(NoticeBoardDto dto){
        if(dto.getCurrentPage() > 0) {
            int startIdx = (dto.getCurrentPage() - 1) * dto.getRecordCount();
            if (startIdx < 0) {
                startIdx = 0;
            }
            dto.setStartIdx(startIdx);
            return mapper.selNoticeBoardList(dto); //공지게시판 리스트
        }
        return mapper.selNoticeHomeBoardList(); //home에서 뿌려주는 공지게시판 리스트
    }

    //게시글 디테일 정보
    public BoardVo selBoard(int quesiboard){
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setQuesiboard(quesiboard);
        BoardVo detail = mapper.selBoard(boardEntity);
        int hitsResult = mapper.addHits(boardEntity);
        if(hitsResult == 1){ //detail로 들어갔을때 올려진 조회수가 바로 보이게하기위해
            detail.setHits(detail.getHits() + 1);
        }

        return detail;
    }

    //공지게시글 디테일 정보
    public NoticeBoardVo selNoticeBoard(int iboard){
        NoticeBoardEntity entity = new NoticeBoardEntity();
        entity.setIboard(iboard);
        NoticeBoardVo detail = mapper.selNoticeBoard(entity);
        int hitsResult = mapper.addNoticeHits(entity);
        if(hitsResult == 1){ //detail로 들어갔을때 올려진 hits가 바로보이게하기위해
            detail.setHits(detail.getHits() + 1);
        }
        return detail;
    }

    //답변 디테일 정보
    public BoardCmtEntity selCmtBoard(int quesiboard){
        BoardCmtEntity entity = new BoardCmtEntity();
        entity.setQuesiboard(quesiboard);
        return mapper.selCmtBoard(entity);
    }

    //이전글 다음글
    public BoardPrevNextVo selPrevNext(BoardVo vo){
        return mapper.selPrevNext(vo);
    }

    //질문게시판 최대페이지수 구하기
    public ResultVo selMaxPageVal(BoardDto dto){
        return mapper.selMaxPageVal(dto);
    }

    //공지게시판 최대페이지수 구하기
    public ResultVo selMaxPageVal2(NoticeBoardDto dto){
        return mapper.selMaxPageVal2(dto);
    }

    //게시글 수정
    public int modBoard(BoardEntity boardEntity){
        boardEntity.setWriteriuser(userUtils.getLoginUserPk());
        return mapper.modBoard(boardEntity);
    }

    //게시글 삭제
    public void delBoard(int quesiboard){
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setQuesiboard(quesiboard);
        boardEntity.setWriteriuser(userUtils.getLoginUserPk());
        mapper.delBoard(boardEntity);
    }
}
