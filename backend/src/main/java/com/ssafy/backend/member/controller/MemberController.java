package com.ssafy.backend.member.controller;

import com.ssafy.backend.member.dto.request.RequestCheckIdDto;
import com.ssafy.backend.member.dto.request.RequestCheckNicknameDto;
import com.ssafy.backend.member.dto.request.RequestLocalLoginDto;
import com.ssafy.backend.member.dto.request.RequestLocalSignupDto;
import com.ssafy.backend.member.dto.response.ResponseCheckIdDto;
import com.ssafy.backend.member.dto.response.ResponseCheckNicknameDto;
import com.ssafy.backend.member.dto.response.ResponseLocalSignupDto;
import com.ssafy.backend.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/check-id")
    public ResponseEntity<ResponseCheckIdDto> CheckId(@RequestBody RequestCheckIdDto requestCheckIdDto) {

        ResponseCheckIdDto responseCheckIdDto = new ResponseCheckIdDto();

        if (memberService.checkId(requestCheckIdDto)) {
            responseCheckIdDto.setMessage("중복된 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseCheckIdDto);
        } else {
            responseCheckIdDto.setMessage("OK");
            return ResponseEntity.status(HttpStatus.OK).body(responseCheckIdDto);
        }
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<ResponseCheckNicknameDto> CheckNickname(@RequestBody RequestCheckNicknameDto requestCheckNicknameDto) {

        ResponseCheckNicknameDto responseCheckNicknameDto = new ResponseCheckNicknameDto();

        if (memberService.checkNickname(requestCheckNicknameDto)) {
            responseCheckNicknameDto.setMessage("중복된 닉네임입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseCheckNicknameDto);
        } else {
            responseCheckNicknameDto.setMessage("OK");
            return ResponseEntity.status(HttpStatus.OK).body(responseCheckNicknameDto);
        }
    }

    @PostMapping("/local-signup")
    public ResponseEntity<ResponseLocalSignupDto> localSignup(
            RequestLocalSignupDto requestLocalSignupDto) throws IOException, NoSuchAlgorithmException {

        ResponseLocalSignupDto responseLocalSignupDto = new ResponseLocalSignupDto();

        if (memberService.localSignup(requestLocalSignupDto)) {
            responseLocalSignupDto.setMessage("OK");
            return ResponseEntity.status(HttpStatus.OK).body(responseLocalSignupDto);

        } else {
            responseLocalSignupDto.setMessage("회원가입 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseLocalSignupDto);

        }

    }

    @PostMapping("/local-login")
    public ResponseEntity<?> localLogin(@RequestBody RequestLocalLoginDto loginDto) throws NoSuchAlgorithmException {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> data = memberService.localLogin(loginDto);

            HttpHeaders headers = new HttpHeaders();

            headers.add("atk", (String) data.get("atk"));
            data.remove("atk");
            headers.add("rtk", (String) data.get("rtk"));
            data.remove("rtk");

            resultMap.put("data", data);
            resultMap.put("message", "OK");
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resultMap);

        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();

        String msg = (String) request.getAttribute("message");
        if (msg == null) {
            Long seq = (Long) request.getAttribute("seq");

            memberService.logout(seq);
            resultMap.put("message", "OK");

            return ResponseEntity.status(HttpStatus.OK).body(resultMap);
        } else {
            resultMap.put("message", msg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) { // rtk만 갖고온 사람한테 atk 재발급해줘야됨
        Map<String, String> resultMap = new HashMap<>();

        String msg = (String) request.getAttribute("message");

        if (msg == null) {
            Long seq = (Long) request.getAttribute("seq");
            try {
                Map<String, String> returnMap = memberService.reissue(seq);
                HttpHeaders headers = new HttpHeaders();

                headers.add("atk", returnMap.get("atk"));
                returnMap.remove("atk");
                headers.add("rtk", returnMap.get("rtk"));
                returnMap.remove("rtk");

                resultMap.put("message", "OK");

                return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resultMap);
            } catch (Exception e) {
                resultMap.put("message", "세션이 만료되었습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
            }
        } else {
            resultMap.put("message", msg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }
    }

}
