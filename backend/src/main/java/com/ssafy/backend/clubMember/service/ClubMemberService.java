package com.ssafy.backend.clubMember.service;

import com.ssafy.backend.clubMember.dto.request.RequestClubMemberApplyAcceptDto;
import com.ssafy.backend.clubMember.dto.request.RequestClubMemberApplyDto;
import com.ssafy.backend.clubMember.dto.request.RequestClubMemberApplyRejectDto;
import com.ssafy.backend.clubMember.dto.response.ResponseClubMemberApplyAcceptDto;
import com.ssafy.backend.clubMember.dto.response.ResponseClubMemberApplyDto;
import com.ssafy.backend.clubMember.dto.response.ResponseClubMemberApplyListDto;
import com.ssafy.backend.clubMember.dto.response.ResponseClubMemberApplyRejectDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface ClubMemberService {
    ResponseClubMemberApplyDto clubMemberApply(RequestClubMemberApplyDto requestClubMemberApplyDto, HttpServletRequest httpServletRequest);

    ResponseClubMemberApplyDto clubMemberApplyCancel(RequestClubMemberApplyDto requestClubMemberApplyDto, HttpServletRequest httpServletRequest);

    ResponseClubMemberApplyListDto clubMemberApplyList(Long clubSeq, HttpServletRequest httpServletRequest);

    ResponseClubMemberApplyAcceptDto clubMemberApplyAccept(RequestClubMemberApplyAcceptDto requestClubMemberApplyAcceptDto, HttpServletRequest httpServletRequest);

    ResponseEntity<ResponseClubMemberApplyRejectDto> clubMemberApplyReject(RequestClubMemberApplyRejectDto requestClubMemberApplyRejectDto, HttpServletRequest httpServletRequest);
}
