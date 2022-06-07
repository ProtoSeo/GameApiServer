package com.example.member.service

import com.example.member.dto.MemberLoginResponse
import com.example.member.dto.MemberRequest
import com.example.member.exception.MemberException
import com.example.member.exception.MemberExceptionType.*
import com.example.member.repository.MemberRepository
import com.example.membercharacter.repository.MemberCharacterRepository
import com.example.memberitem.repository.MemberItemRepository
import com.example.utils.JwtProvider
import java.util.*

class MemberService(
    private val memberRepository: MemberRepository,
    private val memberItemRepository: MemberItemRepository,
    private val memberCharacterRepository: MemberCharacterRepository
) {
    private val base64Encoder = Base64.getEncoder()

    fun register(request: MemberRequest): Long {
        if (isDuplicateEmail(request.email)) {
            throw MemberException(DUPLICATE_EMAIL)
        }
        val memberId =
            memberRepository.save(request.copy(password = String(base64Encoder.encode(request.password.toByteArray()))))
        memberItemRepository.saveMemberItems(memberId)
        memberCharacterRepository.saveMemberCharacters(memberId)
        return memberId
    }

    fun login(request: MemberRequest): MemberLoginResponse {
        val member = memberRepository.findByEmail(request.email) ?: throw MemberException(NOT_FOUND)
        if (member.password == String(base64Encoder.encode(request.password.toByteArray()))) {
            return MemberLoginResponse(member.email, JwtProvider.createJWT(member))
        }
        throw MemberException(BAD_REQUEST)
    }

    fun isDuplicateEmail(email: String): Boolean = memberRepository.existsMemberByEmail(email)
}